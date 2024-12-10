package id.go.jakarta.bksd.dokumen.service.kolaborasi;

import java.util.Optional;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.JenisKolaborasi;
import id.go.jakarta.bksd.dokumen.repository.kolaborasi.JenisKolaborasiRepository;

@Service
public class JenisKolaborasiService {
    private JenisKolaborasiRepository repository;

    public JenisKolaborasiService(JenisKolaborasiRepository repository) {
        this.repository = repository;
    }

    public JenisKolaborasi save(JenisKolaborasi jenis) {
        return repository.save(jenis);
    }

    public Optional<JenisKolaborasi> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<JenisKolaborasi> findByNama(String nama) {
        return repository.findByNama(nama);
    }
}
