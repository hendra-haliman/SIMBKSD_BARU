package id.go.jakarta.bksd.dokumen.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.Pemakai;

@Repository
public interface PemakaiRepository extends JpaRepository<Pemakai, Long> {
    @Query("from Pemakai where email = ?1")
    public Pemakai findByUserName(String userName);

    @Query("Select u from Pemakai u  where u.namaDepan like lower(concat('%', :term,'%')) or u.namaKeluarga like lower(concat('%', :term,'%'))")
    public Page<Pemakai> findByKeyword(Pageable pageable, String term);

    @Query("Select u from Pemakai u where u.resetStr=:resetStr")
    public Pemakai findByResetStr(String resetStr);
}
