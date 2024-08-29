package id.go.jakarta.bksd.dokumen.domain;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DokumenKerjaSamaLuarNegeri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjek;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date tgl;

    private int tahun;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date tglDimulai;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date tglBerakhir;

    @Column(columnDefinition = "varchar(2048)")
    private String judulDokumen;

    @Column(columnDefinition = "varchar(2048)")
    private String keterangan;

    @Column(columnDefinition = "varchar(2048)")
    private String linkDokumen;

    @Column(columnDefinition = "varchar(2048)")
    private String penanggungJawabTertandaTangan;

    private boolean deleted;

    @ManyToOne(targetEntity = JenisNaskahLuarNegeri.class)
    private JenisNaskahLuarNegeri jenisDokumen;

    @OneToMany(targetEntity = DokumenLuarNegeri.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "dokumenKerjaSamaLuarNegeriId", referencedColumnName = "id")
    // @JsonIgnore
    private List<DokumenLuarNegeri> dokumen = new ArrayList<DokumenLuarNegeri>();

    @Column(columnDefinition = "MEDIUMTEXT")
    private String fileContent;

}
