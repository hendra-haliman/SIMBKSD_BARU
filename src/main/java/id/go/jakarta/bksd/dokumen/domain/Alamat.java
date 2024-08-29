package id.go.jakarta.bksd.dokumen.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alamat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String alamat1;

    private String alamat2;

    private String alamat3;

    private String rt;

    private String rw;

    private String kelurahan;

    private String kecamatan;

    private String kabupaten;

    private String propinsi;

    private Integer kodepos;

//    private Long userId;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private long modifiedDate;


    public Alamat(
            String alamat1,
            String alamat2,
            String rt,
            String rw,
            Integer kodepos
    ) {
        this.alamat1 = alamat1;
        this.alamat2 = alamat2;
        this.rt = rt;
        this.rw = rw;
        this.kodepos = kodepos;
        this.createdDate = Instant.now().getEpochSecond();
        this.modifiedDate = Instant.now().getEpochSecond();
    }
}
