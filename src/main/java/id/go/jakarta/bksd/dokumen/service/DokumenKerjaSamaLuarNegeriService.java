package id.go.jakarta.bksd.dokumen.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
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
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSamaLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskahLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.DokumenKerjaSamaLuarNegeriDTO;
import id.go.jakarta.bksd.dokumen.repository.DokumenKerjaSamaLuarNegeriRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class DokumenKerjaSamaLuarNegeriService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private DokumenKerjaSamaLuarNegeriRepository dokumenKerjaSamaLuarNegeriRepository;

    private PemakaiService pemakaiService;

    private ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public DokumenKerjaSamaLuarNegeriService(DokumenKerjaSamaLuarNegeriRepository dokumenKerjaSamaLuarNegeriRepository,
            ModelMapper modelMapper, PemakaiService pemakaiService) {
        this.dokumenKerjaSamaLuarNegeriRepository = dokumenKerjaSamaLuarNegeriRepository;
        this.modelMapper = modelMapper;
        this.pemakaiService = pemakaiService;
    }

    public Page<DokumenKerjaSamaLuarNegeriDTO> getDokumen(PagingRequest pagingRequest) {
        int pageNo = pagingRequest.getStart() / pagingRequest.getLength();
        int pageSize = pagingRequest.getLength();

        return getDokumen2(pagingRequest, pageNo, pageSize);
    }

    public Page<DokumenKerjaSamaLuarNegeriDTO> getDokumen2(PagingRequest pagingRequest, int pageNo, int pageSize) {
        boolean canViewDocLink = pemakaiService.hasAuthority(PemakaiService.AUTH_CAN_VIEW_DOC_LINK);

        Date filterStartDate = pagingRequest.getFilterStartDate();
        Date filterEndDate = pagingRequest.getFilterEndDate();
        Date filterTglBerakhirStartDate = pagingRequest.getFilterTglBerakhirStartDate();
        Date filterTglBerkahirEndDate = pagingRequest.getFilterTglBerakhirEndDate();
        boolean includeNullTglBerakhir = pagingRequest.isIncludeNullTglBerakhir();

        String searchTerm = "%" + pagingRequest.getSearch().getValue() + "%";

        if (filterStartDate == null) {
            filterStartDate = new Date(70, 01, 01);
        }

        if (filterEndDate == null) {
            filterEndDate = new Date();
        }

        if (filterTglBerakhirStartDate == null) {
            filterTglBerakhirStartDate = new Date(70, 01, 01);
        }

        if (filterTglBerkahirEndDate == null) {
            filterTglBerkahirEndDate = new Date(4000, 01, 01);
        }

        int filterTahun = pagingRequest.getFilterTahun();

        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();

        JpaCriteriaQuery<DokumenKerjaSamaLuarNegeri> cq = cb.createQuery(DokumenKerjaSamaLuarNegeri.class);
        Root<DokumenKerjaSamaLuarNegeri> dokumenKerjaSamaLuarNegeri = cq.from(DokumenKerjaSamaLuarNegeri.class);

        Predicate searchJudulPredicate = cb.like(dokumenKerjaSamaLuarNegeri.get("judulDokumen"), searchTerm);
        Predicate searchFileContentPredicate = cb.like(dokumenKerjaSamaLuarNegeri.get("fileContent"), searchTerm);
        Predicate searchPredicate = cb.or(searchJudulPredicate, searchFileContentPredicate);

        Predicate deletedPredicate = cb.equal(dokumenKerjaSamaLuarNegeri.get("deleted"), false);

        Predicate filterStartDatePredicate = cb.greaterThanOrEqualTo(dokumenKerjaSamaLuarNegeri.get("tgl"),
                filterStartDate);
        Predicate filterEndDatePredicate = cb.lessThanOrEqualTo(dokumenKerjaSamaLuarNegeri.get("tgl"), filterEndDate);
        Predicate filterTahunPredicate = cb.equal(dokumenKerjaSamaLuarNegeri.get("tahun"), filterTahun);

        Predicate filterTglBerakhirStartDatePredicate = cb.greaterThanOrEqualTo(
                dokumenKerjaSamaLuarNegeri.get("tglBerakhir"),
                filterTglBerakhirStartDate);

        Predicate filterTglBerakhirEndDatePredicate = cb
                .lessThanOrEqualTo(dokumenKerjaSamaLuarNegeri.get("tglBerakhir"), filterTglBerkahirEndDate);

        Predicate filterTglBerakhirPredicate = cb.and(filterTglBerakhirStartDatePredicate,
                filterTglBerakhirEndDatePredicate);
        Predicate filterNullTglBerakhir = cb.isNull(dokumenKerjaSamaLuarNegeri.get("tglBerakhir"));

        JpaPredicate datePredicate = cb.or(filterTahunPredicate,
                cb.and(filterStartDatePredicate, filterEndDatePredicate));

        JpaPredicate jpaPredicate = cb.and(datePredicate, searchPredicate, deletedPredicate);

        if (includeNullTglBerakhir) {
            jpaPredicate = cb.and(jpaPredicate, cb.or(filterTglBerakhirPredicate, filterNullTglBerakhir));

        } else {
            jpaPredicate = cb.and(jpaPredicate, filterTglBerakhirPredicate);

        }

        cq.where(jpaPredicate);

        cq.orderBy(
                cb.desc(dokumenKerjaSamaLuarNegeri.get("tahun")));

        cq.select(dokumenKerjaSamaLuarNegeri);

        List<DokumenKerjaSamaLuarNegeri> dokumenList = entityManager.createQuery(cq)
                .setFirstResult(pageNo * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        TypedQuery<Long> countQuery = entityManager.createQuery(cq.createCountQuery());

        Long totalElements = countQuery.getSingleResult();

        Page<DokumenKerjaSamaLuarNegeriDTO> page = new Page<>(
                dokumenList.stream().map(dks -> convertToDto(dks, canViewDocLink)).collect(Collectors.toList()));
        page.setRecordsFiltered((int) totalElements.longValue());
        page.setRecordsTotal((int) totalElements.longValue());
        page.setDraw(pagingRequest.getDraw());

        return page;

    }

    private DokumenKerjaSamaLuarNegeriDTO convertToDto(DokumenKerjaSamaLuarNegeri dokumenKerjaSama,
            boolean canViewDocLink) {
        // return modelMapper.map(dokumenKerjaSama,
        // DokumenKerjaSamaLuarNegeriDTO.class);

        DokumenKerjaSamaLuarNegeriDTO dto = new DokumenKerjaSamaLuarNegeriDTO();

        dto.setId(dokumenKerjaSama.getId());
        dto.setSubjek(dokumenKerjaSama.getSubjek());
        dto.setTgl(dokumenKerjaSama.getTgl());
        dto.setTahun(dokumenKerjaSama.getTahun());
        dto.setTglDimulai(dokumenKerjaSama.getTglDimulai());
        dto.setTglBerakhir(dokumenKerjaSama.getTglBerakhir());
        dto.setJudulDokumen(dokumenKerjaSama.getJudulDokumen());
        dto.setKeterangan(dokumenKerjaSama.getKeterangan());

        if (canViewDocLink) {
            dto.setLinkDokumen("N/A");
            dto.setDokumen(dokumenKerjaSama.getDokumen());
        } else {
            dto.setLinkDokumen("N/A");
            dto.setDokumen(new ArrayList<>());
        }

        dto.setPenanggungJawabTertandaTangan(dokumenKerjaSama.getPenanggungJawabTertandaTangan());
        dto.setJenisDokumen(dokumenKerjaSama.getJenisDokumen());
        dto.setDeleted(dokumenKerjaSama.isDeleted());

        return dto;
    }

    public Optional<DokumenKerjaSamaLuarNegeri> findById(Long id) {
        Optional<DokumenKerjaSamaLuarNegeri> dklno = dokumenKerjaSamaLuarNegeriRepository.findById(id);
        boolean canViewDocLink = pemakaiService.hasAuthority(PemakaiService.AUTH_CAN_VIEW_DOC_LINK);

        DokumenKerjaSamaLuarNegeri dkln = dklno.get();
        dkln.setLinkDokumen("N/A");

        if (canViewDocLink) {
            return Optional.of(dkln);
        }

        dkln.setDokumen(new ArrayList<>());

        return Optional.of(dkln);
    }

    public DokumenKerjaSamaLuarNegeri save(DokumenKerjaSamaLuarNegeri dokumenKerjaSama) {
        return dokumenKerjaSamaLuarNegeriRepository.save(dokumenKerjaSama);
    }

    public ByteArrayInputStream exportExcel(PagingRequest pagingRequest) throws IOException {
        Page<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSama = getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSamaList = dokumenKerjaSama.getData();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        writeToExcel(workbook, sheet, dokumenKerjaSamaList);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());

    }

    public int writeToExcel(Workbook workbook, Sheet sheet, List<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSamaList) {
        return writeToExcel(workbook, sheet, dokumenKerjaSamaList, 0);
    }

    public int writeToExcel(Workbook workbook, Sheet sheet, List<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSamaList,
            int rowIdx) {
        return writeToExcel(workbook, sheet, dokumenKerjaSamaList, rowIdx, null);
    }

    public int writeToExcel(Workbook workbook, Sheet sheet, List<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSamaList,
            int rowIdx, String title) {

        sheet.setColumnWidth(1, 40 * 256);
        sheet.setColumnWidth(2, 12 * 256);
        sheet.setColumnWidth(3, 40 * 256);
        sheet.setColumnWidth(4, 40 * 256);
        sheet.setColumnWidth(5, 40 * 256);
        sheet.setColumnWidth(6, 40 * 256);
        sheet.setColumnWidth(7, 40 * 256);

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
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 7));
        }

        rowIdx++;
        row = sheet.createRow(rowIdx);

        cell = row.createCell(col++);
        cell.setCellValue("No.");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Subjek");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Tanggal");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Dokumen");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Keterangan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Link Dokumen");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Penanggung Jawab Tertanda Tangan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Jenis Kerja Sama");
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

        for (DokumenKerjaSamaLuarNegeriDTO dokumenKerjaSamaDTO : dokumenKerjaSamaList) {
            col = 0;
            row = sheet.createRow(rowIdx++);
            row.setHeight((short) -1);

            row.createCell(col++).setCellValue(counter++);
            Cell subjek = row.createCell(col++);
            subjek.setCellValue(dokumenKerjaSamaDTO.getSubjek());
            subjek.setCellStyle(wrapStyle);

            Cell tgl = row.createCell(col++);
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
            dateStyle.setBorderBottom(BorderStyle.THIN);
            dateStyle.setBorderTop(BorderStyle.THIN);
            dateStyle.setBorderLeft(BorderStyle.THIN);
            dateStyle.setBorderRight(BorderStyle.THIN);

            tgl.setCellValue(dokumenKerjaSamaDTO.getTgl());
            tgl.setCellStyle(dateStyle);

            Cell judul = row.createCell(col++);
            judul.setCellValue(dokumenKerjaSamaDTO.getJudulDokumen());
            judul.setCellStyle(wrapStyle);

            Cell keterangan = row.createCell(col++);
            keterangan.setCellValue(dokumenKerjaSamaDTO.getKeterangan());
            keterangan.setCellStyle(wrapStyle);

            Cell link = row.createCell(col++);
            link.setCellValue(dokumenKerjaSamaDTO.getLinkDokumen());
            link.setCellStyle(wrapStyle);

            Cell jawab = row.createCell(col++);
            jawab.setCellValue(dokumenKerjaSamaDTO.getPenanggungJawabTertandaTangan());
            jawab.setCellStyle(wrapStyle);

            JenisNaskahLuarNegeri jenisDok = dokumenKerjaSamaDTO.getJenisDokumen();
            row.createCell(col++).setCellValue(jenisDok != null ? jenisDok.getNama() : "");

        }

        return rowIdx;
    }

    public long getExpiredInFirstSemesterCount(int year) {
        return dokumenKerjaSamaLuarNegeriRepository.getExpiredInFirstSemesterCount(year);
    }

    public long getExpiredInYearCount(int year) {
        return dokumenKerjaSamaLuarNegeriRepository.getExpiredInYearCount(year);
    }

    public long getCount(int tahun, JenisNaskahLuarNegeri jenisNaskahLuarNegeri) {
        return dokumenKerjaSamaLuarNegeriRepository.getCount(tahun, jenisNaskahLuarNegeri);
    }

    public long getCount(int year) {
        return dokumenKerjaSamaLuarNegeriRepository.getCount(year);
    }

    public List<DokumenKerjaSamaLuarNegeri> findAll() {
        return dokumenKerjaSamaLuarNegeriRepository.findAll();
    }

    @Transactional
    public List<DokumenKerjaSamaLuarNegeri> findAllJoined() {
        return dokumenKerjaSamaLuarNegeriRepository.findAllJoined();
    }

}
