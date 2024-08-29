package id.go.jakarta.bksd.dokumen.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.Pemetaan;

@Repository
public interface PemetaanRepository extends JpaRepository<Pemetaan, Long> {

    // @Query("from Pemetaan p left join fetch p.opd o where o.nama like
    // lower(concat('%', :term,'%'))")
    // public Page<Pemetaan> findByKeyword(Pageable pageable, String term);

    // @Query("from Pemetaan p left join fetch p.opd o where o.nama like
    // lower(concat('%', :term,'%')) and (p.tahun = :tahun)")
    // public Page<Pemetaan> findByKeywordAndTahun(Pageable pageable, String term,
    // int tahun);

    @Query("from Pemetaan p where p.kegiatan like lower(concat('%', :term,'%'))")
    public Page<Pemetaan> findByKeyword(Pageable pageable, String term);

    @Query("from Pemetaan p where (p.tahun = :tahun) and p.kegiatan like lower(concat('%', :term,'%'))")
    public Page<Pemetaan> findByKeywordAndTahun(Pageable pageable, int tahun, String term);

}
