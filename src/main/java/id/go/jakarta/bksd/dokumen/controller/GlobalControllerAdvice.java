package id.go.jakarta.bksd.dokumen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private String profileLogo;

    @Value("${enable.kolaborasi:false}")
    private boolean enableKolaborasi;

    @ModelAttribute
    public void globalAttributes(Model model) {
        model.addAttribute("profileLogo", profileLogo);
        model.addAttribute("enableKolaborasi", enableKolaborasi);
    }
}
