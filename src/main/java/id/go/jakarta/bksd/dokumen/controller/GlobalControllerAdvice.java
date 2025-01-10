package id.go.jakarta.bksd.dokumen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private String profileLogo;

    @ModelAttribute
    public void globalAttributes(Model model) {
        model.addAttribute("profileLogo", profileLogo);
    }
}
