package id.go.jakarta.bksd.dokumen.service.kolaborasi;

import java.util.Optional;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.DokumenPendukungKolaborasi;
import id.go.jakarta.bksd.dokumen.repository.kolaborasi.DokumenPendukungKolaborasiRepository;

@Service
public class DokumenPendukungKolaborasiService {
    private DokumenPendukungKolaborasiRepository repository;

    public DokumenPendukungKolaborasiService(DokumenPendukungKolaborasiRepository repository) {
        this.repository = repository;
    }

    public DokumenPendukungKolaborasi save(DokumenPendukungKolaborasi dok) {
        return repository.save(dok);
    }

    public Optional<DokumenPendukungKolaborasi> findById(Long id) {
        return repository.findById(id);
    }
}
