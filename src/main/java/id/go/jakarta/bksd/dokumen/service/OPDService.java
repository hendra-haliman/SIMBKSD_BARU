package id.go.jakarta.bksd.dokumen.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.OPD;
import id.go.jakarta.bksd.dokumen.repository.OPDRepository;

@Service
public class OPDService {
    private OPDRepository opdRepository;

    public OPDService(OPDRepository opdRepository) {
        this.opdRepository = opdRepository;
    }

    public Optional<OPD> findByNama(String nama) {
        return opdRepository.findByNama(nama);
    }

    public List<OPD> findAll() {
        return opdRepository.findAll();
    }

    public static String getString(List<OPD> opdList) {
        if ((opdList == null) || (opdList.isEmpty())) {
            return "N/A";
        }

        String concatenatedNames = IntStream.range(0, opdList.size())
                .mapToObj(i -> {
                    if (i == opdList.size() - 1 && opdList.size() > 1)
                        return "dan \n" + opdList.get(i).getNama();
                    else
                        return opdList.get(i).getNama();
                })
                .collect(Collectors.joining(", \n", "", ""));

        return concatenatedNames;
    }
}
