package id.go.jakarta.bksd.dokumen.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.JenisMitra;
import id.go.jakarta.bksd.dokumen.repository.JenisMitraRepository;

@Service
public class JenisMitraService {

    private JenisMitraRepository jenisMitraRepository;

    public JenisMitraService(JenisMitraRepository jenisMitraRepository) {
        this.jenisMitraRepository = jenisMitraRepository;
    }

    public Optional<JenisMitra> findByNama(String nama) {
        return jenisMitraRepository.getByNama(nama);
    }

    public List<JenisMitra> findAll() {
        return jenisMitraRepository.findAll();
    }
}
