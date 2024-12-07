package id.go.jakarta.bksd.dokumen.config;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import id.go.jakarta.bksd.dokumen.domain.GoogleResponse;
import id.go.jakarta.bksd.dokumen.service.CaptchaServiceV3;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Component
public class CustomLoginFilter2 extends OncePerRequestFilter {
    Logger logger = LoggerFactory.getLogger(getClass());

    protected static final String RECAPTCHA_URL_TEMPLATE = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

    @Value("${google.recaptcha.key.secret}")
    private String secret;

    private String action = "register";

    private float threshold = 0.5f;

    @Value("${useRecaptcha}")
    private boolean useRecaptcha;

    // @Autowired
    private CaptchaServiceV3 captchaServiceV3;

    public CustomLoginFilter2(CaptchaServiceV3 captchaServiceV3) {
        this.captchaServiceV3 = captchaServiceV3;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.endsWith("authenticateUser") && useRecaptcha) {
            String google_response = request.getParameter("response");

            final URI verifyUri = URI
                    .create(String.format(RECAPTCHA_URL_TEMPLATE, secret, google_response, getClientIP(request)));

            RestTemplate restTemplate = new RestTemplate();
            final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri,
                    GoogleResponse.class);
            logger.info("Google's response: {} ", googleResponse.toString());

            if (googleResponse.isSuccess() && googleResponse.getAction().equals(action)
                    && googleResponse.getScore() >= threshold) {
                filterChain.doFilter(request, response);
            } else {
                request.logout();

            }

        } 
        
        else if (uri.endsWith("authenticateUser")) {
            HttpSession session = request.getSession();
            String captchaAnswer = (String) session.getAttribute("captchaAns");
            String capthcaSubmitted = request.getParameter("captcha");

            if (!captchaAnswer.equals(capthcaSubmitted)) {
                logger.info("CAPTCHA diff [{}] [{}]", captchaAnswer, capthcaSubmitted);
                response.sendRedirect(request.getContextPath() + "/showLoginPage?errorCaptcha=y");
                return ;
            }
        }

        filterChain.doFilter(request, response);
    }

    protected String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
