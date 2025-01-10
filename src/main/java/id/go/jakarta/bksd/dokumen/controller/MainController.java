package id.go.jakarta.bksd.dokumen.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.apiclub.captcha.Captcha;
import id.go.jakarta.bksd.dokumen.captcha.CaptchaUtils;
import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.Rekapitulasi;
import id.go.jakarta.bksd.dokumen.domain.Role;
import id.go.jakarta.bksd.dokumen.domain.dto.ChangePasswordDTO;
import id.go.jakarta.bksd.dokumen.service.BagianService;
import id.go.jakarta.bksd.dokumen.service.DataUploaderService;
import id.go.jakarta.bksd.dokumen.service.DokumenKerjaSamaLuarNegeriService;
import id.go.jakarta.bksd.dokumen.service.DokumenKerjaSamaService;
import id.go.jakarta.bksd.dokumen.service.EmailService;
import id.go.jakarta.bksd.dokumen.service.FileManager;
import id.go.jakarta.bksd.dokumen.service.LaporanService;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.RoleService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HttpSession httpSession;

    private PemakaiService pemakaiService;
    private EmailService emailService;
    private PasswordEncoder passwordEncoder;
    private DokumenKerjaSamaService dokumenKerjaSamaService;
    private BagianService bagianService;
    private DokumenKerjaSamaLuarNegeriService dokumenKerjaSamaLuarNegeriService;
    private LaporanService laporanService;
    private RoleService roleService;

    // @Autowired
    // private String profileLogo;

    @Value("${useRecaptcha}")
    private boolean useRecaptcha;

    @Value("${enable.kolaborasi}")
    private boolean enableKolaborasi;

    public MainController(PemakaiService pemakaiService, EmailService emailService, PasswordEncoder passwordEncoder,
            DataUploaderService dataUploaderService, FileManager fileManager,
            DokumenKerjaSamaService dokumenKerjaSamaService, BagianService bagianService,
            DokumenKerjaSamaLuarNegeriService dokumenKerjaSamaLuarNegeriService,
            LaporanService laporanService,
            RoleService roleService) {
        this.pemakaiService = pemakaiService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.dokumenKerjaSamaService = dokumenKerjaSamaService;
        this.bagianService = bagianService;
        this.dokumenKerjaSamaLuarNegeriService = dokumenKerjaSamaLuarNegeriService;
        this.laporanService = laporanService;
        this.roleService = roleService;
    }

    @GetMapping("")
    public String home(Model model) {
        model.addAttribute("currentPage", "home");
        model.addAttribute("enableKolaborasi", enableKolaborasi);
       // model.addAttribute("profileLogo", profileLogo);
        return "home";
    }

    @GetMapping("/home")
    public String home2(Model model) {
        model.addAttribute("currentPage", "home");
        model.addAttribute("enableKolaborasi", enableKolaborasi);
        //model.addAttribute("profileLogo", profileLogo);
        return "home";
    }

    @GetMapping("/agendaKota") 
    public String agendaKota(Model model) {
        model.addAttribute("currentPage", "agendakota");
        model.addAttribute("enableKolaborasi", enableKolaborasi);
        return "agenda-kota";
    }

    @GetMapping("/terminologi")
    public String terminologi(Model model) {
        model.addAttribute("currentPage", "terminologi");
        return "terminologi";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,  @AuthenticationPrincipal UserDetails userDetails) {
        String userName = userDetails.getUsername();
        logger.info("Username : [{}]", userName);
        Pemakai pemakai = pemakaiService.findByUserName(userName);
        Role kolabRole = roleService.getByNama("ROLES_KOLABORATOR").get();
        if (pemakai.getRoles().contains(kolabRole)) {
            return "redirect:/kolaborator/dashboardKolab";
        }

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int nextYear = year + 1;

        Bagian KPT = bagianService.findBySingkatan("KPT").get();
        Bagian KDN = bagianService.findBySingkatan("KDN").get();
        //Bagian KLN = bagianService.findBySingkatan("KLN").get();

        // This year
        Long KPTExpiredinFirstSemesterCount = dokumenKerjaSamaService.getExpiredInFirstSemesterByBagianCount(KPT, year);
        Long KDNExpiredinFirstSemesterCount = dokumenKerjaSamaService.getExpiredInFirstSemesterByBagianCount(KDN, year);
        Long KLNExpiredinFirstSemesterCount = dokumenKerjaSamaLuarNegeriService.getExpiredInFirstSemesterCount(year);
        Long totalExpiredInFirstSemesterCount = dokumenKerjaSamaService.getExpiredInFirstSemesterCount(year);
        totalExpiredInFirstSemesterCount = totalExpiredInFirstSemesterCount + KLNExpiredinFirstSemesterCount;

        Long KPTExpiredThisYearCount = dokumenKerjaSamaService.getExpiredInYearByBagianCount(KPT, year);
        Long KDNExpiredThisYearCount = dokumenKerjaSamaService.getExpiredInYearByBagianCount(KDN, year);
        Long KLNExpiredThisYearCount = dokumenKerjaSamaLuarNegeriService.getExpiredInYearCount(year);
        Long totalExpiredThisYearCount = dokumenKerjaSamaService.getExpiredInYearCount(year);
        totalExpiredThisYearCount = totalExpiredThisYearCount + KLNExpiredThisYearCount;

        // Next year
        Long KPTExpiredinFirstSemesterNextYearCount = dokumenKerjaSamaService
                .getExpiredInFirstSemesterByBagianCount(KPT, nextYear);
        Long KDNExpiredinFirstSemesterNextYearCount = dokumenKerjaSamaService
                .getExpiredInFirstSemesterByBagianCount(KDN, nextYear);
        Long KLNExpiredinFirstSemesterNextYearCount = dokumenKerjaSamaLuarNegeriService
                .getExpiredInFirstSemesterCount(nextYear);
        Long totalExpiredInFirstSemesterNextYearCount = dokumenKerjaSamaService
                .getExpiredInFirstSemesterCount(nextYear);

        Long KPTExpiredNextYearCount = dokumenKerjaSamaService.getExpiredInYearByBagianCount(KPT, nextYear);
        Long KDNExpiredNextYearCount = dokumenKerjaSamaService.getExpiredInYearByBagianCount(KDN, nextYear);
        Long KLNExpiredNextYearCount = dokumenKerjaSamaLuarNegeriService.getExpiredInYearCount(nextYear);
        Long totalExpiredNextYearCount = dokumenKerjaSamaService.getExpiredInYearCount(nextYear);

        List<Rekapitulasi> rekapList = laporanService.getRekapitulasiTahunan();

        // logger.info("EXPIRED THIS YEAR : [{}]", totalExpiredInFirstSemesterCount);

        model.addAttribute("KPTExpiredinFirstSemesterCount", KPTExpiredinFirstSemesterCount);
        model.addAttribute("KDNExpiredinFirstSemesterCount", KDNExpiredinFirstSemesterCount);
        model.addAttribute("KLNExpiredinFirstSemesterCount", KLNExpiredinFirstSemesterCount);
        model.addAttribute("totalExpiredInFirstSemesterCount", totalExpiredInFirstSemesterCount);

        model.addAttribute("KPTExpiredThisYearCount", KPTExpiredThisYearCount);
        model.addAttribute("KDNExpiredThisYearCount", KDNExpiredThisYearCount);
        model.addAttribute("KLNExpiredThisYearCount", KLNExpiredThisYearCount);
        model.addAttribute("totalExpiredThisYearCount", totalExpiredThisYearCount);

        model.addAttribute("KPTExpiredinFirstSemesterNextYearCount", KPTExpiredinFirstSemesterNextYearCount);
        model.addAttribute("KDNExpiredinFirstSemesterNextYearCount", KDNExpiredinFirstSemesterNextYearCount);
        model.addAttribute("KLNExpiredinFirstSemesterNextYearCount", KLNExpiredinFirstSemesterNextYearCount);
        model.addAttribute("totalExpiredInFirstSemesterNextYearCount", totalExpiredInFirstSemesterNextYearCount);

        model.addAttribute("KPTExpiredNextYearCount", KPTExpiredNextYearCount);
        model.addAttribute("KDNExpiredNextYearCount", KDNExpiredNextYearCount);
        model.addAttribute("KLNExpiredNextYearCount", KLNExpiredNextYearCount);
        model.addAttribute("totalExpiredNextYearCount", totalExpiredNextYearCount);

        model.addAttribute("rekapList", rekapList);

        model.addAttribute("currentPage", "dashboard");

        return "dashboard";
    }

    @GetMapping("/showLoginPage")
    public String showLogin(Model model) {
        if (useRecaptcha) {
            return "login";
        } else {
            Captcha captcha = CaptchaUtils.createCaptcha(200, 50);
            String base64Image = CaptchaUtils.encodeBase64(captcha);
            httpSession.setAttribute("captchaAns", captcha.getAnswer());
            model.addAttribute("base64Image", base64Image);

            return "login_nocaptcha";
        }
    }


    @GetMapping("/forgotPassword")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam(name = "email") String email) {
        if (email.isEmpty() || email.isBlank()) {
            return "forgot-password";
        }
        Pemakai pemakai = pemakaiService.findByUserName(email);

        if (pemakai != null) {
            String resetLink = pemakaiService.generateResetPasswordLink(pemakai);
            // pemakai.setResetStr(resetLink);
            // pemakaiService.save(pemakai);
            
            Map<String, Object> templateModel = new HashMap<String, Object>();
            templateModel.put("recipientName", pemakai.getFullName());
            templateModel.put("resetLink", resetLink);

            try {
                emailService.sendMessageUsingThymeleafTemplate(email, "Reset Password", templateModel);
            } catch (IOException ioe) {
                logger.info(ioe.getMessage());
            } catch (MessagingException me) {
                logger.info(me.getMessage());
            }

        }

        return "reset-password-confirmation";
    }

    @GetMapping("/resetPassword2")
    public String showResetPasswordForm(@RequestParam(name = "v") String resetStr, Model model) {
        Pemakai pemakai = pemakaiService.findByResetStr(resetStr);

        if (pemakai == null) {
            return "reset-expired";
        }

        model.addAttribute("pemakai", pemakai);
        model.addAttribute("resetStr", resetStr);
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setResetStr(resetStr);
        model.addAttribute("changePasswordDTO", changePasswordDTO);

        return "reset-password";
    }

    @PostMapping("/resetPassword3")
    public String performResetPassword(@ModelAttribute ChangePasswordDTO changePasswordDTO, Model model) {

        if (!changePasswordDTO.getConfirmPassword().equals(changePasswordDTO.getNewPassword())) {
            model.addAttribute("errorMessage", "Password tidak sama");
            return "reset-password";
        }

        // todo: reset password, remove resetStr from user.
        String encodedPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        String resetStr = changePasswordDTO.getResetStr();

        Pemakai pemakai = pemakaiService.findByResetStr(resetStr);

        if (pemakai == null) {
            model.addAttribute("errorMessage", "Error resetting Password. Harap hubungi Admin.");
            return "reset-password";
        }

        pemakai.setPassword(encodedPassword);
        pemakai.setResetStr(null);
        pemakaiService.save(pemakai);

        return "login";
    }

    @GetMapping("/testerror")
    public String triggerError() {
        throw new RuntimeException("error 500 untuk keperluan testing.");
    }

}
