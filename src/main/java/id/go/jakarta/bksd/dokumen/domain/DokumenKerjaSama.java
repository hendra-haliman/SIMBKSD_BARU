package id.go.jakarta.bksd.dokumen.domain;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DokumenKerjaSama extends AuditMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Bagian.class, optional = false)
    private Bagian bagian;

    @ManyToOne(targetEntity = JenisMitra.class)
    private JenisMitra jenisMitra;

    @Column(columnDefinition = "varchar(1024)")
    private String mitraKerjaSama;

    @ManyToMany(targetEntity = OPD.class, fetch = FetchType.LAZY)
    private List<OPD> opd;

    @ManyToMany(targetEntity = AsistenSekretarisDaerah.class)
    private List<AsistenSekretarisDaerah> rumpun_opd;

    @Column(columnDefinition = "varchar(512)")
    private String judulDokumenPerjanjian;

    @Column(columnDefinition = "varchar(512)")
    private String noDokumenPerjanjian;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date tglPenetapan;

    private int tahun;

    private String urusanPemerintahanYgDikerjakan;

    @ManyToOne(targetEntity = JenisNaskah.class)
    private JenisNaskah jenisDokumen;

    @Column(columnDefinition = "varchar(512)")
    private String jangkaWaktuPelaksanaan;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date waktuPelaksanaanDimulai;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date waktuPelaksanaanBerakhir;

    @Column(columnDefinition = "varchar(1024)")
    private String pembiayaan;

    private boolean filesTelahDiperoleh = false;

    private boolean deleted;

    @OneToMany(targetEntity = Dokumen.class, cascade = CascadeType.MERGE)
    @JoinColumn(name = "dokumenkerjasamaId", referencedColumnName = "id")
    // @JsonIgnore
    private List<Dokumen> dokumen = new ArrayList<Dokumen>();

    @Column(columnDefinition = "varchar(2048)")
    private String keterangan;

    @Column(columnDefinition = "varchar(1024)")
    private String keterangan2;

    @Column(columnDefinition = "varchar(1024)")
    private String keterangan3;

    @Column(columnDefinition = "varchar(1024)")
    private String keterangan4;

    @Column(columnDefinition = "varchar(1024)")
    private String linkDokumenKerjaSama;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String fileContent;

    // public void setFileContent(String value) {
    //     this.fileContent = new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    // }

}
