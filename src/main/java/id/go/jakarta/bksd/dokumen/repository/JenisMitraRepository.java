package id.go.jakarta.bksd.dokumen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.JenisMitra;

@Repository
public interface JenisMitraRepository extends JpaRepository<JenisMitra, Long> {
    @Query("from JenisMitra where nama = :nama")
    public Optional<JenisMitra> getByNama(String nama);

}
