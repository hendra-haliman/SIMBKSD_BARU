package id.go.jakarta.bksd.dokumen.domain.dto.kolaborasi;

import java.util.ArrayList;
import java.util.List;

import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.kolaborasi.DokumenPendukungKolaborasi;
import id.go.jakarta.bksd.dokumen.domain.kolaborasi.JenisKolaborasi;
import id.go.jakarta.bksd.dokumen.domain.kolaborasi.PhotoKolaborasi;
import id.go.jakarta.bksd.dokumen.domain.kolaborasi.ProfilePIC;
import id.go.jakarta.bksd.dokumen.domain.kolaborasi.StatusPengajuanKolaborasi;
import id.go.jakarta.bksd.dokumen.domain.kolaborasi.SubjekKerjaSama;
import lombok.Data;

@Data
public class PengajuanKolaborasiDTO {
    private Long id;

    private StatusPengajuanKolaborasi statusPengajuan;

    private PhotoKolaborasi photoKolaborasi;

    private SubjekKerjaSama subjekKerjaSama;

    private JenisKolaborasi jenisKolaborasi;

    private Pemakai kolaborator;

    private ProfilePIC profilePIC;

    private String deskripsi_kolaborasi;

    private String sasaran;

    private List<Pemakai> mitraKolaborator;

    private String estimasiAnggaran;

    private String periodeKegiatan;

    private List<DokumenPendukungKolaborasi> dokumenPendukungKolaborasi= new ArrayList<DokumenPendukungKolaborasi>();

    private String pernyataan;

}
