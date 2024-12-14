package id.go.jakarta.bksd.dokumen.service.pangan;

import java.util.List;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.pangan.BUMD;
import id.go.jakarta.bksd.dokumen.repository.pangan.BUMDRepository;

@Service
public class BUMDService {
    private BUMDRepository bumdRepository;

    public BUMDService(BUMDRepository bumdRepository) {
        this.bumdRepository = bumdRepository;
    }

    public BUMD findByNama(String nama) {
        return bumdRepository.findByNama(nama);
    }

    public List<BUMD> findAll() {
        return bumdRepository.findAll();
    }
}
