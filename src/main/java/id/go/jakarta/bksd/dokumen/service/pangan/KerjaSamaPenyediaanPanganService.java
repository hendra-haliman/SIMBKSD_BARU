package id.go.jakarta.bksd.dokumen.service.pangan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaPredicate;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.Kolaborasi;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.pangan.BUMD;
import id.go.jakarta.bksd.dokumen.domain.pangan.Jenis;
import id.go.jakarta.bksd.dokumen.domain.pangan.Kelompok;
import id.go.jakarta.bksd.dokumen.domain.pangan.KerjaSamaPenyediaanPangan;
import id.go.jakarta.bksd.dokumen.domain.pangan.KotaKabupaten;
import id.go.jakarta.bksd.dokumen.domain.pangan.Pemda;
import id.go.jakarta.bksd.dokumen.repository.pangan.KerjaSamaPenyediaanPanganRepository;
import id.go.jakarta.bksd.dokumen.service.ListUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class KerjaSamaPenyediaanPanganService {
    private KerjaSamaPenyediaanPanganRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    private KelompokService kelompokService;


    public KerjaSamaPenyediaanPanganService(KerjaSamaPenyediaanPanganRepository repository,
                                            KelompokService kelompokService) {
        this.repository = repository;
        this.kelompokService = kelompokService;
    }

    public KerjaSamaPenyediaanPangan save(KerjaSamaPenyediaanPangan kerjasama) {
        return repository.save(kerjasama);
    }

    public List<KerjaSamaPenyediaanPangan> findAll() {
        return repository.findAll();
    }

    public Optional<KerjaSamaPenyediaanPangan> findById(Long id) {
        return repository.findById(id);
    }

    public Page<KerjaSamaPenyediaanPangan> getKerjasama(PagingRequest pagingRequest) {
        int pageNo = pagingRequest.getStart() / pagingRequest.getLength();
        int pageSize = pagingRequest.getLength();

        return getKerjasama(pagingRequest, pageNo, pageSize);
    }

    public Page<KerjaSamaPenyediaanPangan> getKerjasama(PagingRequest pagingRequest, int pageNo, int pageSize) {
        int tahun = pagingRequest.getFilterTahun();
        String searchTerm =  "%" + pagingRequest.getSearch().getValue() + "%";
        List<Long> filterKelompok = pagingRequest.getFilterKelompok();

        if (filterKelompok==null || filterKelompok.isEmpty()) {
            filterKelompok  = kelompokService.findAll().stream().map(Kelompok::getId).collect(Collectors.toList());
        }

        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();
        JpaCriteriaQuery<KerjaSamaPenyediaanPangan> cq = cb.createQuery(KerjaSamaPenyediaanPangan.class);
        Root<KerjaSamaPenyediaanPangan> kerjasama = cq.from(KerjaSamaPenyediaanPangan.class);
        Join<KerjaSamaPenyediaanPangan, Pemda> pemdaJoin = kerjasama.join("pemda");
        
        Predicate tahunPredicate;
        if (tahun == 0) {
            tahunPredicate = cb.greaterThan(kerjasama.get("tahun"), 0);
        } else {
            tahunPredicate = cb.equal(kerjasama.get("tahun"), tahun);
        }

        Predicate searchPredicate = cb.like(kerjasama.get("mitraBusiness"), searchTerm);

        Predicate notDeleted = cb.isFalse(kerjasama.get("deleted"));

        JpaPredicate predicate = cb.and(tahunPredicate, searchPredicate, notDeleted);
       
        Predicate kelompokPredicate = kerjasama.get("kelompok").get("id").in(filterKelompok);

        predicate = cb.and(predicate, kelompokPredicate);


        cq.where(predicate);

        cq.orderBy(
            cb.desc(kerjasama.get("kelompok")),
            cb.desc(kerjasama.get("tahun")),
            cb.desc(pemdaJoin.get("nama"))
        );

        cq.select(kerjasama);

        List<KerjaSamaPenyediaanPangan> kerjasamaList = entityManager.createQuery(cq)
                                                                    .setFirstResult(pageNo * pageSize)
                                                                    .setMaxResults(pageSize)
                                                                    .getResultList();

        TypedQuery<Long> countQuery = entityManager.createQuery(cq.createCountQuery());
        Long totalElements = countQuery.getSingleResult();

        Page<KerjaSamaPenyediaanPangan> kerjasamaPage = new Page<>(kerjasamaList);
        kerjasamaPage.setRecordsFiltered((int) totalElements.longValue());
        kerjasamaPage.setRecordsTotal((int) totalElements.longValue());
        kerjasamaPage.setDraw(pagingRequest.getDraw());

        return kerjasamaPage;
    }

    public ByteArrayInputStream exportExcel(PagingRequest pagingRequest) throws IOException {
        Page<KerjaSamaPenyediaanPangan> pangan = getKerjasama(pagingRequest, 0, 1000000);
        List<KerjaSamaPenyediaanPangan> panganList = pangan.getData();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Kerja Sama Pangan");

        writeDokumenKerjasamaToExcel(workbook, sheet, panganList);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());

    }

    public void writeDokumenKerjasamaToExcel(Workbook workbook, Sheet sheet, List<KerjaSamaPenyediaanPangan> panganList) {
        sheet.setColumnWidth(1, 10 * 256);
        sheet.setColumnWidth(2, 30 * 256);
        sheet.setColumnWidth(3, 20 * 256);
        sheet.setColumnWidth(4, 40 * 256);
        sheet.setColumnWidth(5, 20 * 256);
        sheet.setColumnWidth(6, 20 * 256);
        sheet.setColumnWidth(7, 20 * 256);
        sheet.setColumnWidth(8, 20 * 256);
        sheet.setColumnWidth(9, 20 * 256);


        int rowIdx = 0;

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont boldFont = (XSSFFont) workbook.createFont();
        boldFont.setBold(true);
        headerCellStyle.setFont(boldFont);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setWrapText(true);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints((float) 40.0);
        int col = 0;

        Cell cell = row.createCell(col++);
        cell.setCellValue("No");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Tahun");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("BUMD");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Pemda");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Mitra Bisnis");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Kota/Kabupaten");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Jenis");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Kuantitas(Kg)");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Nilai Transaksi(Rp)");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Keterangan");
        cell.setCellStyle(headerCellStyle);   
        
        rowIdx++;
 
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);
        wrapStyle.setBorderBottom(BorderStyle.THIN);
        wrapStyle.setBorderTop(BorderStyle.THIN);
        wrapStyle.setBorderLeft(BorderStyle.THIN);
        wrapStyle.setBorderRight(BorderStyle.THIN);
        wrapStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (KerjaSamaPenyediaanPangan pangan : panganList) {
            col = 0;
            row = sheet.createRow(rowIdx++);
            row.setHeight((short) -1);

            Cell numberCell = row.createCell(col++);
            numberCell.setCellValue(rowIdx - 1);
            numberCell.setCellStyle(wrapStyle);

            Cell tahunCell = row.createCell(col++);
            tahunCell.setCellValue(pangan.getTahun());
            tahunCell.setCellStyle(wrapStyle);

            Cell bumdCell = row.createCell(col++);
            BUMD bumd = pangan.getBumd();
            bumdCell.setCellValue(bumd!=null?bumd.getNama(): "");
            bumdCell.setCellStyle(wrapStyle);


            Cell pemdaCell = row.createCell(col++);
            Pemda pemda = ListUtil.getFirstOrNull(pangan.getPemda());
            pemdaCell.setCellValue(pemda!=null?pemda.getNama(): "");
            pemdaCell.setCellStyle(wrapStyle);

            Cell mitraCell = row.createCell(col++);
            mitraCell.setCellValue(pangan.getMitraBusiness());
            mitraCell.setCellStyle(wrapStyle);

            Cell kotaCell = row.createCell(col++);
            KotaKabupaten kota = ListUtil.getFirstOrNull(pangan.getKotaKabupaten());
            kotaCell.setCellValue(kota!=null?kota.getNama(): "");
            kotaCell.setCellStyle(wrapStyle);

            Cell jenisCell = row.createCell(col++);
            Jenis jenis = pangan.getJenis();
            jenisCell.setCellValue(jenis!=null?jenis.getNama(): "");
            jenisCell.setCellStyle(wrapStyle);

            Cell kuantitasCell = row.createCell(col++);
            kuantitasCell.setCellValue(pangan.getKuantitas());
            kuantitasCell.setCellStyle(wrapStyle);

            Cell nilaiCell = row.createCell(col++);
            nilaiCell.setCellValue(pangan.getNilaiTransaksi());
            nilaiCell.setCellStyle(wrapStyle);

            Cell keteranganCell = row.createCell(col++);
            keteranganCell.setCellValue(pangan.getKeterangan());
            keteranganCell.setCellStyle(wrapStyle);

        }
    }
}
