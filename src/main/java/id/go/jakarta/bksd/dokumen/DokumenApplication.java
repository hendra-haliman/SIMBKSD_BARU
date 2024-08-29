package id.go.jakarta.bksd.dokumen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class DokumenApplication {

	public static void main(String[] args) {
		SpringApplication.run(DokumenApplication.class, args);
	}

}
