package id.go.jakarta.bksd.dokumen.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class Kolaborasi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mitraKerjaSama;

    @Column(columnDefinition = "varchar(512)")
    private String namaDanTempatKejadian;

    private String waktu;

    @ManyToMany(targetEntity = Bagian.class)
    private List<Bagian> bagian;

    private String keterangan;

    private int tahun;

    private boolean deleted;

    @Column(columnDefinition = "varchar(1024)")
    private String output;

}
