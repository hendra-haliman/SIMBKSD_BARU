package id.go.jakarta.bksd.dokumen.domain.pangan;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class KerjaSamaPenyediaanPangan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Kelompok kelompok;

    @ManyToMany
    private List<Pemda> pemda;

    @ManyToOne
    private BUMD bumd;

    @ManyToOne
    private MitraBusiness mitraBusiness;

    @ManyToMany
    private List<KotaKabupaten> kotaKabupaten;

    @ManyToOne
    private Jenis jenis;

    private int tahun;

    private int kuantitas;

    private float nilaiTransaksi;

    private String keterangan;
}
