package id.go.jakarta.bksd.dokumen.config;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.UserActivity;
import id.go.jakarta.bksd.dokumen.repository.UserActivityRepository;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;

@Aspect
@Component
@ConditionalOnProperty(name = "record.user.activity")
public class UserActivityAspect {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserActivityRepository userActivityRepository; // Assuming a repository for persistence

    @Autowired
    private PemakaiService pemakaiService;

    // @AfterReturning(pointcut =
    // "@within(id.go.jakarta.bksd.dokumen.controller..*)", returning = "retVal")
    @AfterReturning("execution(* id.go.jakarta.bksd.dokumen.controller.*.*(..))")
    // public void logUserActivity(JoinPoint joinPoint, Object retVal) {
    public void logUserActivity(JoinPoint joinPoint) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String methodName = joinPoint.getSignature().getName();
        Pemakai pemakai = pemakaiService.findByUserName(username);

        // String arguments = Arrays.toString(joinPoint.getArgs());
        String arguments = "";
        String targetClass = joinPoint.getTarget().getClass().getSimpleName();

        // Additional logic to capture accessed URL or other details based on your
        // application logic
        logger.info("LOGGER ACTIVITY USER");

        UserActivity activity = new UserActivity(username, pemakai, methodName, arguments, targetClass);
        userActivityRepository.save(activity);
    }
}