package id.go.jakarta.bksd.dokumen.repository.pangan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.pangan.Jenis;

@Repository
public interface JenisRepository extends JpaRepository<Jenis, Long> {
    @Query("from Jenis where nama = :nama")
    public Jenis findByNama(String nama);
}
