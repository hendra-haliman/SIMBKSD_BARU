package id.go.jakarta.bksd.dokumen.domain.dto;

import java.util.Date;
import java.util.List;

import id.go.jakarta.bksd.dokumen.domain.AsistenSekretarisDaerah;
import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.Dokumen;
import id.go.jakarta.bksd.dokumen.domain.JenisMitra;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskah;
import id.go.jakarta.bksd.dokumen.domain.OPD;
import lombok.Data;

@Data
public class DokumenKerjaSamaDTO {
    private Long id;

    private Bagian bagian;

    private JenisMitra jenisMitra;

    private String mitraKerjaSama;

    private List<OPD> opd;

    private List<AsistenSekretarisDaerah> rumpun_opd;

    private String judulDokumenPerjanjian;

    private String noDokumenPerjanjian;

    private Date tglPenetapan;

    private int tahun;

    private String urusanPemerintahanYgDikerjakan;

    private JenisNaskah jenisDokumen;

    private String jangkaWaktuPelaksanaan;

    private Date waktuPelaksanaanDimulai;

    private Date waktuPelaksanaanBerakhir;

    private String pembiayaan;

    private boolean filesTelahDiperoleh;

    private boolean deleted;

    private List<Dokumen> dokumen;

    private String keterangan;

    private String keterangan2;

    private String keterangan3;

    private String keterangan4;

    private String linkDokumenKerjaSama;

}
