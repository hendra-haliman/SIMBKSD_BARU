package id.go.jakarta.bksd.dokumen.repository.kolaborasi;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.JenisKolaborasi;

@Repository
public interface JenisKolaborasiRepository  extends JpaRepository<JenisKolaborasi, Long> {
    @Query("from JenisKolaborasi where nama = :nama")
    public Optional<JenisKolaborasi> findByNama(String nama);
}
