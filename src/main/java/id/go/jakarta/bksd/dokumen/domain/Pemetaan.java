package id.go.jakarta.bksd.dokumen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Pemetaan extends AuditMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Min(value = 2000, message = "Tahun harus minimal 2000.")
    @Max(value = 2100, message = "Tahun harus maximal 2100.")
    private int tahun;

    @ManyToOne(targetEntity = Bagian.class)
    @NotNull(message = "Bagian harus diisi")
    private Bagian bagian;

    @ManyToOne(targetEntity = OPD.class)
    @NotNull(message = "OPD harus diisi")
    private OPD opd;

    @ManyToOne(targetEntity = JenisMitra.class)
    private JenisMitra jenisMitra;

    @NotNull(message="Urusan Pemerintahan harus diisi.")
    @NotEmpty(message="Urusan Pemerintahan harus diisi.")
    private String urusanPemerintahan;

    @Column(columnDefinition = "varchar(1024)")
    private String kegiatan;

    private String jangkaWaktu;

    @Column(columnDefinition = "varchar(1024)")
    private String namaCalonMitra;

    private String noKontrakCalonMitra;

    private String skalaPrioritas;

    private String waktuPelaksanaan;

    private String waktuPelaksanaanTW1;

    private String waktuPelaksanaanTW2;

    private String waktuPelaksanaanTW3;

    private String waktuPelaksanaanTW4;

    private String pic;

    private String sumberPembiayaan;

    private boolean deleted;
}
