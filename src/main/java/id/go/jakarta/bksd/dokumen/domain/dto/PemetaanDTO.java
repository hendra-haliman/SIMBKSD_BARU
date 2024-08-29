package id.go.jakarta.bksd.dokumen.domain.dto;

import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.JenisMitra;
import id.go.jakarta.bksd.dokumen.domain.OPD;
import lombok.Data;

@Data
public class PemetaanDTO {
    private Long id;

    private int tahun;

    private Bagian bagian;
    
    private OPD opd;

    private JenisMitra jenisMitra;

    private String urusanPemerintahan;

    private String kegiatan;

    private String jangkaWaktu;

    private String namaCalonMitra;

    private String noKontrakCalonMitra;

    private String skalaPrioritas;

    private String waktuPelaksanaan;

    private String waktuPelaksanaanTW1;

    private String waktuPelaksanaanTW2;

    private String waktuPelaksanaanTW3;

    private String waktuPelaksanaanTW4;

    private String PIC;

    private String sumberPembiayaan;

}
