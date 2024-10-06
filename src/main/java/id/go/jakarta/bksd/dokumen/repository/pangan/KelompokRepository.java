package id.go.jakarta.bksd.dokumen.repository.pangan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.pangan.Kelompok;

@Repository
public interface KelompokRepository extends JpaRepository<Kelompok, Long> {
    @Query("from Kelompok where nama = :nama")
    public Kelompok findByNama(String nama);
}
