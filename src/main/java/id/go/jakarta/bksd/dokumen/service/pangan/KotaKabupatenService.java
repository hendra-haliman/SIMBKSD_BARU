package id.go.jakarta.bksd.dokumen.service.pangan;

import java.util.List;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.pangan.KotaKabupaten;
import id.go.jakarta.bksd.dokumen.repository.pangan.KotaKabupatenRepository;

@Service
public class KotaKabupatenService {
    private KotaKabupatenRepository kotaKabupatenRepository;

    public KotaKabupatenService(KotaKabupatenRepository kabupatenRepository) {
        this.kotaKabupatenRepository = kabupatenRepository;
    }

    public KotaKabupaten findByNama(String nama) {
        return kotaKabupatenRepository.findByNama(nama);
    }

    public List<KotaKabupaten> findAll() {
        return kotaKabupatenRepository.findAll();
    }
}
