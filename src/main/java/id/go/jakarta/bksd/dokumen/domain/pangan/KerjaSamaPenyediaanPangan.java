package id.go.jakarta.bksd.dokumen.domain.pangan;

import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import id.go.jakarta.bksd.dokumen.domain.AuditMetaData;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class KerjaSamaPenyediaanPangan  extends AuditMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Kelompok harus diisi")
    //@Size(min = 1, message = "Kelompok harus diisi")
    private Kelompok kelompok;

    @ManyToMany
    @NotNull(message = "Pemda harus diisi")
    @Size(min = 1, message = "Pemda harus diisi")
    private List<Pemda> pemda = new ArrayList<>();

    @ManyToOne
    @NotNull(message = "BUMD harus diisi")
    //@Size(min = 1, message = "BUMD harus diisi")
    private BUMD bumd;

    private String mitraBusiness;

    @ManyToMany
    private List<KotaKabupaten> kotaKabupaten = new ArrayList<>();

    @ManyToOne
    @NotNull(message = "Jenis harus diisi")
    //@Size(min = 1, message = "Jenis harus diisi")        
    private Jenis jenis;

    @Column(nullable = false)
    @Min(value = 2000, message = "Tahun harus minimal 2000.")
    @Max(value = 2100, message = "Tahun harus maximal 2100.")
    private int tahun;

    @NumberFormat(style = Style.NUMBER, pattern = "#,##0.00")
    private double kuantitas;

    @NumberFormat(style = Style.NUMBER, pattern = "#,##0.00")
    private double nilaiTransaksi;

    private String keterangan;

    private boolean deleted;
}
