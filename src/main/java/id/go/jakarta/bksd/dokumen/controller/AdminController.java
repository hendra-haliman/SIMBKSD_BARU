package id.go.jakarta.bksd.dokumen.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.service.BUMDPanganDataUploaderService;
import id.go.jakarta.bksd.dokumen.service.DataUploaderService;
import id.go.jakarta.bksd.dokumen.service.FileManager;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.RoleService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private PemakaiService pemakaiService;
    private DataUploaderService dataUploaderService;
    // private DevUtilityService devUtilityService;
    private FileManager fileManager;
    private BUMDPanganDataUploaderService bumdPanganDataUploaderService;

    private RoleService roleService;

    public AdminController(PemakaiService pemakaiService,
            RoleService roleService,
            DataUploaderService dataUploaderService,
            // DevUtilityService devUtilityService,
            FileManager fileManager,
            BUMDPanganDataUploaderService bumdPanganDataUploaderService) {
        this.pemakaiService = pemakaiService;
        this.roleService = roleService;
        this.dataUploaderService = dataUploaderService;
        // this.devUtilityService = devUtilityService;
        this.fileManager = fileManager;
        this.bumdPanganDataUploaderService = bumdPanganDataUploaderService;
    }

    @GetMapping("/viewUserProfile/{id}")
    public String viewUserProfile(@PathVariable Long id, Model model) {
        model.addAttribute("pemakai", pemakaiService.findById(id));
        model.addAttribute("roles", roleService.getAllRoles());
        return "profile";
    }

    @PostMapping("/addProfileImage/{id}")
    public String addProfileImage(Model model, @PathVariable("id") Long id,
            @RequestParam("profileImage") MultipartFile profileImage) throws IOException {
        byte[] bytes = profileImage.getBytes();
        Pemakai pemakai = pemakaiService.findById(id);
        pemakai.setProfileImage(bytes);
        pemakaiService.save(pemakai);
        model.addAttribute("pemakai", pemakai);
        return "profile";
    }

    @GetMapping("/viewProfileImage/{id}")
    @ResponseBody
    public byte[] getMethodName(@PathVariable Long id) {
        Pemakai pemakai = pemakaiService.findById(id);
        return pemakai.getProfileImage();
    }

    @GetMapping("/uploadKSD2023")
    @ResponseBody
    public String uploadKSD2023() {
        try {
            dataUploaderService.uploadKSD2023();
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish KSD2023";
    }

    @GetMapping("/uploadKSD2024")
    @ResponseBody
    public String uploadKSD2024() {
        try {
            dataUploaderService.uploadKSD2024();
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish KSD2024";
    }

    @GetMapping("/uploadKSD2021")
    @ResponseBody
    public String uploadKSD2021() {
        try {
            dataUploaderService.uploadKSD2021();
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish KSD2021";
    }

    @GetMapping("/uploadKSD2020")
    @ResponseBody
    public String uploadKSD2020() {
        try {
            dataUploaderService.uploadKSD2020();
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish KSD2020";
    }

    @GetMapping("/uploadKPT2022")
    @ResponseBody
    public String uploadKPT2022() {
        try {
            dataUploaderService.uploadFromWorksheet(2022, "KPT");
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish KPT2022";
    }

    @GetMapping("/uploadKDN2022")
    @ResponseBody
    public String uploadKDN2022() {
        try {
            dataUploaderService.uploadFromWorksheet(2022, "KDN");
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish KDN2022";
    }

    @GetMapping("/uploadNPBK2022")
    @ResponseBody
    public String uploadNPBK2022() {
        try {
            dataUploaderService.uploadNPBK2022();
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish NPBK2022";
    }

    @GetMapping("/uploadKLN2022")
    @ResponseBody
    public String uploadKLN2022() {
        try {
            dataUploaderService.uploadKLN2022();
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish KLN2022";
    }

    @GetMapping("/uploadPemetaan2023")
    @ResponseBody
    public String uploadPemetaan2023() {
        try {
            dataUploaderService.uploadPemetaan2023();
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish Pemetaan2023";
    }

    @GetMapping("/uploadPemetaan2024")
    @ResponseBody
    public String uploadPemetaan2024() {
        try {
            dataUploaderService.uploadPemetaan2024();
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish Pemetaan2024";
    }

    @GetMapping("/testCheckRekap2021")
    @ResponseBody
    public String testCheckRekap2021() throws IOException, InvalidFormatException {
        String folder = "D:/testrekap";
        String filename = "[2021] Rekapitulasi Data Informasi Kerja Sama Biro KSD Tahun 2021 (Update 21 November 2023).xlsx";

        dataUploaderService.testCheckRekap2021(folder + "/" + filename, folder + "/" + "report2021.txt");

        return "Finish testing Rekap 2021";
    }

    @GetMapping("/testCheckRekap2022")
    @ResponseBody
    public String testCheckRekap2022() throws IOException, InvalidFormatException {
        String folder = "D:/testrekap";
        String filename = "[2022] Rekapitulasi Data Informasi Kerja Sama Biro KSD Tahun 2022 (Update 21 November 2023).xlsx";

        dataUploaderService.testCheckRekap2022(folder + "/" + filename, folder + "/" + "report2022.txt");

        return "Finish testing Rekap 2022";
    }

    @GetMapping("/testCheckRekap2023")
    @ResponseBody
    public String testCheckRekap2023() throws IOException, InvalidFormatException {
        String folder = "D:/testrekap";
        String filename = "[2023] Rekapitulasi Data Informasi Kerja Sama Biro KSD Tahun 2023 (Update 7 Mei 2024).xlsx";

        dataUploaderService.testCheckRekap2023(folder + "/" + filename, folder + "/" + "report2023.txt");

        return "Finish testing Rekap 2023";
    }

    @GetMapping("/updateRekap2020")
    @ResponseBody
    public String updateRekap2020() throws IOException, InvalidFormatException {
        String folder = "D:/testrekap";

        dataUploaderService.updateRekap(2020, folder, "report2020.txt");

        return "Finish updating Rekap 2020";
    }

    @GetMapping("/updateRekap2021")
    @ResponseBody
    public String updateRekap2021() throws IOException, InvalidFormatException {
        String folder = "D:/testrekap";
        String filename = "[2021] Rekapitulasi Data Informasi Kerja Sama Biro KSD Tahun 2021 (Update 21 November 2023).xlsx";

        dataUploaderService.updateRekap2021(folder + "/" + filename, folder + "/" + "report2021.txt");

        return "Finish updating Rekap 2021";
    }

    @GetMapping("/updateRekap2022")
    @ResponseBody
    public String updateRekap2022() throws IOException, InvalidFormatException {
        String folder = "D:/testrekap";
        String filename = "[2022] Rekapitulasi Data Informasi Kerja Sama Biro KSD Tahun 2022 (Update 21 November 2023).xlsx";

        dataUploaderService.updateRekap2022(folder + "/" + filename, folder + "/" + "report2022.txt");

        return "Finish updating Rekap 2022";
    }

    @GetMapping("/updateRekap2023")
    @ResponseBody
    public String updateRekap2023() throws IOException, InvalidFormatException {
        String folder = "D:/testrekap";
        String filename = "[2023] Rekapitulasi Data Informasi Kerja Sama Biro KSD Tahun 2023 (Update 7 Mei 2024).xlsx";

        dataUploaderService.updateRekap2023(folder + "/" + filename, folder + "/" + "report2023.txt");

        return "Finish updating Rekap 2023";
    }

    @GetMapping("/downloadMissingFile")
    @ResponseBody
    public String downloadMissingFile() throws IOException, InvalidFormatException {

        // devUtilityService.downloadMissingFile();

        return "Finish download missing files";
    }

    /**
     * Test method to use response and write directly to it.
     * 
     * @param response
     * @throws IOException
     */
    @GetMapping("/testCommand")
    public void testCommand(HttpServletResponse response) throws IOException {
        response.reset();
        response.setHeader("Custom-Header", "foo");
        response.setStatus(200);
        response.getWriter().println("Hello World!");
        response.getWriter().println("Hello World!");
        response.getWriter().println("Hello World!");

    }

    @GetMapping("/uploadPangan")
    @ResponseBody
    public String uploadPangan() {
        try {
            bumdPanganDataUploaderService.uploadDataPangan();
        } catch (IOException | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return "finish uploadPangan";
    }

    @GetMapping("/testReport")
    public ResponseEntity<byte[]> testReport() throws GeneralSecurityException, IOException, JRException {
        List<Pemakai> pemakais = pemakaiService.findAll();
        JRBeanCollectionDataSource datasrc = new JRBeanCollectionDataSource(pemakais);
        JasperPrint jasperPrint = JasperFillManager.fillReport("D:/testrekap/report.jasper", new HashMap<>(), datasrc);
        byte[] report = JasperExportManager.exportReportToPdf(jasperPrint);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(report);
    }

}
