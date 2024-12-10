package id.go.jakarta.bksd.dokumen.repository.kolaborasi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.kolaborasi.PhotoKolaborasi;

@Repository
public interface PhotoKolaborasiRepository extends JpaRepository<PhotoKolaborasi, Long> { 

}
