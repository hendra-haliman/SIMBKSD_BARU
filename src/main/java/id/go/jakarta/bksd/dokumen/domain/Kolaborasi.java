package id.go.jakarta.bksd.dokumen.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Kolaborasi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Mitra Kerja Sama harus diisi")
    @NotEmpty(message = "Mitra Kerja Sama harus diisi")
    private String mitraKerjaSama;

    @Column(columnDefinition = "varchar(512)")
    @NotNull(message = "Nama & Tempat Kejadian Sama harus diisi")
    @NotEmpty(message = "Nama & Tempat Kejadian Sama harus diisi")
    private String namaDanTempatKejadian;

    private String waktu;

    @ManyToMany(targetEntity = Bagian.class)
    @NotNull(message = "Bagian harus diisi")
    @Size(min = 1, message = "Bagian harus diisi")
    private List<Bagian> bagian;

    private String keterangan;

    @Column(nullable = false)
    @Min(value = 2000, message = "Tahun harus minimal 2000.")
    @Max(value = 2100, message = "Tahun harus maximal 2100.")
    private int tahun;

    private boolean deleted;

    @Column(columnDefinition = "varchar(1024)")
    private String output;

}
