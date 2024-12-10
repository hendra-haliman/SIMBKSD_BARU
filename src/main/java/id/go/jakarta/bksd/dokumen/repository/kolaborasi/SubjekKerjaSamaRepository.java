package id.go.jakarta.bksd.dokumen.repository.kolaborasi;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.SubjekKerjaSama;

@Repository
public interface SubjekKerjaSamaRepository extends JpaRepository<SubjekKerjaSama, Long> {
    @Query("from SubjekKerjaSama where nama = :nama")
    public Optional<SubjekKerjaSama> findByNama(String nama);
}
