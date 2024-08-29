package id.go.jakarta.bksd.dokumen.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.JenisNaskah;
import id.go.jakarta.bksd.dokumen.repository.JenisNaskahRepository;

@Service
public class JenisNaskahService {

    private JenisNaskahRepository jenisNaskahRepository;

    private ModelMapper modelMapper;

    public JenisNaskahService(JenisNaskahRepository jenisNaskahRepository, ModelMapper mapper) {
        this.jenisNaskahRepository = jenisNaskahRepository;
        this.modelMapper = mapper;
    }

    public Optional<JenisNaskah> findByNama(String nama) {
        return jenisNaskahRepository.getByNama(nama);
    }

    public List<JenisNaskah> findAll() {
        return jenisNaskahRepository.findAll();
    }

    // public List<JenisNaskahDTO> findAll(String selected) {
    // List<JenisNaskah> jenisNaskahList = jenisNaskahRepository.findAll();

    // List<JenisNaskahDTO> jenisNaskahDTOList =
    // jenisNaskahList.stream().map(this::convertToDTO).collect(Collectors.toList());

    // if ("pks".equals(selected)) {
    // JenisNaskah jnPKS = findByNama("Perjanjian Kerja Sama").get();
    // List<JenisNaskah> jnList = List.of(jnPKS);

    // for(JenisNaskah jenisNaskah : jenisNaskahList) {
    // if (jenisNaskah.getNama().equals(jnList))
    // }
    // }

    // return jenisNaskahDTOList;
    // }

    // private JenisNaskahDTO convertToDTO(JenisNaskah jenisNaskah) {
    // return modelMapper.map(jenisNaskah, JenisNaskahDTO.class);
    // }
}
