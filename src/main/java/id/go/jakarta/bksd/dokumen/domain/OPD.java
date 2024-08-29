package id.go.jakarta.bksd.dokumen.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class OPD {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String singkatan;

    private String nama;

    @ManyToOne(targetEntity = AsistenSekretarisDaerah.class)
    private AsistenSekretarisDaerah asisten;
}
