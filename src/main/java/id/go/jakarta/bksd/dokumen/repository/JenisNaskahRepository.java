package id.go.jakarta.bksd.dokumen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.JenisNaskah;

@Repository
public interface JenisNaskahRepository extends JpaRepository<JenisNaskah, Long> {
    
    @Query("from JenisNaskah where nama = :nama")
    public Optional<JenisNaskah> getByNama(String nama);
}
