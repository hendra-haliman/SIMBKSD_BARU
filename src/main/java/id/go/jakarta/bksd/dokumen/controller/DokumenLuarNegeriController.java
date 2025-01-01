package id.go.jakarta.bksd.dokumen.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSamaLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.DokumenLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskahLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.SelectedTableColumn;
import id.go.jakarta.bksd.dokumen.domain.TableDefinition;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.DokumenKerjaSamaLuarNegeriDTO;
import id.go.jakarta.bksd.dokumen.service.DokumenKerjaSamaLuarNegeriService;
import id.go.jakarta.bksd.dokumen.service.FileReadWriteService;
import id.go.jakarta.bksd.dokumen.service.JenisNaskahLuarNegeriService;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.TableDefinitionService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/dokumen_luar_negeri")
public class DokumenLuarNegeriController {
    Logger logger = LoggerFactory.getLogger(getClass());

    private DokumenKerjaSamaLuarNegeriService dokumenKerjaSamaLuarNegeriService;
    private JenisNaskahLuarNegeriService jenisNaskahLuarNegeriService;
    private FileReadWriteService fileReadWriteService;
    private PemakaiService pemakaiService;
    private TableDefinitionService tableDefinitionService;

    public DokumenLuarNegeriController(DokumenKerjaSamaLuarNegeriService dokumenKerjaSamaLuarNegeriService,
            JenisNaskahLuarNegeriService jenisNaskahLuarNegeriService,
            FileReadWriteService fileReadWriteService,
            PemakaiService pemakaiService,
            TableDefinitionService tableDefinitionService) {
        this.dokumenKerjaSamaLuarNegeriService = dokumenKerjaSamaLuarNegeriService;
        this.jenisNaskahLuarNegeriService = jenisNaskahLuarNegeriService;
        this.fileReadWriteService = fileReadWriteService;
        this.pemakaiService = pemakaiService;
        this.tableDefinitionService = tableDefinitionService;
    }

    @PostMapping("/list")
    @ResponseBody
    public Page<DokumenKerjaSamaLuarNegeriDTO> list(@RequestBody PagingRequest pagingRequest) {
        logger.info("Pagingrequest : [{}]", pagingRequest);
        Page<DokumenKerjaSamaLuarNegeriDTO> page = dokumenKerjaSamaLuarNegeriService.getDokumen(pagingRequest);
        return page;
    }

    @GetMapping("/view/{id}")
    public String lihat(@PathVariable Long id, Model model) {
        DokumenKerjaSamaLuarNegeri dok = dokumenKerjaSamaLuarNegeriService.findById(id).get();
        model.addAttribute("dokumen", dok);
        model.addAttribute("currentPage", "pelaksanaanLN");

        return "pelaksanaanLN/dokumenLN-lihat";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        DokumenKerjaSamaLuarNegeri dok = dokumenKerjaSamaLuarNegeriService.findById(id).get();
        List<JenisNaskahLuarNegeri> jenisNaskahList = jenisNaskahLuarNegeriService.findAll();

        model.addAttribute("jenisNaskahList", jenisNaskahList);
        model.addAttribute("dokumenKerjaSamaLuarNegeri", dok);
        model.addAttribute("currentPage", "pelaksanaanLN");

        return "pelaksanaanLN/dokumenLN-edit";
    }

    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable Long id) {
        DokumenKerjaSamaLuarNegeri dokumenKerjaSama = dokumenKerjaSamaLuarNegeriService.findById(id).get();

        if (dokumenKerjaSama != null) {
            dokumenKerjaSama.setDeleted(true);
            dokumenKerjaSamaLuarNegeriService.save(dokumenKerjaSama);
        }

        return "redirect:/pelaksanaan_luar_negeri";
    }

    @GetMapping("/add")
    public String showAddDocument(Model model) {
        int currentYear = Year.now().getValue();
        DokumenKerjaSamaLuarNegeri dok = new DokumenKerjaSamaLuarNegeri();
        dok.setTahun(currentYear);
        model.addAttribute("dokumenKerjaSamaLuarNegeri", dok);
        List<JenisNaskahLuarNegeri> jenisNaskah = jenisNaskahLuarNegeriService.findAll();
        model.addAttribute("jenisNaskahList", jenisNaskah);
        model.addAttribute("currentPage", "pelaksanaanLN");

        return "pelaksanaanLN/dokumenLN-add";
    }

    @PostMapping("saveNew")
    public String saveNewDokumen(@RequestParam("files") MultipartFile[] files, Model model, @Valid DokumenKerjaSamaLuarNegeri dokumenKerjaSamaLuarNegeri, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<JenisNaskahLuarNegeri> jenisNaskah = jenisNaskahLuarNegeriService.findAll();
            model.addAttribute("jenisNaskahList", jenisNaskah);
            model.addAttribute("currentPage", "pelaksanaanLN");
    
            return "pelaksanaanLN/dokumenLN-add";
        }        


        for (MultipartFile file : files) {
            if (!file.getOriginalFilename().isEmpty()) {

                try {
                    fileReadWriteService.saveFileNoHash(file);
                    DokumenLuarNegeri dokumen = new DokumenLuarNegeri();
                    dokumen.setFilename(file.getOriginalFilename());

                    dokumenKerjaSamaLuarNegeri.getDokumen().add(dokumen);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        dokumenKerjaSamaLuarNegeriService.save(dokumenKerjaSamaLuarNegeri);
        redirectAttributes.addFlashAttribute("success_msg", "Dokumen berhasil disimpan.");

        return "redirect:/pelaksanaan_luar_negeri";
    }

    @PostMapping("saveEdit")
    public String saveEditDokumen(@RequestParam("files") MultipartFile[] files, Model model,
            @Valid DokumenKerjaSamaLuarNegeri dokumenKerjaSamaLuarNegeri, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<JenisNaskahLuarNegeri> jenisNaskah = jenisNaskahLuarNegeriService.findAll();
            model.addAttribute("jenisNaskahList", jenisNaskah);
            model.addAttribute("currentPage", "pelaksanaanLN");
    
            return "pelaksanaanLN/dokumenLN-edit";
        }                

        for (MultipartFile file : files) {
            if (!file.getOriginalFilename().isEmpty()) {

                try {
                    fileReadWriteService.saveFileNoHash(file);
                    DokumenLuarNegeri dokumen = new DokumenLuarNegeri();
                    dokumen.setFilename(file.getOriginalFilename());

                    dokumenKerjaSamaLuarNegeri.getDokumen().add(dokumen);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        dokumenKerjaSamaLuarNegeriService.save(dokumenKerjaSamaLuarNegeri);
        redirectAttributes.addFlashAttribute("success_msg", "Dokumen berhasil disimpan.");

        return "redirect:/pelaksanaan_luar_negeri";
    }

    @PostMapping("/exportExcel")
    public void exportExcel(@RequestBody PagingRequest pagingRequest, HttpServletResponse response)
            throws IOException {

        ByteArrayInputStream isr = dokumenKerjaSamaLuarNegeriService.exportExcel(pagingRequest);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=export.xlsx");
        IOUtils.copy(isr, response.getOutputStream());

    }

    @PostMapping("/saveTableOption")
    @ResponseStatus(HttpStatus.OK)
    public void saveTableOption(@RequestBody SelectedTableColumn options,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Selected :[{}]", options);
        // return ResponseEntity.ok("Received");
        String userName = userDetails.getUsername();
        logger.info("Username : [{}]", userName);
        Pemakai pemakai = pemakaiService.findByUserName(userName);

        Set<TableDefinition> kerjasamaLNDefs = tableDefinitionService.findByNamaTable("kerjasamaLN");
        List<TableDefinition> defs = tableDefinitionService.findByIds(options.getSelectedOption());

        pemakai.getTableDefinition().removeAll(kerjasamaLNDefs);
        pemakai.getTableDefinition().addAll(defs);
        // pemakai.setTableDefinition(defs);

        pemakaiService.save(pemakai);
    }

}
