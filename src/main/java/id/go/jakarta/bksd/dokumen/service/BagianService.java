package id.go.jakarta.bksd.dokumen.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.repository.BagianRepository;

@Service
public class BagianService {
    private BagianRepository bagianRepository;

    public BagianService(BagianRepository bagianRepository) {
        this.bagianRepository = bagianRepository;
    }

    public List<Bagian> findAll() {
        return bagianRepository.findAll();
    }

    public Optional<Bagian> findByNama(String nama) {
        return bagianRepository.findByNama(nama);
    }

    public Optional<Bagian> findBySingkatan(String singkatan) {
        return bagianRepository.findBySingkatan(singkatan);
    }

}
