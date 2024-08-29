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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.Kolaborasi;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.SelectedTableColumn;
import id.go.jakarta.bksd.dokumen.domain.TableDefinition;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.service.BagianService;
import id.go.jakarta.bksd.dokumen.service.KolaborasiService;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.TableDefinitionService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/kolaborasi")
public class KolaborasiController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private KolaborasiService kolaborasiService;
    private BagianService bagianService;
    private PemakaiService pemakaiService;
    private TableDefinitionService definitionService;

    public KolaborasiController(KolaborasiService kolaborasiService,
            BagianService bagianService,
            PemakaiService pemakaiService,
            TableDefinitionService definitionService) {
        this.kolaborasiService = kolaborasiService;
        this.bagianService = bagianService;
        this.pemakaiService = pemakaiService;
        this.definitionService = definitionService;
    }

    @GetMapping("")
    public String kolaborasiHome(Model model, Principal principal) throws JsonProcessingException {
        List<Kolaborasi> kolaborasiList = kolaborasiService.findAll();
        Set<TableDefinition> tableDef = definitionService.findByNamaTable("kolaborasi");

        String name = principal.getName();
        Pemakai pemakai = pemakaiService.findByUserName(name);

        List<TableDefinition> defs = pemakai.getTableDefinition().stream()
                .filter(def -> def.getNamaTable().equals("kolaborasi")).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String jsonDefs = mapper.writeValueAsString(defs);

        logger.info("JSONFEFS:::[{}]", jsonDefs);

        model.addAttribute("kolaborasiList", kolaborasiList);
        model.addAttribute("tableDef", tableDef);
        model.addAttribute("pemakai", pemakai);

        model.addAttribute("jsonDefs", jsonDefs);

        return "kolaborasi";
    }

    @PostMapping("/list")
    @ResponseBody
    public Page<Kolaborasi> list(@RequestBody PagingRequest pagingRequest) {
        Page<Kolaborasi> kolaborasiPage = kolaborasiService.getKolaborasi(pagingRequest);
        logger.info("Kolaborasi List:::{}", kolaborasiPage);
        return kolaborasiPage;
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        Kolaborasi kolaborasi = kolaborasiService.findById(id);

        model.addAttribute("kolaborasi", kolaborasi);

        return "kolaborasi-lihat";
    }

    @GetMapping("/add")
    public String add(Model model) {
        LocalDate today = LocalDate.now();
        int year = today.getYear();

        Kolaborasi kolaborasi = new Kolaborasi();
        kolaborasi.setTahun(year);
        List<Bagian> bagianList = bagianService.findAll();

        model.addAttribute("kolaborasi", kolaborasi);
        model.addAttribute("bagianList", bagianList);

        return "kolaborasi-add";
    }

    @PostMapping("/saveNew")
    public String saveNewDokumen(Model model,
            @ModelAttribute("kolaborasi") Kolaborasi kolaborasi, RedirectAttributes redirectAttributes) {

        kolaborasiService.save(kolaborasi);
        redirectAttributes.addFlashAttribute("success_msg", "Dokumen berhasil disimpan.");

        return "redirect:/kolaborasi";
    }

    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable Long id) {
        Kolaborasi kolaborasi = kolaborasiService.findById(id);

        if (kolaborasi != null) {
            kolaborasi.setDeleted(true);
            kolaborasiService.save(kolaborasi);
        }

        return "redirect:/kolaborasi";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Kolaborasi kolaborasi = kolaborasiService.findById(id);
        List<Bagian> bagianList = bagianService.findAll();

        model.addAttribute("bagianList", bagianList);

        model.addAttribute("kolaborasi", kolaborasi);

        return "kolaborasi-edit";
    }

    @PostMapping("saveEdit")
    public String saveEditDokumen(Model model,
            @ModelAttribute("kolaborasi") Kolaborasi kolaborasi,
            RedirectAttributes redirectAttributes) {

        kolaborasiService.save(kolaborasi);
        redirectAttributes.addFlashAttribute("success_msg", "Dokumen berhasil disimpan.");

        return "redirect:/kolaborasi";
    }

    @PostMapping("/exportExcel")
    public void exportExcel(@RequestBody PagingRequest pagingRequest, HttpServletResponse response)
            throws IOException {

        ByteArrayInputStream isr = kolaborasiService.exportExcel(pagingRequest);

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

        Set<TableDefinition> kerjasamaDefs = definitionService.findByNamaTable("kolaborasi");
        List<TableDefinition> defs = definitionService.findByIds(options.getSelectedOption());

        pemakai.getTableDefinition().removeAll(kerjasamaDefs);
        pemakai.getTableDefinition().addAll(defs);
        // pemakai.setTableDefinition(defs);

        pemakaiService.save(pemakai);
    }

}
