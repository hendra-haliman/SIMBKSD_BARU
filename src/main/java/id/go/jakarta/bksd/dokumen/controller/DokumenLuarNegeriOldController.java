package id.go.jakarta.bksd.dokumen.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSamaLuarNegeriOld;
import id.go.jakarta.bksd.dokumen.domain.DokumenLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskahLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.SelectedTableColumn;
import id.go.jakarta.bksd.dokumen.domain.TableDefinition;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.DokumenKerjaSamaLuarNegeriOldDTO;
import id.go.jakarta.bksd.dokumen.service.DokumenKerjaSamaLuarNegeriOldService;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.TableDefinitionService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/dokumen_luar_negeri_old")
public class DokumenLuarNegeriOldController {
    Logger logger = LoggerFactory.getLogger(getClass());

    private DokumenKerjaSamaLuarNegeriOldService dokumenKerjaSamaLuarNegeriOldService;
    private PemakaiService pemakaiService;

    public DokumenLuarNegeriOldController(DokumenKerjaSamaLuarNegeriOldService dokumenKerjaSamaLuarNegeriOldService,
            PemakaiService pemakaiService,
            TableDefinitionService tableDefinitionService) {
        this.dokumenKerjaSamaLuarNegeriOldService = dokumenKerjaSamaLuarNegeriOldService;
        this.pemakaiService = pemakaiService;
    }

    @PostMapping("/list")
    @ResponseBody
    public Page<DokumenKerjaSamaLuarNegeriOldDTO> list(@RequestBody PagingRequest pagingRequest) {
        logger.info("Pagingrequest : [{}]", pagingRequest);
        Page<DokumenKerjaSamaLuarNegeriOldDTO> page = dokumenKerjaSamaLuarNegeriOldService.getDokumen(pagingRequest);
        return page;
    }

    @GetMapping("/view/{id}")
    public String lihat(@PathVariable Long id, Model model) {
        DokumenKerjaSamaLuarNegeriOld dok = dokumenKerjaSamaLuarNegeriOldService.findById(id).get();
        model.addAttribute("dokumen", dok);
        model.addAttribute("currentPage", "pelaksanaanLN");

        return "pelaksanaanLNOld/dokumenLNOld-lihat";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        DokumenKerjaSamaLuarNegeriOld dok = dokumenKerjaSamaLuarNegeriOldService.findById(id).get();

        model.addAttribute("dokumenLN", dok);
        model.addAttribute("currentPage", "pelaksanaanLN");

        return "pelaksanaanLNOld/dokumenLNOld-edit";
    }

    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable Long id) {
        DokumenKerjaSamaLuarNegeriOld dokumenKerjaSama = dokumenKerjaSamaLuarNegeriOldService.findById(id).get();

        if (dokumenKerjaSama != null) {
            dokumenKerjaSama.setDeleted(true);
            dokumenKerjaSamaLuarNegeriOldService.save(dokumenKerjaSama);
        }

        return "redirect:/pelaksanaan_luar_negeri";
    }

    @GetMapping("/add")
    public String showAddDocument(Model model) {
        model.addAttribute("dokumen", new DokumenKerjaSamaLuarNegeriOld());
        model.addAttribute("currentPage", "pelaksanaanLN");

        return "pelaksanaanLN/dokumenLN-add";
    }

    @PostMapping("saveNew")
    public String saveNewDokumen(@RequestParam("files") MultipartFile[] files, Model model,
            @ModelAttribute("dokumen") DokumenKerjaSamaLuarNegeriOld dokumenKerjaSama,
            RedirectAttributes redirectAttributes) {

        dokumenKerjaSamaLuarNegeriOldService.save(dokumenKerjaSama);
        redirectAttributes.addFlashAttribute("success_msg", "Dokumen berhasil disimpan.");

        return "redirect:/pelaksanaan_luar_negeri_old";
    }

    @PostMapping("saveEdit")
    public String saveEditDokumen(@RequestParam("files") MultipartFile[] files, Model model,
            @ModelAttribute("dokumenLNOld") DokumenKerjaSamaLuarNegeriOld dokumenKerjaSama,
            RedirectAttributes redirectAttributes) {

        dokumenKerjaSamaLuarNegeriOldService.save(dokumenKerjaSama);
        redirectAttributes.addFlashAttribute("success_msg", "Dokumen berhasil disimpan.");

        return "redirect:/pelaksanaan_luar_negeri_old";
    }

    @PostMapping("/exportExcel")
    public void exportExcel(@RequestBody PagingRequest pagingRequest, HttpServletResponse response)
            throws IOException {

        ByteArrayInputStream isr = dokumenKerjaSamaLuarNegeriOldService.exportExcel(pagingRequest);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=export_kerjasama_luar_negeri_old.xlsx");
        IOUtils.copy(isr, response.getOutputStream());

    }

}
