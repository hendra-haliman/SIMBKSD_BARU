package id.go.jakarta.bksd.dokumen.domain.pengajuan_kolaborasi;

import java.util.ArrayList;
import java.util.List;

import id.go.jakarta.bksd.dokumen.domain.AuditMetaData;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pk_pengajuan_kolaborasi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PengajuanKolaborasi extends AuditMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = StatusPengajuanKolaborasi.class)
    private StatusPengajuanKolaborasi statusPengajuan;

    @OneToOne
    private PhotoKolaborasi photoKolaborasi;

    @OneToOne
    private SubjekKerjaSama subjekKerjaSama;

    @ManyToOne(targetEntity = JenisKolaborasi.class)
    private JenisKolaborasi jenisKolaborasi;

    @ManyToOne(targetEntity = Pemakai.class)
    private Pemakai kolaborator;

    @OneToOne
    private ProfilePIC profilePIC;

    private String deskripsi_kolaborasi;

    private String sasaran;

    @ManyToMany
    private List<Pemakai> mitraKolaborator;

    private String estimasiAnggaran;

    private String periodeKegiatan;

    @OneToMany(targetEntity = DokumenPendukungKolaborasi.class, cascade = CascadeType.ALL)
    private List<DokumenPendukungKolaborasi> dokumenPendukungKolaborasi= new ArrayList<DokumenPendukungKolaborasi>();

    private String pernyataan;
}
