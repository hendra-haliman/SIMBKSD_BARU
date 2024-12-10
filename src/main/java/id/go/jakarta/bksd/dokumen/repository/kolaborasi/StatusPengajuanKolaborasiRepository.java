package id.go.jakarta.bksd.dokumen.repository.kolaborasi;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.StatusPengajuanKolaborasi;

@Repository
public interface StatusPengajuanKolaborasiRepository extends JpaRepository<StatusPengajuanKolaborasi, Long> {
    @Query("from StatusPengajuanKolaborasi where nama = :nama")
    public Optional<StatusPengajuanKolaborasi> findByNama(String nama);
}
