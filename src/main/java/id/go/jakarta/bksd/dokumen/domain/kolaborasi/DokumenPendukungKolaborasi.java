package id.go.jakarta.bksd.dokumen.domain.kolaborasi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pk_dokumen_pendukung_kolaborasi")
@Data
public class DokumenPendukungKolaborasi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
}
