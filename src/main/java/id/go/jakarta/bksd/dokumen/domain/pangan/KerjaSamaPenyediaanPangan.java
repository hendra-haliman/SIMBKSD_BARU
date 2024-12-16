package id.go.jakarta.bksd.dokumen.domain.pangan;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class KerjaSamaPenyediaanPangan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Kelompok kelompok;

    @ManyToMany
    private List<Pemda> pemda = new ArrayList<>();

    @ManyToOne
    private BUMD bumd;

    private String mitraBusiness;

    @ManyToMany
    private List<KotaKabupaten> kotaKabupaten = new ArrayList<>();

    @ManyToOne
    private Jenis jenis;

    private int tahun;

    private double kuantitas;

    private double nilaiTransaksi;

    private String keterangan;
}
