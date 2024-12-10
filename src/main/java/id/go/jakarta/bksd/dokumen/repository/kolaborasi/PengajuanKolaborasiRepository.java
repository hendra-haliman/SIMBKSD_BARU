package id.go.jakarta.bksd.dokumen.repository.kolaborasi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.PengajuanKolaborasi;

@Repository
public interface PengajuanKolaborasiRepository extends JpaRepository<PengajuanKolaborasi, Long> {

}
