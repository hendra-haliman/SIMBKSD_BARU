package id.go.jakarta.bksd.dokumen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Pemetaan extends AuditMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tahun;

    @ManyToOne(targetEntity = Bagian.class)
    private Bagian bagian;

    @ManyToOne(targetEntity = OPD.class)
    private OPD opd;

    @ManyToOne(targetEntity = JenisMitra.class)
    private JenisMitra jenisMitra;

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
