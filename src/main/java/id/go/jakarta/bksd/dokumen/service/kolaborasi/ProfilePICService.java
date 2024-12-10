package id.go.jakarta.bksd.dokumen.service.kolaborasi;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.ProfilePIC;
import id.go.jakarta.bksd.dokumen.repository.kolaborasi.ProfilePICRepository;

@Service
public class ProfilePICService {
    private ProfilePICRepository repository;

    public ProfilePICService(ProfilePICRepository repository) {
        this.repository = repository;
    }

    public ProfilePIC save(ProfilePIC pic) {
        return repository.save(pic);
    }
}
