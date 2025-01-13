package id.go.jakarta.bksd.dokumen.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.google.common.cache.LoadingCache;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    
    private LoadingCache<String, Integer> loginAttemptCache;

    public void setLoginAttemptCache(LoadingCache<String, Integer> loginAttemptCache) {
        this.loginAttemptCache = loginAttemptCache;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        Object attribute = request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isKolaborator = authorities.stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLES_KOLABORATOR"));

        loginAttemptCache.invalidate(request.getParameter("email"));

        if (isKolaborator) {
            redirectStrategy.sendRedirect(request, response, "/kolaborator/dashboardKolab");
            return;
        }
        if (attribute instanceof SavedRequest) {
            SavedRequest savedRequest = (SavedRequest) attribute;
            String redirectUrl = savedRequest.getRedirectUrl();
            response.sendRedirect(redirectUrl);
        } else {
            // Redirect to default success page if no saved request found
            redirectStrategy.sendRedirect(request, response, "/dashboard");
        }
    }
}