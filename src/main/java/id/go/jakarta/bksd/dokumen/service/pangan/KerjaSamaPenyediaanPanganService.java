package id.go.jakarta.bksd.dokumen.service.pangan;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.pangan.KerjaSamaPenyediaanPangan;
import id.go.jakarta.bksd.dokumen.repository.pangan.KerjaSamaPenyediaanPanganRepository;

@Service
public class KerjaSamaPenyediaanPanganService {
    private KerjaSamaPenyediaanPanganRepository repository;

    public KerjaSamaPenyediaanPanganService(KerjaSamaPenyediaanPanganRepository repository) {
        this.repository = repository;
    }

    public KerjaSamaPenyediaanPangan save(KerjaSamaPenyediaanPangan kerjasama) {
        return repository.save(kerjasama);
    }
}
