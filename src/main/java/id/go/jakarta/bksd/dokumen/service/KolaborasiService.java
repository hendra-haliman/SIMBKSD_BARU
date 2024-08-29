package id.go.jakarta.bksd.dokumen.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.Kolaborasi;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.repository.KolaborasiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class KolaborasiService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private KolaborasiRepository kolaborasiRepository;
    private BagianService bagianService;
    private ModelMapper modelMapper;
    private PemakaiService pemakaiService;

    @PersistenceContext
    private EntityManager entityManager;

    public KolaborasiService(KolaborasiRepository kolaborasiRepository,
            BagianService bagianService,
            ModelMapper mapper,
            PemakaiService pemakaiService) {
        this.kolaborasiRepository = kolaborasiRepository;
        this.bagianService = bagianService;
        this.modelMapper = mapper;
        this.pemakaiService = pemakaiService;
    }

    public List<Kolaborasi> findAll() {
        return kolaborasiRepository.findAll();
    }

    public ByteArrayInputStream exportExcel(PagingRequest pagingRequest) throws IOException {
        Page<Kolaborasi> kolaborasi = getKolaborasi(pagingRequest, 0, 1000000);
        List<Kolaborasi> kolaborasiList = kolaborasi.getData();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        writeDokumenKerjasamaToExcel(workbook, sheet, kolaborasiList);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public Page<Kolaborasi> getKolaborasi(PagingRequest pagingRequest) {
        int pageNo = pagingRequest.getStart() / pagingRequest.getLength();
        int pageSize = pagingRequest.getLength();

        return getKolaborasi(pagingRequest, pageNo, pageSize);
    }

    public Page<Kolaborasi> getKolaborasi(PagingRequest pagingRequest, int pageNo, int pageSize) {

        int tahun = pagingRequest.getFilterTahun();
        logger.info("tahun:::{}", tahun);
        String searchTerm = "%" + pagingRequest.getSearch().getValue() + "%";

        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();
        JpaCriteriaQuery<Kolaborasi> cq = cb.createQuery(Kolaborasi.class);
        Root<Kolaborasi> kolaborasi = cq.from(Kolaborasi.class);

        Predicate tahunPredicate;
        if (tahun == 0) {
            tahunPredicate = cb.greaterThan(kolaborasi.get("tahun"), 0);
        } else {
            tahunPredicate = cb.equal(kolaborasi.get("tahun"), tahun);
        }

        Predicate searchPredicate = cb.like(kolaborasi.get("namaDanTempatKejadian"), searchTerm);

        JpaPredicate predicate = cb.and(tahunPredicate, searchPredicate);

        cq.where(predicate);

        cq.select(kolaborasi);

        List<Kolaborasi> kolaborasiList = entityManager.createQuery(cq)
                .setFirstResult(pageNo * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        TypedQuery<Long> countQuery = entityManager.createQuery(cq.createCountQuery());

        Long totalElements = countQuery.getSingleResult();

        Page<Kolaborasi> kolaborasiPage = new Page<>(kolaborasiList);
        kolaborasiPage.setRecordsFiltered((int) totalElements.longValue());
        kolaborasiPage.setRecordsTotal((int) totalElements.longValue());
        kolaborasiPage.setDraw(pagingRequest.getDraw());

        return kolaborasiPage;
    }

    public Kolaborasi save(Kolaborasi kolaborasi) {
        return kolaborasiRepository.save(kolaborasi);
    }

    public void writeDokumenKerjasamaToExcel(Workbook workbook, Sheet sheet, List<Kolaborasi> kolaborasiList) {
        sheet.setColumnWidth(1, 40 * 256);
        sheet.setColumnWidth(2, 40 * 256);
        sheet.setColumnWidth(3, 20 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 20 * 256);

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
        cell.setCellValue("Mitra Kerja Sama");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Nama dan Tempat Kejadian");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Waktu");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Bagian");
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

        for (Kolaborasi kolaborasi : kolaborasiList) {
            col = 0;
            row = sheet.createRow(rowIdx++);
            row.setHeight((short) -1);

            Cell numberCell = row.createCell(col++);
            numberCell.setCellValue(rowIdx - 1);
            numberCell.setCellStyle(wrapStyle);

            Cell mitraCell = row.createCell(col++);
            mitraCell.setCellValue(kolaborasi.getMitraKerjaSama());
            mitraCell.setCellStyle(wrapStyle);

            Cell namaTempatKejadianCell = row.createCell(col++);
            namaTempatKejadianCell.setCellValue(kolaborasi.getNamaDanTempatKejadian());
            namaTempatKejadianCell.setCellStyle(wrapStyle);

            Cell waktuCell = row.createCell(col++);
            waktuCell.setCellValue(kolaborasi.getWaktu());
            waktuCell.setCellStyle(wrapStyle);

            Cell bagianCell = row.createCell(col++);
            bagianCell.setCellValue(convertToString(kolaborasi.getBagian()));
            bagianCell.setCellStyle(wrapStyle);

            Cell keteranganCell = row.createCell(col);
            keteranganCell.setCellValue(kolaborasi.getKeterangan());
            keteranganCell.setCellStyle(wrapStyle);
        }

    }

    private String convertToString(List<Bagian> bagianList) {
        if (bagianList == null || bagianList.isEmpty()) {
            return "";
        }

        if (bagianList.size() == 1) {
            return bagianList.get(0).getSingkatan();
        }

        StringBuffer sb = new StringBuffer();
        int counter = 1;
        for (Bagian bagian : bagianList) {
            sb.append(counter + ". ");
            sb.append(bagian.getSingkatan());
            sb.append("\n");
            counter++;
        }

        return sb.toString();
    }

    public Kolaborasi findById(Long id) {
        return kolaborasiRepository.findById(id).get();
    }

    public long getCount(int tahun) {
        return kolaborasiRepository.getCount(tahun);
    }

}
