package id.go.jakarta.bksd.dokumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.AsistenSekretarisDaerah;

@Repository
public interface AsistenSekretarisDaerahRepository extends JpaRepository<AsistenSekretarisDaerah, Long> {

}
