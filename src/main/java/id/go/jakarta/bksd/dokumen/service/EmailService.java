package id.go.jakarta.bksd.dokumen.service;

import java.io.IOException;
import java.util.Map;

import id.go.jakarta.bksd.dokumen.domain.EmailDetails;
import jakarta.mail.MessagingException;

public interface EmailService {
    String sendSimpleMail(EmailDetails details);

    String sendMailWithAttachment(EmailDetails details);

    void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String, Object> templateModel)
            throws IOException, MessagingException;

}