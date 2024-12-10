package id.go.jakarta.bksd.dokumen.service.kolaborasi;

import java.util.Optional;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.PhotoKolaborasi;
import id.go.jakarta.bksd.dokumen.repository.kolaborasi.PhotoKolaborasiRepository;

@Service
public class PhotoKolaborasiService {
    private PhotoKolaborasiRepository repository;

    public PhotoKolaborasiService(PhotoKolaborasiRepository repository) {
        this.repository = repository;
    }

    public PhotoKolaborasi save(PhotoKolaborasi photo) {
        return repository.save(photo);
    }

    public Optional<PhotoKolaborasi> findById(Long id) {
        return repository.findById(id);
    }
}
