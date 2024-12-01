package id.go.jakarta.bksd.dokumen.domain.pengajuan_kolaborasi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pk_status_pengajuan_kolaborasi")
public class StatusPengajuanKolaborasi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nama;
}
