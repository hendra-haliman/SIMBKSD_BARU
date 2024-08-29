package id.go.jakarta.bksd.dokumen.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSamaLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskahLuarNegeri;

public interface DokumenKerjaSamaLuarNegeriRepository extends JpaRepository<DokumenKerjaSamaLuarNegeri, Long> {
    @Query("""
            Select d from DokumenKerjaSamaLuarNegeri d
              where ( d.judulDokumen like lower(concat('%', :term,'%')) or d.fileContent like lower(concat('%', :term,'%'))) and (d.tgl between :fromDate and :toDate)
                      """)
    public Page<DokumenKerjaSamaLuarNegeri> findByKeywordAndDateRange(Pageable pageable, String term, Date fromDate,
            Date toDate);

    @Query(nativeQuery = true, value = """
            Select count(*) from dokumen_kerja_sama_luar_negeri d
                where ((year(d.tgl_berakhir) = :tahun) and (month(d.tgl_berakhir) between 1 and 6))
                    """)
    public long getExpiredInFirstSemesterCount(int tahun);

    @Query(nativeQuery = true, value = """
            Select count(1) from dokumen_kerja_sama_luar_negeri d
                where (year(d.tgl_berakhir) = :tahun)

                    """)
    public long getExpiredInYearCount(int tahun);

    @Query(value = """
            Select count(1) from DokumenKerjaSamaLuarNegeri
                where year(tgl) = :tahun and jenisDokumen = :jenisNaskahLuarNegeri
                """)
    public long getCount(int tahun, JenisNaskahLuarNegeri jenisNaskahLuarNegeri);

    @Query(value = """
            Select count(1) from DokumenKerjaSamaLuarNegeri
                where year(tgl) = :tahun
                """)
    public long getCount(int tahun);

    @Query(value = """
            from DokumenKerjaSamaLuarNegeri d
            """)
    public List<DokumenKerjaSamaLuarNegeri> findAll();

    @Query(value = """
            from DokumenKerjaSamaLuarNegeri d left join d.dokumen
            """)
    public List<DokumenKerjaSamaLuarNegeri> findAllJoined();

}
