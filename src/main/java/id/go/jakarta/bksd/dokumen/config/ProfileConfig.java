package id.go.jakarta.bksd.dokumen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ProfileConfig {
    @Bean
    @Profile("default")
    public String profileLogo() {
        return "SIMBKSD-DEV";
    }

    @Bean
    @Profile("dev")
    public String profileLogoDev() {
        return "SIMBKSD-DEV";
    }

    @Bean
    @Profile("devhendra")
    public String profileLogoDevhendra() {
        return "SIMBKSD-DEV";
    }

    @Bean
    @Profile("devpusdatin")
    public String profileLogoDevpusdatin() {
        return "SIMBKSD-DEV";
    }


    @Bean
    @Profile("uat")
    public String profileLogoUat() {
        return "SIMBKSD-UAT";
    }

    @Bean
    @Profile("prod")
    public String profileLogoProd() {
        return "SIMBKSD";
    }
}
