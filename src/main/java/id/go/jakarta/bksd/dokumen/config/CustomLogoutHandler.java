package id.go.jakarta.bksd.dokumen.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLogoutHandler extends SecurityContextLogoutHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutHandler.class);

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logger.info("in logout handler!!!");

        super.logout(request, response, authentication);
        if (authentication != null) {
            logger.info("***************** Logging out user " + authentication.getPrincipal() + " *****************");
        }

        try {
            response.sendRedirect("home");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
