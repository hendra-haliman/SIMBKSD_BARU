package id.go.jakarta.bksd.dokumen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import id.go.jakarta.bksd.dokumen.service.PemakaiService;

@Controller
@RequestMapping("/user")
public class UserController {
    private PemakaiService pemakaiService;

    public UserController(PemakaiService pemakaiService) {
        this.pemakaiService = pemakaiService;
    }

    @GetMapping("")
    public String userHome(Model model) {
        return "user/user";
    }

}
