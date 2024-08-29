package id.go.jakarta.bksd.dokumen.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.JenisNaskahLuarNegeri;
import id.go.jakarta.bksd.dokumen.repository.JenisNaskahLuarNegeriRepository;

@Service
public class JenisNaskahLuarNegeriService {

    private JenisNaskahLuarNegeriRepository jenisNaskahLuarNegeriRepository;

    public JenisNaskahLuarNegeriService(JenisNaskahLuarNegeriRepository jenisNaskahLuarNegeriRepository) {
        this.jenisNaskahLuarNegeriRepository = jenisNaskahLuarNegeriRepository;
    }

    public Optional<JenisNaskahLuarNegeri> findByNama(String nama) {
        return jenisNaskahLuarNegeriRepository.getByNama(nama);
    }

    public List<JenisNaskahLuarNegeri> findAll() {
        return jenisNaskahLuarNegeriRepository.findAll();
    }
}
