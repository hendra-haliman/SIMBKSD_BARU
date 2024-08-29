package id.go.jakarta.bksd.dokumen.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {
    Logger logger = LoggerFactory.getLogger(getClass());

    // @Autowired
    // private AuthenticationManager authenticationManager;

    public CustomLoginFilter(String loginURL, String method) {
        setUsernameParameter("email");
        // setAuthenticationManager(authenticationManager);
        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginURL, method));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        logger.info("on CustomLoginFilter");

        logger.info("Authentication Manager: [{}]", this.getAuthenticationManager());

        // super.setAuthenticationManager(this.getAuthenticationManager());
        return super.attemptAuthentication(request, response);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        logger.info("in doFilter Custom");

        super.doFilter(request, response, chain);
    }
}
