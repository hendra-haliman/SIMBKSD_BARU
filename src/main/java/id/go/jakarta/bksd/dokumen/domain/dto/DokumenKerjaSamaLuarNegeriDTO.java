package id.go.jakarta.bksd.dokumen.domain.dto;

import java.util.Date;
import java.util.List;

import id.go.jakarta.bksd.dokumen.domain.DokumenLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskahLuarNegeri;
import lombok.Data;

@Data
public class DokumenKerjaSamaLuarNegeriDTO {
    private Long id;

    private String subjek;

    private Date tgl;

    private int tahun;

    private Date tglDimulai;

    private Date tglBerakhir;

    private String judulDokumen;

    private String keterangan;

    private String linkDokumen;

    private List<DokumenLuarNegeri> dokumen;

    private String penanggungJawabTertandaTangan;

    private JenisNaskahLuarNegeri jenisDokumen;

    private boolean deleted;
}
