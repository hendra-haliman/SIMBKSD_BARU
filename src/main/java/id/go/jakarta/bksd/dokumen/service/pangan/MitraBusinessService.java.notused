package id.go.jakarta.bksd.dokumen.service.pangan;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.pangan.MitraBusiness;
import id.go.jakarta.bksd.dokumen.repository.pangan.MitraBusinessRepository;

@Service
public class MitraBusinessService {
    private MitraBusinessRepository businessRepository;

    public MitraBusinessService(MitraBusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public MitraBusiness findByNama(String nama) {
        return businessRepository.findByNama(nama);
    }
}
