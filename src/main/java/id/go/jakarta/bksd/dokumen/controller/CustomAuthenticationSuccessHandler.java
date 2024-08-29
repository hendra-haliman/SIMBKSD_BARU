package id.go.jakarta.bksd.dokumen.controller;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
        log.info("In customAuthenticationSuccessHandler");
        log.info("**** authentication *** {} {}", authentication, authentication.getPrincipal());
        String userName = authentication.getName();
        log.info("userName=" + userName);

        // Pemakai pemakai = pemakaiService.findByUserName(userName);
        // now place in the session
        HttpSession session = request.getSession();
        // session.setAttribute("pemakai", pemakai);
        // forward to home page
        response.sendRedirect(request.getContextPath() + "/");
    }
}