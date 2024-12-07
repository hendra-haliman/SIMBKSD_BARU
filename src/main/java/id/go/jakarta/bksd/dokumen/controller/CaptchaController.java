package id.go.jakarta.bksd.dokumen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.apiclub.captcha.Captcha;
import id.go.jakarta.bksd.dokumen.captcha.CaptchaUtils;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    HttpSession httpSession;

    @GetMapping("/getCaptcha")
    public String getCapthca() {
        Captcha captcha = CaptchaUtils.createCaptcha(200, 50);
        String base64Image = CaptchaUtils.encodeBase64(captcha);
        httpSession.setAttribute("captchaAns", captcha.getAnswer());
        return base64Image;
    }
}
