package id.go.jakarta.bksd.dokumen.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSama;
import id.go.jakarta.bksd.dokumen.domain.JenisMitra;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskah;

@Repository
public interface DokumenKerjasamaRepository extends JpaRepository<DokumenKerjaSama, Long> {
  @Query("""
      Select d from DokumenKerjaSama d left join fetch d.jenisDokumen
        where  d.mitraKerjaSama like lower(concat('%', :term,'%'))
            or d.judulDokumenPerjanjian like lower(concat('%', :term,'%'))
            or d.noDokumenPerjanjian like lower(concat('%', :term,'%'))
            or d.urusanPemerintahanYgDikerjakan like lower(concat('%', :term,'%'))
                      """)
  public Page<DokumenKerjaSama> findByKeyword(Pageable pageable, String term);

  @Query("""
      Select d from DokumenKerjaSama d left join fetch d.jenisDokumen
        where (( d.mitraKerjaSama like lower(concat('%', :term,'%'))
               or d.judulDokumenPerjanjian like lower(concat('%', :term,'%'))
               or d.noDokumenPerjanjian like lower(concat('%', :term,'%'))
               or d.urusanPemerintahanYgDikerjakan like lower(concat('%', :term,'%')))
          and (d.tglPenetapan between :fromDate and :toDate))
                      """)
  public Page<DokumenKerjaSama> findByKeywordAndDateRange(Pageable pageable, String term, Date fromDate,
      Date toDate);

  @Query("""
      Select d from DokumenKerjaSama d left join fetch d.jenisDokumen
        where ((d.bagian.id in :bagianList)
          and ( d.mitraKerjaSama like lower(concat('%', :term,'%'))
                or d.judulDokumenPerjanjian like lower(concat('%', :term,'%'))
                or d.noDokumenPerjanjian like lower(concat('%', :term,'%'))
                or d.urusanPemerintahanYgDikerjakan like lower(concat('%', :term,'%')))
          and (d.tglPenetapan between :fromDate and :toDate))
                      """)
  public Page<DokumenKerjaSama> findByKeywordAndDateRangeAndBagian(Pageable pageable, String term, Date fromDate,
      Date toDate, List<Long> bagianList);

  // @Query("""
  // Select d from DokumenKerjaSama d left join fetch d.jenisDokumen
  // where ((d.opd.id in :opdList) and (d.bagian.id in :bagianList)
  // and ( d.mitraKerjaSama like lower(concat('%', :term,'%'))
  // or d.judulDokumenPerjanjian like lower(concat('%', :term,'%'))
  // or d.noDokumenPerjanjian like lower(concat('%', :term,'%'))
  // or d.urusanPemerintahanYgDikerjakan like lower(concat('%', :term,'%')))
  // and (d.tglPenetapan between :fromDate and :toDate))
  // """)
  // public Page<DokumenKerjaSama>
  // findByKeywordAndDateRangeAndBagianAndOPD(Pageable pageable, String term,
  // Date fromDate, Date toDate, List<Long> bagianList, List<Long> opdList);

  @Query("""
      Select d from DokumenKerjaSama d
        where ((year(d.waktuPelaksanaanBerakhir) = :tahun) and (month(d.waktuPelaksanaanBerakhir) between 1 and 6))
              """)
  public Page<DokumenKerjaSama> getExpiredInFirstSemester(Pageable pageable,
      int tahun);

  @Query(nativeQuery = true, value = """
      Select count(*) from dokumen_kerja_sama d
        where ((year(d.waktu_pelaksanaan_berakhir) = :tahun) and (month(d.waktu_pelaksanaan_berakhir) between 1 and 6))
              """)
  public long getExpiredInFirstSemesterCount(int tahun);

  @Query(nativeQuery = true, value = """
      Select count(*) from dokumen_kerja_sama d
        where ((year(d.waktu_pelaksanaan_berakhir) = :tahun)
          and (month(d.waktu_pelaksanaan_berakhir) between 1 and 6)
          and (d.bagian_id = :bagianId))
              """)
  public long getExpiredInFirstSemesterByBagianCount(Long bagianId, int tahun);

  @Query("""
      Select d from DokumenKerjaSama d
        where (year(d.waktuPelaksanaanBerakhir) = :tahun)

              """)
  public Page<DokumenKerjaSama> getExpiredInYear(Pageable pageable, int tahun);

  @Query(nativeQuery = true, value = """
      Select count(1) from dokumen_kerja_sama d
        where (year(d.waktu_pelaksanaan_berakhir) = :tahun)

              """)
  public long getExpiredInYearCount(int tahun);

  @Query(nativeQuery = true, value = """
      Select count(1) from dokumen_kerja_sama d
        where (year(d.waktu_pelaksanaan_berakhir) = :tahun
          and (d.bagian_id = :bagianId))

              """)
  public long getExpiredInYearByBagianCount(Long bagianId, int tahun);

  @Query(value = """
      Select count(1) from DokumenKerjaSama
        where (year(tglPenetapan) = :tahun or tahun = :tahun) and jenisDokumen = :jenisNaskah and jenisMitra in :jenisMitraList and deleted is false
      """)
  public long getCount(int tahun, JenisNaskah jenisNaskah, List<JenisMitra> jenisMitraList);

  @Query(value = """
      Select count(1) from DokumenKerjaSama
        where (year(tglPenetapan) = :tahun or tahun = :tahun) and jenisDokumen in :jenisNaskahList and jenisMitra in :jenisMitraList and deleted is false
      """)
  public long getCount(int tahun, List<JenisNaskah> jenisNaskahList, List<JenisMitra> jenisMitraList);

  @Query(value = """
      Select count(1) from DokumenKerjaSama
        where (year(tglPenetapan) = :tahun or tahun = :tahun) and bagian = :bagian and jenisDokumen = :jenisNaskah and jenisMitra in :jenisMitraList and deleted is false
      """)
  public long getCount(int tahun, Bagian bagian, JenisNaskah jenisNaskah, List<JenisMitra> jenisMitraList);

  @Query(value = """
      Select count(1) from DokumenKerjaSama
        where (year(tglPenetapan) = :tahun or tahun = :tahun) and jenisDokumen = :jenisNaskah and deleted is false
      """)
  public long getCount(int tahun, JenisNaskah jenisNaskah);

  @Query(value = """
      Select count(1) from DokumenKerjaSama
        where (year(tglPenetapan) = :tahun or tahun = :tahun) and jenisDokumen in :jenisNaskahList and :dummy = :dummy and deleted is false
      """)
  public long getCount(int tahun, List<JenisNaskah> jenisNaskahList, int dummy);

  @Query(value = """
      Select count(1) from DokumenKerjaSama
        where (year(tglPenetapan) = :tahun or tahun = :tahun) and bagian = :bagian and jenisDokumen = :jenisNaskah and deleted is false
      """)
  public long getCount(int tahun, Bagian bagian, JenisNaskah jenisNaskah);

  @Query(value = """
      Select count(1) from DokumenKerjaSama
        where (year(tglPenetapan) = :tahun or tahun= :tahun) and bagian = :bagian and deleted is false
      """)
  public long getCount(int tahun, Bagian bagian);

  @Query(value = """
      select count(1) from DokumenKerjaSama
        where (year(tglPenetapan) = :tahun or tahun = :tahun) and jenisMitra in :jenisMitraList and deleted is false
      """)
  public long getCount(int tahun, List<JenisMitra> jenisMitraList);

  // Find based on nomor dokumen (removing all spaces and white spaces)
  @Query(nativeQuery = true, value = """
      SELECT * FROM dokumen_kerja_sama
        WHERE regexp_REPLACE(no_dokumen_perjanjian, '\\\\s', '') = :nomorDokumen
      """)
  public List<DokumenKerjaSama> findByNomorDokumen(String nomorDokumen);

  @Query(value = """
        from DokumenKerjaSama d left join fetch d.dokumen where d.deleted is false
      """)
  public List<DokumenKerjaSama> findAllFetchDokumen();

}
