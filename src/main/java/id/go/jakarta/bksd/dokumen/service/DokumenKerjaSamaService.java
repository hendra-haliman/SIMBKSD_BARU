package id.go.jakarta.bksd.dokumen.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.AsistenSekretarisDaerah;
import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.Dokumen;
import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSama;
import id.go.jakarta.bksd.dokumen.domain.JenisMitra;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskah;
import id.go.jakarta.bksd.dokumen.domain.OPD;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.DokumenKerjaSamaDTO;
import id.go.jakarta.bksd.dokumen.repository.DokumenKerjasamaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class DokumenKerjaSamaService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HttpServletRequest request;

    private DokumenKerjasamaRepository dokumenKerjasamaRepository;

    private OPDService opdService;

    private BagianService bagianService;

    private JenisMitraService jenisMitraService;

    private AsistenSekretarisDaerahService asistenSekretarisDaerahService;

    private ModelMapper modelMapper;

    private JenisNaskahService jenisNaskahService;

    private PemakaiService pemakaiService;

    @PersistenceContext
    private EntityManager entityManager;

    public DokumenKerjaSamaService(DokumenKerjasamaRepository dokumenKerjasamaRepository,
            OPDService opdService,
            BagianService bagianService,
            JenisMitraService jenisMitraService,
            AsistenSekretarisDaerahService asistenSekretarisDaerahService,
            ModelMapper mapper,
            JenisNaskahService jenisNaskahService,
            PemakaiService pemakaiService) {
        this.dokumenKerjasamaRepository = dokumenKerjasamaRepository;
        this.opdService = opdService;
        this.bagianService = bagianService;
        this.jenisMitraService = jenisMitraService;
        this.modelMapper = mapper;
        this.asistenSekretarisDaerahService = asistenSekretarisDaerahService;
        this.jenisNaskahService = jenisNaskahService;
        this.pemakaiService = pemakaiService;
    }

    /**
     * Export to Excel
     * TODO: make separate worksheet for KPT, KDN, KLN, NPBK ?
     * atau buat rekap saja ? group by bidang ?
     * 
     * @param pagingRequest
     * @return
     * @throws IOException
     */
    public ByteArrayInputStream exportExcel(PagingRequest pagingRequest) throws IOException {
        Page<DokumenKerjaSamaDTO> dokumenKerjaSama = getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaDTO> dokumenKerjaSamaList = dokumenKerjaSama.getData();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        String url = pagingRequest.getUrl();

        writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, url);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public String getFullUrl() {
        String scheme = request.getScheme(); // https
        String serverName = request.getServerName(); // localhost
        int serverPort = request.getServerPort(); // 8443
        String contextPath = request.getContextPath(); // /dokumen

        // Construct the full URL
        return scheme + "://" + serverName + ":" + serverPort + contextPath;
    }

    public int writeDokumenKerjasamaToExcel(Workbook workbook, Sheet sheet,
            List<DokumenKerjaSamaDTO> dokumenKerjaSamaList, int rowIdx) {
        return writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, getFullUrl(),
                rowIdx);
    }

    public int writeDokumenKerjasamaToExcel(Workbook workbook, Sheet sheet,
            List<DokumenKerjaSamaDTO> dokumenKerjaSamaList) {
        return writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, getFullUrl(), 0);
    }

    public int writeDokumenKerjasamaToExcel(Workbook workbook, Sheet sheet,
            List<DokumenKerjaSamaDTO> dokumenKerjaSamaList, String url) {
        return writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, url, 0);
    }

    public int writeDokumenKerjasamaToExcel(Workbook workbook, Sheet sheet,
            List<DokumenKerjaSamaDTO> dokumenKerjaSamaList, String url, String title) {
        return writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, url, 0, title);
    }

    public int writeDokumenKerjasamaToExcel(Workbook workbook, Sheet sheet,
            List<DokumenKerjaSamaDTO> dokumenKerjaSamaList, int rowIdx, String title) {
        return writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, getFullUrl(), 0,
                title);
    }

    public int writeDokumenKerjasamaToExcel(Workbook workbook, Sheet sheet,
            List<DokumenKerjaSamaDTO> dokumenKerjaSamaList, String url, int rowIdx) {
        return writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, url, rowIdx, null);
    }

    public int writeDokumenKerjasamaToExcel(Workbook workbook, Sheet sheet,
            List<DokumenKerjaSamaDTO> dokumenKerjaSamaList, String url, int rowIdx, String title) {

        boolean canViewDocLink = pemakaiService.hasAuthority(PemakaiService.AUTH_CAN_VIEW_DOC_LINK);

        sheet.setColumnWidth(1, 20 * 256);

        sheet.setColumnWidth(2, 40 * 256);
        sheet.setColumnWidth(3, 40 * 256);

        sheet.setColumnWidth(5, 40 * 256);
        sheet.setColumnWidth(6, 50 * 256);
        sheet.setColumnWidth(7, 30 * 256);
        sheet.setColumnWidth(8, 14 * 256);
        sheet.setColumnWidth(9, 30 * 256);
        sheet.setColumnWidth(10, 30 * 256);
        sheet.setColumnWidth(11, 40 * 256);
        sheet.setColumnWidth(12, 40 * 256);
        sheet.setColumnWidth(13, 40 * 256);
        sheet.setColumnWidth(14, 40 * 256);

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
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 11));
        }

        rowIdx++;
        row = sheet.createRow(rowIdx);
        col = 0;
        cell = row.createCell(col++);
        cell.setCellValue("Bagian");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Kelompok");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Instansi Terkait");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Rumpun OPD");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("No.");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Mitra Kerja Sama");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Judul Dokumen Perjanjian");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("No Dokumen Perjanjian");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Tanggal Penetapan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Urusan Pemerintahan yg Dikerjakan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Jenis Dokumen Perjanjian");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Jangka Waktu Pelaksanaan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Pembiayaan");
        cell.setCellStyle(headerCellStyle);

        if (canViewDocLink) {
            cell = row.createCell(col++);
            cell.setCellValue("File Terkait");
            cell.setCellStyle(headerCellStyle);
        }

        // Catatan : Removed link to google drive dokumen.
        // cell = row.createCell(col++);
        // cell.setCellValue("Link Dokumen");
        // cell.setCellStyle(headerCellStyle);

        rowIdx++;

        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);
        wrapStyle.setBorderBottom(BorderStyle.THIN);
        wrapStyle.setBorderTop(BorderStyle.THIN);
        wrapStyle.setBorderLeft(BorderStyle.THIN);
        wrapStyle.setBorderRight(BorderStyle.THIN);
        wrapStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        wrapStyle.setAlignment(HorizontalAlignment.CENTER);

        // Style the cell to look like a hyperlink
        CellStyle hlinkStyle = workbook.createCellStyle();
        Font hlinkFont = workbook.createFont();
        hlinkFont.setUnderline(Font.U_SINGLE);
        hlinkFont.setColor(IndexedColors.BLUE.getIndex());
        hlinkStyle.setFont(hlinkFont);
        hlinkStyle.setWrapText(true);
        hlinkStyle.setBorderBottom(BorderStyle.THIN);
        hlinkStyle.setBorderTop(BorderStyle.THIN);
        hlinkStyle.setBorderLeft(BorderStyle.THIN);
        hlinkStyle.setBorderRight(BorderStyle.THIN);
        hlinkStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        int counter = 1;

        for (DokumenKerjaSamaDTO dokumenKerjaSamaDTO : dokumenKerjaSamaList) {
            col = 0;
            row = sheet.createRow(rowIdx++);
            row.setHeight((short) -1);

            Bagian bagian = dokumenKerjaSamaDTO.getBagian();
            Cell cellBagian = row.createCell(col++);
            cellBagian.setCellValue(bagian != null ? bagian.getSingkatan() : "");
            cellBagian.setCellStyle(wrapStyle);

            JenisMitra jenisMitra = dokumenKerjaSamaDTO.getJenisMitra();
            Cell jenisMitraCell = row.createCell(col++);
            jenisMitraCell.setCellValue(jenisMitra != null ? jenisMitra.getNama() : "");
            jenisMitraCell.setCellStyle(wrapStyle);

            List<OPD> opdList = dokumenKerjaSamaDTO.getOpd();
            Cell opdCell = row.createCell(col++);
            opdCell.setCellValue(OPDService.getString(opdList));
            opdCell.setCellStyle(wrapStyle);

            List<AsistenSekretarisDaerah> asistenList = dokumenKerjaSamaDTO.getRumpun_opd();
            Cell asistenCell = row.createCell(col++);
            asistenCell.setCellValue(AsistenSekretarisDaerahService.getString(asistenList));
            asistenCell.setCellStyle(wrapStyle);

            Cell numberCell = row.createCell(col++);
            numberCell.setCellValue(counter++);
            numberCell.setCellStyle(wrapStyle);
            // row.createCell(col++).setCellValue(rowIdx - 1);

            Cell mitraKerjaSama = row.createCell(col++);
            mitraKerjaSama.setCellValue(dokumenKerjaSamaDTO.getMitraKerjaSama());
            mitraKerjaSama.setCellStyle(wrapStyle);

            Cell judulCell = row.createCell(col++);
            judulCell.setCellValue(dokumenKerjaSamaDTO.getJudulDokumenPerjanjian());
            judulCell.setCellStyle(wrapStyle);

            Cell noDokumenCell = row.createCell(col++);
            noDokumenCell.setCellValue(dokumenKerjaSamaDTO.getNoDokumenPerjanjian());
            noDokumenCell.setCellStyle(wrapStyle);

            Cell tglPenetapan = row.createCell(col++);
            CellStyle dateStyle = workbook.createCellStyle();

            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
            dateStyle.setBorderBottom(BorderStyle.THIN);
            dateStyle.setBorderTop(BorderStyle.THIN);
            dateStyle.setBorderLeft(BorderStyle.THIN);
            dateStyle.setBorderRight(BorderStyle.THIN);
            dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dateStyle.setAlignment(HorizontalAlignment.CENTER);

            tglPenetapan.setCellValue(dokumenKerjaSamaDTO.getTglPenetapan());
            tglPenetapan.setCellStyle(dateStyle);

            Cell urusanCell = row.createCell(col++);
            urusanCell.setCellValue(dokumenKerjaSamaDTO.getUrusanPemerintahanYgDikerjakan());
            urusanCell.setCellStyle(wrapStyle);

            JenisNaskah jenisDokumen = dokumenKerjaSamaDTO.getJenisDokumen();
            Cell jenisNaskahCell = row.createCell(col++);
            jenisNaskahCell.setCellValue(jenisDokumen != null ? jenisDokumen.getNama() : "");
            jenisNaskahCell.setCellStyle(wrapStyle);
            // row.createCell(col++).setCellValue(jenisDokumen != null ?
            // jenisDokumen.getNama() : "");

            Cell jangaWaktuCell = row.createCell(col++);
            jangaWaktuCell.setCellValue(dokumenKerjaSamaDTO.getJangkaWaktuPelaksanaan());
            jangaWaktuCell.setCellStyle(wrapStyle);

            Cell pembiayaanCell = row.createCell(col++);
            pembiayaanCell.setCellValue(dokumenKerjaSamaDTO.getPembiayaan());
            pembiayaanCell.setCellStyle(wrapStyle);

            // File terkait
            String preLink = url + "/dokumen/download/";

            String docLinks = dokumenKerjaSamaDTO.getDokumen().stream().map(el -> preLink + el.getFilename())
                    .collect(Collectors.joining(" "));
            logger.info("DOCLINKS:::[{}]", docLinks);
            docLinks = docLinks.replace("+", "%2B");
            docLinks = docLinks.replace(" ", "%20");
            docLinks = docLinks.replace("[", "%5B");
            docLinks = docLinks.replace("]", "%5D");
            logger.info("NO>>>[{}]", dokumenKerjaSamaDTO.getNoDokumenPerjanjian());
            logger.info("DOCLINK>>>[{}]", docLinks);

            if (canViewDocLink) {

                Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
                link.setAddress(docLinks);

                Cell fileTerkaitCell = row.createCell(col++);
                fileTerkaitCell.setHyperlink(link);
                if (dokumenKerjaSamaDTO.getDokumen() != null && dokumenKerjaSamaDTO.getDokumen().size() > 0) {
                    fileTerkaitCell.setCellValue(dokumenKerjaSamaDTO.getDokumen() == null ? ""
                            : dokumenKerjaSamaDTO.getDokumen().get(0).getFilename());
                }
                fileTerkaitCell.setCellStyle(hlinkStyle);

            }

            // Catatan: Remove google drive dokumen link
            // Cell linkDokCell = row.createCell(col++);
            // linkDokCell.setCellValue(dokumenKerjaSamaDTO.getLinkDokumenKerjaSama());
            // linkDokCell.setCellStyle(wrapStyle);
        }

        return rowIdx;
    }

    public Page<DokumenKerjaSamaDTO> getDokumen(PagingRequest pagingRequest) {
        int pageNo = pagingRequest.getStart() / pagingRequest.getLength();
        int pageSize = pagingRequest.getLength();

        return getDokumen2(pagingRequest, pageNo, pageSize);
    }

    public Page<DokumenKerjaSamaDTO> getDokumen2(PagingRequest pagingRequest, int pageNo, int pageSize) {
        boolean canViewDocLink = pemakaiService.hasAuthority(PemakaiService.AUTH_CAN_VIEW_DOC_LINK);

        List<Long> filterBagian = pagingRequest.getFilterBagian();
        List<Long> filterOPD = pagingRequest.getFilterOPD();
        List<Long> filterJenisMitra = pagingRequest.getFilterJenisMitra();
        Date filterStartDate = pagingRequest.getFilterStartDate();
        Date filterEndDate = pagingRequest.getFilterEndDate();
        int filterTahun = pagingRequest.getFilterTahun();
        String searchTerm = "%" + pagingRequest.getSearch().getValue() + "%";

        Date filterTglBerakhirStartDate = pagingRequest.getFilterTglBerakhirStartDate();
        Date filterTglBerakhirEndDate = pagingRequest.getFilterTglBerakhirEndDate();
        boolean includeNullTglBerakhir = pagingRequest.isIncludeNullTglBerakhir();

        boolean filterEmptyFile = pagingRequest.isFilterEmptyFile();

        boolean filterEmptyRumpunOPD = pagingRequest.isFilterEmptyRumpunOPD();

        List<Long> filterAsisten = pagingRequest.getFilterAsisten();

        List<Long> filterJenisNaskah = pagingRequest.getFilterJenisNaskah();

        // logger.info("SIZE [{}]", filterOPD.size());

        boolean includeNullBagian = false;
        if (filterBagian.isEmpty()) {
            filterBagian = bagianService.findAll().stream().map(Bagian::getId).collect(Collectors.toList());
            includeNullBagian = true;
        }

        boolean includeNullOPD = false;
        if (filterOPD.isEmpty()) {
            filterOPD = opdService.findAll().stream().map(OPD::getId).collect(Collectors.toList());
            includeNullOPD = true;
        }

        boolean includeNullJenisMitra = false;
        if (filterJenisMitra.isEmpty()) {
            filterJenisMitra = jenisMitraService.findAll().stream().map(JenisMitra::getId).collect(Collectors.toList());
            includeNullJenisMitra = true;
        }

        if (filterStartDate == null) {
            filterStartDate = new Date(70, 01, 01);
        }

        if (filterEndDate == null) {
            filterEndDate = new Date();
        }

        if (filterTglBerakhirStartDate == null) {
            filterTglBerakhirStartDate = new Date(70, 01, 01);
        }

        if (filterTglBerakhirEndDate == null) {
            filterTglBerakhirEndDate = new Date(2000, 12, 31);
        }

        boolean includeNullAsisten = false;
        if (filterAsisten.isEmpty()) {
            filterAsisten = asistenSekretarisDaerahService.findAll().stream().map(AsistenSekretarisDaerah::getId)
                    .collect(Collectors.toList());
            includeNullAsisten = true;
        }

        logger.info("Filter JN:::[{}]", filterJenisNaskah);

        if (filterJenisNaskah.isEmpty()) {
            filterJenisNaskah = jenisNaskahService.findAll().stream().map(JenisNaskah::getId)
                    .collect(Collectors.toList());

        }

        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();

        JpaCriteriaQuery<DokumenKerjaSama> cq = cb.createQuery(DokumenKerjaSama.class);
        Root<DokumenKerjaSama> dokumenKerjaSama = cq.from(DokumenKerjaSama.class);
        //Join<DokumenKerjaSama, Dokumen> childrenJoin = dokumenKerjaSama.join("dokumen", JoinType.LEFT);

        // Filter bagian, opd, jenis mitra, tgl penetapan date range & year
        Predicate bagianPredicate = dokumenKerjaSama.get("bagian").get("id").in(filterBagian);
        Predicate nullBagianPredicate = cb.isNull(dokumenKerjaSama.get("bagian"));

        Predicate opdPredicate = dokumenKerjaSama.get("opd").get("id").in(filterOPD);

        Predicate nullOPDPredicate = cb.isNull(dokumenKerjaSama.get("opd"));

        Predicate jenisMitraPredicate = dokumenKerjaSama.get("jenisMitra").get("id").in(filterJenisMitra);
        Predicate nullJenisMitraPredicate = cb.isNull(dokumenKerjaSama.get("jenisMitra"));
        Predicate filterStartDatePredicate = cb.greaterThanOrEqualTo(dokumenKerjaSama.get("tglPenetapan"),
                filterStartDate);
        Predicate filterEndDatePredicate = cb.lessThanOrEqualTo(dokumenKerjaSama.get("tglPenetapan"), filterEndDate);
        Predicate filterTahunPredicate = cb.equal(dokumenKerjaSama.get("tahun"), filterTahun);

        // Filter Asisten
        Predicate asistenPredicate = dokumenKerjaSama.get("rumpun_opd").get("id").in(filterAsisten);
        Predicate nullAsistenPredicate = cb.or(cb.isNull(dokumenKerjaSama.get("opd")),
                cb.isNull(dokumenKerjaSama.get("opd").get("asisten")));

        // filter buat Waktu berakhir
        Predicate filterTglBerakhir = cb.and(
                cb.greaterThanOrEqualTo(dokumenKerjaSama.get("waktuPelaksanaanBerakhir"), filterTglBerakhirStartDate),
                cb.lessThanOrEqualTo(dokumenKerjaSama.get("waktuPelaksanaanBerakhir"), filterTglBerakhirEndDate));

        Predicate filterNullTglBerakhir = cb.isNull(dokumenKerjaSama.get("waktuPelaksanaanBerakhir"));

        Predicate searchJudulPredicate = cb.like(dokumenKerjaSama.get("judulDokumenPerjanjian"), searchTerm);
        Predicate searchNomorPredicate = cb.like(dokumenKerjaSama.get("noDokumenPerjanjian"), searchTerm);
        Predicate searchMitraPredicate = cb.like(dokumenKerjaSama.get("mitraKerjaSama"), searchTerm);
        Predicate searchFileContentPredicate = cb.like(dokumenKerjaSama.get("fileContent"), searchTerm);
        
        //Predicate searchFileNamePredicate = cb.like(dokumenKerjaSama.get("dokumen").get("filename"), searchTerm);

        // Predicate searchPredicate = cb.or(searchJudulPredicate, searchNomorPredicate, searchMitraPredicate,
        //         searchFileContentPredicate, searchFileNamePredicate);

        Predicate searchPredicate = cb.or(searchJudulPredicate, searchNomorPredicate, searchMitraPredicate,
        searchFileContentPredicate);

        Predicate deletedPredicate = cb.equal(dokumenKerjaSama.get("deleted"), false);

        Predicate jenisNaskahPredicate = dokumenKerjaSama.get("jenisDokumen").get("id").in(filterJenisNaskah);

        Predicate filterEmptyFilePredicate = cb.isEmpty(dokumenKerjaSama.get("dokumen"));

        Predicate filterEmptyRumpunOPDPredicate = cb.isEmpty(dokumenKerjaSama.get("rumpun_opd"));

        JpaPredicate jpaPredicate = cb.and(bagianPredicate, searchPredicate, deletedPredicate);
        JpaPredicate datePredicate = cb.or(filterTahunPredicate,
                cb.and(filterStartDatePredicate, filterEndDatePredicate));

        jpaPredicate = cb.and(jpaPredicate, datePredicate);

        if (includeNullOPD) {
            // jpaPredicate = cb.and(jpaPredicate, cb.or(opdPredicate, nullOPDPredicate));
        } else {
            jpaPredicate = cb.and(jpaPredicate, opdPredicate);
        }

        if (includeNullJenisMitra) {
            jpaPredicate = cb.and(jpaPredicate, cb.or(jenisMitraPredicate,
                    nullJenisMitraPredicate));
        } else {
            jpaPredicate = cb.and(jpaPredicate, jenisMitraPredicate);
        }

        if (includeNullAsisten) {
            // jpaPredicate = cb.and(jpaPredicate, cb.or(asistenPredicate,
            // nullAsistenPredicate));
        } else {
            jpaPredicate = cb.and(jpaPredicate, asistenPredicate);
        }

        // add filter tgl bearkhir. Catatan: database musti di fix to include tgl
        // berakhir.
        if (includeNullTglBerakhir) {
            jpaPredicate = cb.and(jpaPredicate, cb.or(filterTglBerakhir, filterNullTglBerakhir));

        } else {
            jpaPredicate = cb.and(jpaPredicate, filterTglBerakhir);

        }

        if (filterEmptyFile) {
            jpaPredicate = cb.and(jpaPredicate, filterEmptyFilePredicate);
        }

        if (filterEmptyRumpunOPD) {
            jpaPredicate = cb.and(jpaPredicate, filterEmptyRumpunOPDPredicate);
        }

        jpaPredicate = cb.and(jpaPredicate, deletedPredicate);

        jpaPredicate = cb.and(jpaPredicate, jenisNaskahPredicate);

        cq.where(jpaPredicate);

        cq.orderBy(
                cb.desc(dokumenKerjaSama.get("tahun")),
                cb.asc(dokumenKerjaSama.get("bagian")),
                // cb.asc(dokumenKerjaSama.get("opd")),
                cb.asc(dokumenKerjaSama.get("jenisMitra")));

        cq.select(dokumenKerjaSama);    

        List<DokumenKerjaSama> dokumenKerjaSamaList = entityManager.createQuery(cq)
                .setFirstResult(pageNo * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        logger.info("RESLT_LIST_COUNT:::[{}]", dokumenKerjaSamaList.size());

        TypedQuery<Long> countQuery = entityManager.createQuery(cq.createCountQuery());

        Long totalElements = countQuery.getSingleResult();

        Page<DokumenKerjaSamaDTO> page = new Page<>(
                dokumenKerjaSamaList.stream().map(dks -> convertToDto(dks, canViewDocLink))
                        .collect(Collectors.toList()));
        page.setRecordsFiltered((int) totalElements.longValue());
        page.setRecordsTotal((int) totalElements.longValue());
        page.setDraw(pagingRequest.getDraw());

        return page;
    }

    private DokumenKerjaSamaDTO convertToDto(DokumenKerjaSama dokumenKerjaSama, boolean canViewDocLink) {
        // return modelMapper.map(dokumenKerjaSama, DokumenKerjaSamaDTO.class);

        DokumenKerjaSamaDTO dto = new DokumenKerjaSamaDTO();
        dto.setId(dokumenKerjaSama.getId());
        dto.setBagian(dokumenKerjaSama.getBagian());
        dto.setJenisMitra(dokumenKerjaSama.getJenisMitra());
        dto.setMitraKerjaSama(dokumenKerjaSama.getMitraKerjaSama());
        dto.setOpd(dokumenKerjaSama.getOpd());
        dto.setRumpun_opd(dokumenKerjaSama.getRumpun_opd());
        dto.setJudulDokumenPerjanjian(dokumenKerjaSama.getJudulDokumenPerjanjian());
        dto.setNoDokumenPerjanjian(dokumenKerjaSama.getNoDokumenPerjanjian());
        dto.setTglPenetapan(dokumenKerjaSama.getTglPenetapan());
        dto.setTahun(dokumenKerjaSama.getTahun());
        dto.setUrusanPemerintahanYgDikerjakan(dokumenKerjaSama.getUrusanPemerintahanYgDikerjakan());
        dto.setJenisDokumen(dokumenKerjaSama.getJenisDokumen());
        dto.setJangkaWaktuPelaksanaan(dokumenKerjaSama.getJangkaWaktuPelaksanaan());
        dto.setJangkaWaktuPelaksanaan(dokumenKerjaSama.getJangkaWaktuPelaksanaan());
        dto.setWaktuPelaksanaanDimulai(dokumenKerjaSama.getWaktuPelaksanaanDimulai());
        dto.setWaktuPelaksanaanBerakhir(dokumenKerjaSama.getWaktuPelaksanaanBerakhir());
        dto.setPembiayaan(dokumenKerjaSama.getPembiayaan());
        dto.setFilesTelahDiperoleh(dokumenKerjaSama.isFilesTelahDiperoleh());
        dto.setDeleted(dokumenKerjaSama.isDeleted());

        if (canViewDocLink) {
            dto.setDokumen(dokumenKerjaSama.getDokumen());
            // dto.setLinkDokumenKerjaSama(dokumenKerjaSama.getLinkDokumenKerjaSama());
            dto.setLinkDokumenKerjaSama("N/A");
        } else {
            dto.setDokumen(new ArrayList<>());
            dto.setLinkDokumenKerjaSama("N/A");
        }

        dto.setKeterangan(dokumenKerjaSama.getKeterangan());
        dto.setKeterangan2(dokumenKerjaSama.getKeterangan2());
        dto.setKeterangan3(dokumenKerjaSama.getKeterangan3());
        dto.setKeterangan4(dokumenKerjaSama.getKeterangan4());

        return dto;
    }

    /**
     * Mencheck dokumen kerjasama apakah files terkait telah diperoleh.
     * Yang dimaksud dengan diperoleh apakah files terkait tersebut telah berada
     * pada server (tidak pada google drive)
     */
    public void checkFiles() {

    }

    @Transactional
    public List<DokumenKerjaSama> findAll() {
        return dokumenKerjasamaRepository.findAll();
    }

    public List<DokumenKerjaSama> findAllFetchDokumen() {
        return dokumenKerjasamaRepository.findAllFetchDokumen();
    }

    public Optional<DokumenKerjaSama> findById(Long id) {
        Optional<DokumenKerjaSama> dkso = dokumenKerjasamaRepository.findById(id);
        boolean canViewDocLink = pemakaiService.hasAuthority(PemakaiService.AUTH_CAN_VIEW_DOC_LINK);

        DokumenKerjaSama dks = dkso.get();
        dks.setLinkDokumenKerjaSama("N/A");

        if (canViewDocLink) {
            return Optional.of(dks);
        }

        dks.setDokumen(new ArrayList<>());
        return Optional.of(dks);
    }

    public DokumenKerjaSama save(DokumenKerjaSama dokumenKerjaSama) {
        return dokumenKerjasamaRepository.save(dokumenKerjaSama);
    }

    public long getExpiredInFirstSemesterCount(int year) {
        return dokumenKerjasamaRepository.getExpiredInFirstSemesterCount(year);
    }

    public long getExpiredInFirstSemesterByBagianCount(Bagian bagian, int year) {
        return dokumenKerjasamaRepository.getExpiredInFirstSemesterByBagianCount(bagian.getId(), year);
    }

    public long getExpiredInYearCount(int year) {
        return dokumenKerjasamaRepository.getExpiredInYearCount(year);
    }

    public long getExpiredInYearByBagianCount(Bagian bagian, int year) {
        return dokumenKerjasamaRepository.getExpiredInYearByBagianCount(bagian.getId(), year);
    }

    public long getCount(int tahun, List<JenisNaskah> jenisNaskahList, List<JenisMitra> jenisMitraList) {
        return dokumenKerjasamaRepository.getCount(tahun, jenisNaskahList, jenisMitraList);
    }

    public long getCount(int tahun, JenisNaskah jenisNaskah, List<JenisMitra> jenisMitraList) {
        return dokumenKerjasamaRepository.getCount(tahun, jenisNaskah, jenisMitraList);
    }

    public long getCount(int tahun, Bagian bagian, JenisNaskah jenisNaskah, List<JenisMitra> jenisMitraList) {
        return dokumenKerjasamaRepository.getCount(tahun, bagian, jenisNaskah, jenisMitraList);
    }

    public long getCount(int tahun, JenisNaskah jenisNaskah) {
        return dokumenKerjasamaRepository.getCount(tahun, jenisNaskah);
    }

    public long getCount(int tahun, List<JenisNaskah> jenisNaskahList, int dummy) {
        return dokumenKerjasamaRepository.getCount(tahun, jenisNaskahList, dummy);
    }

    public long getCount(int tahun, List<JenisMitra> jenisMitraList) {
        return dokumenKerjasamaRepository.getCount(tahun, jenisMitraList);
    }

    public long getCount(int tahun, Bagian bagian, JenisNaskah jenisNaskah) {
        return dokumenKerjasamaRepository.getCount(tahun, bagian, jenisNaskah);
    }

    public long getCount(int tahun, Bagian bagian) {
        return dokumenKerjasamaRepository.getCount(tahun, bagian);
    }

}
