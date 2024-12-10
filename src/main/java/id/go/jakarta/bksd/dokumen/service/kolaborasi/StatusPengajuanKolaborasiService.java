package id.go.jakarta.bksd.dokumen.service.kolaborasi;

import java.util.Optional;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.StatusPengajuanKolaborasi;
import id.go.jakarta.bksd.dokumen.repository.kolaborasi.StatusPengajuanKolaborasiRepository;

public class StatusPengajuanKolaborasiService {
    private StatusPengajuanKolaborasiRepository repository;

    public StatusPengajuanKolaborasiService(StatusPengajuanKolaborasiRepository repository) {
        this.repository = repository;
    }

    public StatusPengajuanKolaborasi save(StatusPengajuanKolaborasi status) {
        return repository.save(status);
    }

    public Optional<StatusPengajuanKolaborasi> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<StatusPengajuanKolaborasi> findByNama(String nama) {
        return repository.findByNama(nama);
    }
}
