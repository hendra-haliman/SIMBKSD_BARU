package id.go.jakarta.bksd.dokumen.service;

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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaPredicate;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSamaLuarNegeriOld;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.DokumenKerjaSamaLuarNegeriOldDTO;
import id.go.jakarta.bksd.dokumen.repository.DokumenKerjaSamaLuarNegeriOldRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class DokumenKerjaSamaLuarNegeriOldService {
    private DokumenKerjaSamaLuarNegeriOldRepository repository;
    private PemakaiService pemakaiService;

    @PersistenceContext
    private EntityManager entityManager;

    public DokumenKerjaSamaLuarNegeriOldService(
        DokumenKerjaSamaLuarNegeriOldRepository repository,
        PemakaiService pemakaiService) {
        this.repository = repository;
        this.pemakaiService = pemakaiService;
    }

    public DokumenKerjaSamaLuarNegeriOld save(DokumenKerjaSamaLuarNegeriOld doc) {
        return repository.save(doc);
    }

    public Page<DokumenKerjaSamaLuarNegeriOldDTO> getDokumen(PagingRequest pagingRequest) {
        int pageNo = pagingRequest.getStart() / pagingRequest.getLength();
        int pageSize = pagingRequest.getLength();

        return getDokumen2(pagingRequest, pageNo, pageSize);

    }

    public Page<DokumenKerjaSamaLuarNegeriOldDTO> getDokumen2(PagingRequest pagingRequest, int pageNo, int pageSize) {
        boolean canViewDocLink = pemakaiService.hasAuthority(PemakaiService.AUTH_CAN_VIEW_DOC_LINK);
        int filterTahun = pagingRequest.getFilterTahun();
        String searchTerm = "%" + pagingRequest.getSearch().getValue() + "%";

        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();

        JpaCriteriaQuery<DokumenKerjaSamaLuarNegeriOld> cq = cb.createQuery(DokumenKerjaSamaLuarNegeriOld.class);
        Root<DokumenKerjaSamaLuarNegeriOld> dokumenKerjaSamaLuarNegeriOld = cq.from(DokumenKerjaSamaLuarNegeriOld.class);

        Predicate searchObjekPredicate = cb.like(dokumenKerjaSamaLuarNegeriOld.get("objek"), searchTerm);
        Predicate searchMitraPredicate = cb.like(dokumenKerjaSamaLuarNegeriOld.get("mitra"), searchTerm);
        Predicate searchCakupanPredicate = cb.like(dokumenKerjaSamaLuarNegeriOld.get("cakupan"), searchTerm);
        Predicate searchDasarPredicate = cb.like(dokumenKerjaSamaLuarNegeriOld.get("dasar"), searchTerm);
        Predicate searchSKPDredicate = cb.like(dokumenKerjaSamaLuarNegeriOld.get("skpdUnitTerkait"), searchTerm);

        Predicate searchPredicate = cb.or(searchObjekPredicate, searchMitraPredicate, searchCakupanPredicate, searchDasarPredicate, searchSKPDredicate);

        Predicate deletedPredicate = cb.equal(dokumenKerjaSamaLuarNegeriOld.get("deleted"), false);

        Predicate filterTahunPredicate = cb.equal(dokumenKerjaSamaLuarNegeriOld.get("tahun"), filterTahun);

        JpaPredicate jpaPredicate;
        
        if (filterTahun > 0) {
            jpaPredicate = cb.and(searchPredicate, deletedPredicate, filterTahunPredicate);
        } else {
            jpaPredicate = cb.and(searchPredicate, deletedPredicate);
        }       
        
        
        cq.where(jpaPredicate);

        cq.orderBy(
                cb.desc(dokumenKerjaSamaLuarNegeriOld.get("tahun")));
       
        cq.select(dokumenKerjaSamaLuarNegeriOld);
        
        List<DokumenKerjaSamaLuarNegeriOld> dokumenList = entityManager.createQuery(cq)
            .setFirstResult(pageNo * pageSize)
            .setMaxResults(pageSize)
            .getResultList();

        TypedQuery<Long> countQuery = entityManager.createQuery(cq.createCountQuery());

        Long totalElements = countQuery.getSingleResult();

        Page<DokumenKerjaSamaLuarNegeriOldDTO> page = new Page<>(
                dokumenList.stream().map(dks -> convertToDto(dks, canViewDocLink)).collect(Collectors.toList()));
        page.setRecordsFiltered((int) totalElements.longValue());
        page.setRecordsTotal((int) totalElements.longValue());
        page.setDraw(pagingRequest.getDraw());

        return page;
    }

    private DokumenKerjaSamaLuarNegeriOldDTO convertToDto(DokumenKerjaSamaLuarNegeriOld dokumenKerjaSama,
            boolean canViewDocLink) {

        DokumenKerjaSamaLuarNegeriOldDTO dto = new DokumenKerjaSamaLuarNegeriOldDTO();

        dto.setId(dokumenKerjaSama.getId());
        dto.setTahun(dokumenKerjaSama.getTahun());
        dto.setObjek(dokumenKerjaSama.getObjek());
        dto.setMitra(dokumenKerjaSama.getMitra());
        dto.setCakupan(dokumenKerjaSama.getCakupan());
        dto.setDasar(dokumenKerjaSama.getDasar());
        dto.setSkpdUnitTerkait(dokumenKerjaSama.getSkpdUnitTerkait());
        dto.setWaktu(dokumenKerjaSama.getWaktu());
        dto.setPelaksanaan(dokumenKerjaSama.getPelaksanaan());
        dto.setKendala(dokumenKerjaSama.getKendala());
        dto.setTindakLanjut(dokumenKerjaSama.getTindakLanjut());
        dto.setDeleted(dokumenKerjaSama.isDeleted());

        return dto;
    }

    public Optional<DokumenKerjaSamaLuarNegeriOld> findById(Long id) {
        return repository.findById(id);
    }

    public ByteArrayInputStream exportExcel(PagingRequest pagingRequest) throws IOException {
        Page<DokumenKerjaSamaLuarNegeriOldDTO> dokumenKerjaSama = getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaLuarNegeriOldDTO> dokumenKerjaSamaList = dokumenKerjaSama.getData();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        writeToExcel(workbook, sheet, dokumenKerjaSamaList, 0, "Export Data Kerja Sama (Format Lama)");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    
    public int writeToExcel(Workbook workbook, Sheet sheet, List<DokumenKerjaSamaLuarNegeriOldDTO> dokumenKerjaSamaList) {
        return writeToExcel(workbook, sheet, dokumenKerjaSamaList, 0);
    }

    public int writeToExcel(Workbook workbook, Sheet sheet, List<DokumenKerjaSamaLuarNegeriOldDTO> dokumenKerjaSamaList,
            int rowIdx) {
        return writeToExcel(workbook, sheet, dokumenKerjaSamaList, rowIdx, null);
    }

    public int writeToExcel(Workbook workbook, Sheet sheet, List<DokumenKerjaSamaLuarNegeriOldDTO> dokumenKerjaSamaList,
            int rowIdx, String title) {
        sheet.setColumnWidth(0, 6 * 256);        
        sheet.setColumnWidth(1, 8 * 256);
        sheet.setColumnWidth(2, 40 * 256);
        sheet.setColumnWidth(3, 30 * 256);
        sheet.setColumnWidth(4, 40 * 256);
        sheet.setColumnWidth(5, 40 * 256);
        sheet.setColumnWidth(6, 40 * 256);
        sheet.setColumnWidth(7, 40 * 256);
        sheet.setColumnWidth(8, 40 * 256);
        sheet.setColumnWidth(9, 40 * 256);
        sheet.setColumnWidth(10, 40 * 256);

        // int rowIdx = 0;
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

        CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 32);
        titleCellStyle.setFont(titleFont);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setWrapText(true);
        titleCellStyle.setBorderBottom(BorderStyle.THIN);
        titleCellStyle.setBorderTop(BorderStyle.THIN);
        titleCellStyle.setBorderLeft(BorderStyle.THIN);
        titleCellStyle.setBorderRight(BorderStyle.THIN);

        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints((float) 40.0);
        int col = 0;

        Cell cell;
        if (title != null) {
            cell = row.createCell(col);
            cell.setCellValue(title);
            cell.setCellStyle(titleCellStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 10));
        }

        rowIdx++;
        row = sheet.createRow(rowIdx);

        cell = row.createCell(col++);
        cell.setCellValue("No.");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Tahun");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Objek");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Mitra");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Cakupan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Dasar");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("SKPD/Unit Terkait");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Waktu");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Pelaksanaan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Kendala");

        cell.setCellStyle(headerCellStyle);
        cell = row.createCell(col++);
        cell.setCellValue("Tindak Lanjut");
        cell.setCellStyle(headerCellStyle);

        rowIdx++;
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);
        wrapStyle.setBorderBottom(BorderStyle.THIN);
        wrapStyle.setBorderTop(BorderStyle.THIN);
        wrapStyle.setBorderLeft(BorderStyle.THIN);
        wrapStyle.setBorderRight(BorderStyle.THIN);
        wrapStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle wrapStyle2 = workbook.createCellStyle();
        wrapStyle2.setWrapText(true);
        wrapStyle2.setBorderBottom(BorderStyle.THIN);
        wrapStyle2.setBorderTop(BorderStyle.THIN);
        wrapStyle2.setBorderLeft(BorderStyle.THIN);
        wrapStyle2.setBorderRight(BorderStyle.THIN);
        wrapStyle2.setAlignment(HorizontalAlignment.CENTER);
        wrapStyle2.setVerticalAlignment(VerticalAlignment.CENTER);

        int counter = 1;
        
        for (DokumenKerjaSamaLuarNegeriOldDTO dokumenKerjaSamaOldDTO : dokumenKerjaSamaList) {
            col = 0;
            row = sheet.createRow(rowIdx++);
            row.setHeight((short) -1);

            row.createCell(col++).setCellValue(counter++);

            Cell tahun = row.createCell(col++);
            tahun.setCellValue(dokumenKerjaSamaOldDTO.getTahun());
            tahun.setCellStyle(wrapStyle);

            Cell objek = row.createCell(col++);
            objek.setCellValue(dokumenKerjaSamaOldDTO.getObjek());
            objek.setCellStyle(wrapStyle);

            Cell mitra = row.createCell(col++);
            mitra.setCellValue(dokumenKerjaSamaOldDTO.getMitra());
            mitra.setCellStyle(wrapStyle);

            Cell cakupan = row.createCell(col++);
            cakupan.setCellValue(dokumenKerjaSamaOldDTO.getCakupan());
            cakupan.setCellStyle(wrapStyle);

            Cell dasar = row.createCell(col++);
            dasar.setCellValue(dokumenKerjaSamaOldDTO.getDasar());
            dasar.setCellStyle(wrapStyle);

            Cell skpdUnitTerkait = row.createCell(col++);
            skpdUnitTerkait.setCellValue(dokumenKerjaSamaOldDTO.getSkpdUnitTerkait());
            skpdUnitTerkait.setCellStyle(wrapStyle);

            Cell waktu = row.createCell(col++);
            waktu.setCellValue(dokumenKerjaSamaOldDTO.getWaktu());
            waktu.setCellStyle(wrapStyle);

            Cell pelaksanaan = row.createCell(col++);
            pelaksanaan.setCellValue(dokumenKerjaSamaOldDTO.getPelaksanaan());
            pelaksanaan.setCellStyle(wrapStyle);

            Cell kendala = row.createCell(col++);
            kendala.setCellValue(dokumenKerjaSamaOldDTO.getKendala());
            kendala.setCellStyle(wrapStyle);

            Cell tindakLanjut = row.createCell(col++);
            tindakLanjut.setCellValue(dokumenKerjaSamaOldDTO.getTindakLanjut());
            tindakLanjut.setCellStyle(wrapStyle);


        }

        return rowIdx;
        
    }

    public long getCount(int year) {
        return repository.getCount(year);
    }

}
