package id.go.jakarta.bksd.dokumen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.OPD;

@Repository
public interface OPDRepository extends JpaRepository<OPD, Long> {
    @Query("from OPD where nama = :nama")
    public Optional<OPD> findByNama(String nama);
}
