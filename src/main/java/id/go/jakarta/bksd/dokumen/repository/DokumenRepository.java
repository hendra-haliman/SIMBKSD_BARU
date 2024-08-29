package id.go.jakarta.bksd.dokumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.Dokumen;

@Repository
public interface DokumenRepository extends JpaRepository<Dokumen, Long> {

}
