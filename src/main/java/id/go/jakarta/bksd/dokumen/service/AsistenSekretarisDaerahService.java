package id.go.jakarta.bksd.dokumen.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.AsistenSekretarisDaerah;
import id.go.jakarta.bksd.dokumen.domain.OPD;
import id.go.jakarta.bksd.dokumen.repository.AsistenSekretarisDaerahRepository;

@Service
public class AsistenSekretarisDaerahService {
    private AsistenSekretarisDaerahRepository asistenSekretarisDaerahRepository;

    public AsistenSekretarisDaerahService(AsistenSekretarisDaerahRepository asistenSekretarisDaerahRepository) {
        this.asistenSekretarisDaerahRepository = asistenSekretarisDaerahRepository;
    }

    public List<AsistenSekretarisDaerah> findAll() {
        return asistenSekretarisDaerahRepository.findAll();
    }

    public static String getString(List<AsistenSekretarisDaerah> asistenList) {
        if ((asistenList == null) || (asistenList.isEmpty())) {
            return "N/A";
        }

        String concatenatedNames = IntStream.range(0, asistenList.size())
                                            .mapToObj(i -> {
                                                if (i == asistenList.size() - 1 && asistenList.size() > 1)
                                                    return "dan \n" + asistenList.get(i).getNama();
                                                else
                                                    return asistenList.get(i).getNama();
                                            })
                                            .collect(Collectors.joining(", \n", "", ""));    

        return concatenatedNames;
    }

}
