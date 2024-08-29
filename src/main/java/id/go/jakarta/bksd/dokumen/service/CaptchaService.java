package id.go.jakarta.bksd.dokumen.service;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import id.go.jakarta.bksd.dokumen.ReCaptchaInvalidException;
import id.go.jakarta.bksd.dokumen.ReCaptchaUnavailableException;
import id.go.jakarta.bksd.dokumen.domain.GoogleResponse;

@Service("captchaService")
public class CaptchaService extends AbstractCaptchaService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CaptchaService.class);

    @Override
    public void processResponse(final String response) {
        securityCheck(response);

        final URI verifyUri = URI
                .create(String.format(RECAPTCHA_URL_TEMPLATE, getReCaptchaSecret(), response, getClientIP()));
        try {
            final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
            LOGGER.debug("Google's response: {} ", googleResponse.toString());

            if (!googleResponse.isSuccess()) {
                if (googleResponse.hasClientError()) {
                    reCaptchaAttemptService.reCaptchaFailed(getClientIP());
                }
                throw new ReCaptchaInvalidException("reCaptchatidak berhasil divalidasi.");
            }
        } catch (RestClientException rce) {
            throw new ReCaptchaUnavailableException("Login tidak tersedia pada saat ini. Mohon coba beberapa saat lagi.",
                    rce);
        }
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
    }
}