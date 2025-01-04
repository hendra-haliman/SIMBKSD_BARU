package id.go.jakarta.bksd.dokumen.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Year;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import ch.qos.logback.core.model.Model;
import id.go.jakarta.bksd.dokumen.domain.AsistenSekretarisDaerah;
import id.go.jakarta.bksd.dokumen.service.AsistenSekretarisDaerahService;
import id.go.jakarta.bksd.dokumen.service.LaporanService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/laporan")
public class LaporanController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private LaporanService laporanService;
    private AsistenSekretarisDaerahService asistenSekretarisDaerahService;

    public LaporanController(LaporanService laporanService,
            AsistenSekretarisDaerahService asistenSekretarisDaerahService) {
        this.laporanService = laporanService;
        this.asistenSekretarisDaerahService = asistenSekretarisDaerahService;
    }

    @GetMapping("")
    public String laporanHome(Model model) {
        Integer currentYear = Integer.valueOf(Year.now().getValue());

        List<AsistenSekretarisDaerah> asistenList = asistenSekretarisDaerahService.findAll();
        model.addAttribute("asistenList", asistenList);
        model.addAttribute("currentPage", "laporan");
        model.addAttribute("currentYear", currentYear);
        return "laporan";
    }

    @GetMapping("/rekap-tahunan")
    public void rekapTahunan(@RequestParam Integer tahun, HttpServletResponse response) throws IOException {
        ByteArrayInputStream isr = laporanService.rekapTahunan(tahun);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=Rekap_Dokumen_Kerja_Sama_" + tahun + ".xlsx");
        IOUtils.copy(isr, response.getOutputStream());
    }

    @GetMapping("/dokumenBelumExpired")
    public void dokumenBelumExpired(HttpServletResponse response, @RequestParam(required = false) List<Long> asisten)
            throws IOException {
        logger.info("ASISTEN_ID:::: [{}]", asisten);

        ByteArrayInputStream isr = laporanService.dokumenBelumExpired(asisten);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=Rekap_Kerjasama_Belum_Expired" + ".xlsx");
        IOUtils.copy(isr, response.getOutputStream());
    }

}
