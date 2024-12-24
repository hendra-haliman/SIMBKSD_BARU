package id.go.jakarta.bksd.dokumen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSamaLuarNegeriOld;

public interface DokumenKerjaSamaLuarNegeriOldRepository extends JpaRepository<DokumenKerjaSamaLuarNegeriOld, Long> {
    @Query("from DokumenKerjaSamaLuarNegeriOld where id = :id and deleted is false")
    public Optional<DokumenKerjaSamaLuarNegeriOld> findById(Long id);

    @Query("select count(1) from DokumenKerjaSamaLuarNegeriOld where tahun = :tahun and deleted is false")
    public long getCount(int tahun);
}
