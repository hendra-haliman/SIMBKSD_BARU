package id.go.jakarta.bksd.dokumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.Kolaborasi;
import java.util.List;

@Repository
public interface KolaborasiRepository extends JpaRepository<Kolaborasi, Long> {
    public List<Kolaborasi> findByTahun(int tahun);

    @Query(value = """
            select count(1) from Kolaborasi k where k.tahun = :year
            """)
    public long getCount(int year);
}
