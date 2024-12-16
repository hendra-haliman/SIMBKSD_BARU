package id.go.jakarta.bksd.dokumen.repository.pangan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.pangan.KerjaSamaPenyediaanPangan;

@Repository
public interface KerjaSamaPenyediaanPanganRepository extends JpaRepository<KerjaSamaPenyediaanPangan, Long> {
    @Query("from KerjaSamaPenyediaanPangan where deleted = false")
    public List<KerjaSamaPenyediaanPangan> findAll();
}
