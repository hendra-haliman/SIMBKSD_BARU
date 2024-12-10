package id.go.jakarta.bksd.dokumen.service.kolaborasi;

import java.util.Optional;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.SubjekKerjaSama;
import id.go.jakarta.bksd.dokumen.repository.kolaborasi.SubjekKerjaSamaRepository;

@Service
public class SubjekKerjaSamaService {
    private SubjekKerjaSamaRepository repository;

    public SubjekKerjaSamaService(SubjekKerjaSamaRepository repository) {
        this.repository = repository;
    }

    public SubjekKerjaSama save(SubjekKerjaSama subjek) {
        return repository.save(subjek);
    }

    public Optional<SubjekKerjaSama> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<SubjekKerjaSama> findByNama(String nama) {
        return repository.findByNama(nama);
    }
}
