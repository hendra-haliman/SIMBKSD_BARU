package id.go.jakarta.bksd.dokumen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.JenisNaskah;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskahLuarNegeri;

@Repository
public interface JenisNaskahLuarNegeriRepository extends JpaRepository<JenisNaskahLuarNegeri, Long> {
    
    @Query("from JenisNaskahLuarNegeri where nama = :nama")
    public Optional<JenisNaskahLuarNegeri> getByNama(String nama);
}
