package id.go.jakarta.bksd.dokumen.service.pangan;

import java.util.List;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.pangan.Kelompok;
import id.go.jakarta.bksd.dokumen.repository.pangan.KelompokRepository;

@Service
public class KelompokService {
    private KelompokRepository kelompokRepository;

    public KelompokService(KelompokRepository kelompokRepository) {
        this.kelompokRepository = kelompokRepository;
    }

    public Kelompok findByNama(String nama) {
        return kelompokRepository.findByNama(nama);
    }

    public List<Kelompok> findAll() {
        return kelompokRepository.findAll();
    }
}
