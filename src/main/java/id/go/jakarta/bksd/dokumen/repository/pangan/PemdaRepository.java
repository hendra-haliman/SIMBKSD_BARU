package id.go.jakarta.bksd.dokumen.repository.pangan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.pangan.Pemda;

@Repository
public interface PemdaRepository extends JpaRepository<Pemda, Long> {
    @Query("from Pemda where nama = :nama")
    public Pemda findByNama(String nama);
}
