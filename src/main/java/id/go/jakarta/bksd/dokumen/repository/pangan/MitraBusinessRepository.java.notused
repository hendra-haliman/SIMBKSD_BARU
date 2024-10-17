package id.go.jakarta.bksd.dokumen.repository.pangan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.pangan.MitraBusiness;

@Repository
public interface MitraBusinessRepository extends JpaRepository<MitraBusiness, Long> {
    @Query("from MitraBusiness where nama = :nama")
    public MitraBusiness findByNama(String nama);
}
