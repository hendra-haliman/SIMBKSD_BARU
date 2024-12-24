package id.go.jakarta.bksd.dokumen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Entity untuk kerja sama luar negeri tahun 2020-2021, 
 *   karena detail informasi berbeda dengan tahun berikutnya.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DokumenKerjaSamaLuarNegeriOld {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tahun;

    @Column(columnDefinition = "varchar(2048)")
    private String objek;

    private String mitra;

    private String cakupan;

    @Column(columnDefinition = "varchar(2048)")
    private String dasar;

    private String skpdUnitTerkait;

    private String waktu;

    @Column(columnDefinition = "varchar(2048)")
    private String pelaksanaan;

    @Column(columnDefinition = "varchar(2048)")
    private String kendala;

    @Column(columnDefinition = "varchar(2048)")
    private String tindakLanjut;

    private boolean deleted;
}
