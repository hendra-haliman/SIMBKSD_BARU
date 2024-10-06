package id.go.jakarta.bksd.dokumen.repository.pangan;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.pangan.Jenis;

@Repository
public interface JenisRepository {
    @Query("from Jenis where nama = :nama")
    public Jenis findByNama(String nama);
}
