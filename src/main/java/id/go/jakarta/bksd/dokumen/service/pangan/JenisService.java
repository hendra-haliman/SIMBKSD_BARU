package id.go.jakarta.bksd.dokumen.service.pangan;

import java.util.List;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.pangan.Jenis;
import id.go.jakarta.bksd.dokumen.repository.pangan.JenisRepository;

@Service
public class JenisService {
    private JenisRepository jenisRepository;

    public JenisService(JenisRepository jenisRepository) {
        this.jenisRepository = jenisRepository;
    }

    public Jenis findByNama(String nama) {
        return jenisRepository.findByNama(nama);
    }

    public List<Jenis> findAll() {
        return jenisRepository.findAll();
    }
}
