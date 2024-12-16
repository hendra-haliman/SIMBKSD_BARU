package id.go.jakarta.bksd.dokumen.service.pangan;

import java.util.List;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.pangan.Pemda;
import id.go.jakarta.bksd.dokumen.repository.pangan.PemdaRepository;

@Service
public class PemdaService {
    private PemdaRepository pemdaRepository;

    public PemdaService(PemdaRepository pemdaRepository) {
        this.pemdaRepository = pemdaRepository;
    }

    public Pemda findByNama(String nama) {
        return pemdaRepository.findByNama(nama);
    }

    public List<Pemda> findAll() {
        return pemdaRepository.findAll();
    }
}
