package id.go.jakarta.bksd.dokumen.domain.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class DokumenKerjaSamaLuarNegeriOldDTO {
    private Long id;

    private int tahun;

    private String objek;

    private String mitra;

    private String cakupan;

    private String dasar;

    private String skpdUnitTerkait;

    private String waktu;

    private String pelaksanaan;

    private String kendala;

    private String tindakLanjut;

    private boolean deleted;

}
