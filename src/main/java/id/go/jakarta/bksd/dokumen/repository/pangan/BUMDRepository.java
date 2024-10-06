package id.go.jakarta.bksd.dokumen.repository.pangan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.pangan.BUMD;

@Repository
public interface BUMDRepository extends JpaRepository<BUMD, Long> {
    @Query("from BUMD where nama = :nama")
    public BUMD findByNama(String nama);
}
