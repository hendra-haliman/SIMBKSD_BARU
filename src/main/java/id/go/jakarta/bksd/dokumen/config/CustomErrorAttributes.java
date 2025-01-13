package id.go.jakarta.bksd.dokumen.config;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.ServletException;

import org.springframework.boot.web.error.ErrorAttributeOptions;

import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        // Include the stack trace and message based on the options provided
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options.including(ErrorAttributeOptions.Include.STACK_TRACE, ErrorAttributeOptions.Include.MESSAGE));
        Throwable error = getError(webRequest);
        if (error instanceof ServletException) {
            ServletException servletException = (ServletException) error;
            errorAttributes.put("customMessage", servletException.getMessage());
        }
        return errorAttributes;
    }
}
