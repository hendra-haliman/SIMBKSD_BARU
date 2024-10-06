package id.go.jakarta.bksd.dokumen.repository.pangan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.pangan.KotaKabupaten;

@Repository
public interface KotaKabupatenRepository extends JpaRepository<KotaKabupatenRepository, Long> {
    @Query("from KotaKabupaten where nama = :nama")
    public KotaKabupaten findByNama(String nama);
}
