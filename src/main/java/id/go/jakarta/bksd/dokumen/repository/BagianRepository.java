package id.go.jakarta.bksd.dokumen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.Bagian;

@Repository
public interface BagianRepository extends JpaRepository<Bagian, Long> {
    @Query("from Bagian where nama = :nama")
    public Optional<Bagian> findByNama(String nama);

    @Query("from Bagian where singkatan = :singkatan")
    public Optional<Bagian> findBySingkatan(String singkatan);
    
}
