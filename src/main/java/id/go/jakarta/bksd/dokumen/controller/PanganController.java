package id.go.jakarta.bksd.dokumen.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.TableDefinition;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.pangan.BUMD;
import id.go.jakarta.bksd.dokumen.domain.pangan.Jenis;
import id.go.jakarta.bksd.dokumen.domain.pangan.Kelompok;
import id.go.jakarta.bksd.dokumen.domain.pangan.KerjaSamaPenyediaanPangan;
import id.go.jakarta.bksd.dokumen.domain.pangan.KotaKabupaten;
import id.go.jakarta.bksd.dokumen.domain.pangan.Pemda;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.TableDefinitionService;
import id.go.jakarta.bksd.dokumen.service.pangan.BUMDService;
import id.go.jakarta.bksd.dokumen.service.pangan.JenisService;
import id.go.jakarta.bksd.dokumen.service.pangan.KelompokService;
import id.go.jakarta.bksd.dokumen.service.pangan.KerjaSamaPenyediaanPanganService;
import id.go.jakarta.bksd.dokumen.service.pangan.KotaKabupatenService;
import id.go.jakarta.bksd.dokumen.service.pangan.PemdaService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/pangan")
public class PanganController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private KerjaSamaPenyediaanPanganService kerjaSamaPenyediaanPanganService;
    private TableDefinitionService definitionService;
    private PemakaiService pemakaiService;
    private KelompokService kelompokService;
    private PemdaService pemdaService;
    private BUMDService bumdService;
    private KotaKabupatenService kotaKabupatenService;
    private JenisService jenisService;

    public PanganController(KerjaSamaPenyediaanPanganService kerjaSamaPenyediaanPanganService,
            TableDefinitionService definitionService,
            PemakaiService pemakaiService,
            KelompokService kelompokService,
            PemdaService pemdaService,
            BUMDService bumdService,
            KotaKabupatenService kotaKabupatenService,
            JenisService jenisService) {
        this.kerjaSamaPenyediaanPanganService = kerjaSamaPenyediaanPanganService;
        this.definitionService = definitionService;
        this.pemakaiService = pemakaiService;
        this.kelompokService = kelompokService;
        this.pemdaService = pemdaService;
        this.bumdService = bumdService;
        this.kotaKabupatenService = kotaKabupatenService;
        this.jenisService = jenisService;
    }

    @GetMapping("")
    public String panganHome(Model model, Principal principal) throws JsonProcessingException {
        // List<KerjaSamaPenyediaanPangan> kerjasamaList =
        // kerjaSamaPenyediaanPanganService.findAll();
        List<Kelompok> kelompokList = kelompokService.findAll();
        Set<TableDefinition> tableDef = definitionService.findByNamaTable("pangan");

        String name = principal.getName();
        Pemakai pemakai = pemakaiService.findByUserName(name);

        List<TableDefinition> defs = pemakai.getTableDefinition().stream()
                .filter(def -> def.getNamaTable().equals("pangan")).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String jsonDefs = mapper.writeValueAsString(defs);

        logger.info("JSONFEFS:::[{}]", jsonDefs);

        // model.addAttribute("kerjasamaList", kerjasamaList);
        model.addAttribute("kelompokList", kelompokList);
        model.addAttribute("tableDef", tableDef);
        model.addAttribute("pemakai", pemakai);
        model.addAttribute("jsonDefs", jsonDefs);
        model.addAttribute("currentPage", "pangan");

        return "pangan/pangan";
    }

    @PostMapping("/list")
    @ResponseBody
    public Page<KerjaSamaPenyediaanPangan> list(@RequestBody PagingRequest pagingRequest) {
        Page<KerjaSamaPenyediaanPangan> kerjasamaPage = kerjaSamaPenyediaanPanganService.getKerjasama(pagingRequest);
        logger.info("kerjasama pangan List:::{}", kerjasamaPage);
        return kerjasamaPage;
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        KerjaSamaPenyediaanPangan pangan = kerjaSamaPenyediaanPanganService.findById(id).get();

        model.addAttribute("pangan", pangan);
        model.addAttribute("currentPage", "pangan");

        return "pangan/pangan-lihat";
    }

    @GetMapping("/add")
    public String add(Model model) {
        LocalDate today = LocalDate.now();
        int year = today.getYear();

        KerjaSamaPenyediaanPangan pangan = new KerjaSamaPenyediaanPangan();
        pangan.setTahun(year);
        List<Pemda> pemdaList = pemdaService.findAll();
        List<BUMD> bumdList = bumdService.findAll();
        List<KotaKabupaten> kotaKabupatenList = kotaKabupatenService.findAll();
        List<Jenis> jenisList = jenisService.findAll();
        List<Kelompok> kelompokList = kelompokService.findAll();

        model.addAttribute("kerjaSamaPenyediaanPangan", pangan);
        model.addAttribute("pemdaList", pemdaList);
        model.addAttribute("bumdList", bumdList);
        model.addAttribute("kotaKabupatenList", kotaKabupatenList);
        model.addAttribute("jenisList", jenisList);
        model.addAttribute("kelompokList", kelompokList);

        model.addAttribute("currentPage", "pangan");

        return "pangan/pangan-add";
    }

    @PostMapping("/saveNew")
    public String saveNewDokumen(Model model,
            @Valid KerjaSamaPenyediaanPangan kerjaSamaPenyediaanPangan, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            List<Pemda> pemdaList = pemdaService.findAll();
            List<BUMD> bumdList = bumdService.findAll();
            List<KotaKabupaten> kotaKabupatenList = kotaKabupatenService.findAll();
            List<Jenis> jenisList = jenisService.findAll();
            List<Kelompok> kelompokList = kelompokService.findAll();
    
            model.addAttribute("pemdaList", pemdaList);
            model.addAttribute("bumdList", bumdList);
            model.addAttribute("kotaKabupatenList", kotaKabupatenList);
            model.addAttribute("jenisList", jenisList);
            model.addAttribute("kelompokList", kelompokList);
    
            model.addAttribute("currentPage", "pangan");

            return "pangan/pangan-add";
        }

        kerjaSamaPenyediaanPanganService.save(kerjaSamaPenyediaanPangan);
        redirectAttributes.addFlashAttribute("success_msg", "Dokumen berhasil disimpan.");

        return "redirect:/pangan";
    }

    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        KerjaSamaPenyediaanPangan pangan = kerjaSamaPenyediaanPanganService.findById(id).get();

        if (pangan != null) {
            pangan.setDeleted(true);
            redirectAttributes.addFlashAttribute("success_msg", "Kerja sama pangan berhasil dihapus.");
            kerjaSamaPenyediaanPanganService.save(pangan);
        }

        return "redirect:/pangan";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        KerjaSamaPenyediaanPangan pangan = kerjaSamaPenyediaanPanganService.findById(id).get();
        List<Pemda> pemdaList = pemdaService.findAll();
        List<BUMD> bumdList = bumdService.findAll();
        List<KotaKabupaten> kotaKabupatenList = kotaKabupatenService.findAll();
        List<Jenis> jenisList = jenisService.findAll();
        List<Kelompok> kelompokList = kelompokService.findAll();

        model.addAttribute("kerjaSamaPenyediaanPangan", pangan);
        model.addAttribute("pemdaList", pemdaList);
        model.addAttribute("bumdList", bumdList);
        model.addAttribute("kotaKabupatenList", kotaKabupatenList);
        model.addAttribute("jenisList", jenisList);
        model.addAttribute("kelompokList", kelompokList);

        model.addAttribute("currentPage", "pangan");

        return "pangan/pangan-edit";
    }

    @PostMapping("saveEdit")
    public String saveEditDokumen(Model model,
            @Valid KerjaSamaPenyediaanPangan kerjaSamaPenyediaanPangan, BindingResult result,
            RedirectAttributes redirectAttributes) {
                if (result.hasErrors()) {
                    List<Pemda> pemdaList = pemdaService.findAll();
                    List<BUMD> bumdList = bumdService.findAll();
                    List<KotaKabupaten> kotaKabupatenList = kotaKabupatenService.findAll();
                    List<Jenis> jenisList = jenisService.findAll();
                    List<Kelompok> kelompokList = kelompokService.findAll();
            
                    model.addAttribute("pemdaList", pemdaList);
                    model.addAttribute("bumdList", bumdList);
                    model.addAttribute("kotaKabupatenList", kotaKabupatenList);
                    model.addAttribute("jenisList", jenisList);
                    model.addAttribute("kelompokList", kelompokList);
            
                    model.addAttribute("currentPage", "pangan");
        
                    return "pangan/pangan-edit";
                }
        
        kerjaSamaPenyediaanPanganService.save(kerjaSamaPenyediaanPangan);
        redirectAttributes.addFlashAttribute("success_msg", "Dokumen berhasil disimpan.");

        return "redirect:/pangan";
    }

    @PostMapping("/exportExcel")
    public void exportExcel(@RequestBody PagingRequest pagingRequest, HttpServletResponse response)
            throws IOException {

        ByteArrayInputStream isr = kerjaSamaPenyediaanPanganService.exportExcel(pagingRequest);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=export.xlsx");
        IOUtils.copy(isr, response.getOutputStream());

    }

}
