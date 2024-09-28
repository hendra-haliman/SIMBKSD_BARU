package id.go.jakarta.bksd.dokumen.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.OPD;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.Pemetaan;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.PemetaanDTO;
import id.go.jakarta.bksd.dokumen.service.BagianService;
import id.go.jakarta.bksd.dokumen.service.OPDService;
import id.go.jakarta.bksd.dokumen.service.PemetaanService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/pemetaan")
public class PemetaanController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private PemetaanService pemetaanService;
    private OPDService opdService;
    private BagianService bagianService;

    public PemetaanController(PemetaanService pemetaanService, OPDService opdService, BagianService bagianService) {
        this.pemetaanService = pemetaanService;
        this.opdService = opdService;
        this.bagianService = bagianService;
    }

    @GetMapping("")
    public String pemetaan() {
        return "pemetaan/pemetaan";
    }

    @GetMapping("/grouping")
    public String pemetaanGrouping() {
        return "pemetaan/pemetaan-group";
    }

    @PostMapping("/list")
    @ResponseBody
    public Page<PemetaanDTO> list(@RequestBody PagingRequest pagingRequest) {
        logger.info("Pagingrequest : [{}]", pagingRequest);
        logger.info("Pemetaan list start time: {}", new Date());

        Page<PemetaanDTO> page = pemetaanService.getPemetaan(pagingRequest);

        logger.info("Pemetaan list end time: {}", new Date());
        return page;
    }

    @GetMapping("/view/{id}")
    public String lihat(@PathVariable Long id, Model model) {
        Pemetaan pemetaan = pemetaanService.findById(id).get();
        model.addAttribute("pemetaan", pemetaan);
        return "pemetaan/pemetaan-lihat";
    }

    @GetMapping("/add")
    public String showAddPemetaan(Model model, @RequestParam(required = false) String mode) {
        model.addAttribute("pemetaan", new Pemetaan());
        List<OPD> opd = opdService.findAll();

        if ("group".equals(mode)) {
            model.addAttribute("mode", mode);
        }
        model.addAttribute("opdList", opd);

        return "pemetaan/pemetaan-add";
    }

    @PostMapping("saveNew")
    public String saveNewPemetaan(Model model, @ModelAttribute("pemetaan") Pemetaan pemetaan,
            @RequestParam(required = false) String mode) {

        pemetaanService.save(pemetaan);
        model.addAttribute("success_msg", "Pemetaan berhasil disimpan.");
        if ("group".equals(mode)) {
            return "pemetaan-group";
        }
        return "pemetaan/pemetaan";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model, @RequestParam(required = false) String mode) {
        Pemetaan pemetaan = pemetaanService.findById(id).get();
        List<OPD> opd = opdService.findAll();
        List<Bagian> bagianList = bagianService.findAll();

        model.addAttribute("opdList", opd);
        model.addAttribute("bagianList", bagianList);
        model.addAttribute("success_msg", "Pemetaan berhasil disimpan.");
        if ("group".equals(mode)) {
            model.addAttribute("mode", mode);
        }
        model.addAttribute("pemetaan", pemetaan);
        return "pemetaan/pemetaan-edit";
    }

    @PostMapping("saveEdit")
    public String saveEditPemetaan(Model model,
            @ModelAttribute("pemetaan") Pemetaan pemetaan, @RequestParam(required = false) String mode) {

        pemetaanService.save(pemetaan);
        model.addAttribute("success_msg", "Pemetaan berhasil disimpan.");

        if ("group".equals(mode)) {
            return "pemetaan-group";
        }
        return "pemetaan/pemetaan";
    }

    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable Long id) {
        Pemetaan pemetaan = pemetaanService.findById(id).get();

        if (pemetaan != null) {
            pemetaan.setDeleted(true);
            pemetaanService.save(pemetaan);
        }

        return "redirect:/pemetaan";
    }

    @PostMapping("/exportExcel")
    public void exportExcel(@RequestBody PagingRequest pagingRequest, HttpServletResponse response)
            throws IOException {

        ByteArrayInputStream isr = pemetaanService.exportExcel(pagingRequest);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=export.xlsx");
        IOUtils.copy(isr, response.getOutputStream());

    }

    @PostMapping("/exportExcelPerPerangkat")
    public void exportExcelPerPerangkat(@RequestBody PagingRequest pagingRequest, HttpServletResponse response)
            throws IOException {

        ByteArrayInputStream isr = pemetaanService.exportExcelPerPerangkat(pagingRequest);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=export.xlsx");
        IOUtils.copy(isr, response.getOutputStream());

    }

}
