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
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.OPD;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.Pemetaan;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.PemetaanDTO;
import id.go.jakarta.bksd.dokumen.repository.PemetaanRepository;

@Service
public class PemetaanService {
    private PemetaanRepository pemetaanRepository;
    private BagianService bagianService;
    private ModelMapper modelMapper;

    public PemetaanService(PemetaanRepository pemetaanRepository,
            BagianService bagianService,
            ModelMapper mapper) {
        this.pemetaanRepository = pemetaanRepository;
        this.bagianService = bagianService;
        this.modelMapper = mapper;
    }

    public Pemetaan save(Pemetaan pemetaan) {
        return pemetaanRepository.save(pemetaan);
    }

    public Page<PemetaanDTO> getPemetaan(PagingRequest pagingRequest) {
        int pageNo = pagingRequest.getStart() / pagingRequest.getLength();
        int pageSize = pagingRequest.getLength();

        return getPemetaan(pagingRequest, pageNo, pageSize);
    }

    public Page<PemetaanDTO> getPemetaan(PagingRequest pagingRequest, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        String keyword = pagingRequest.getSearch().getValue();

        int filterTahun = pagingRequest.getFilterTahun();

        org.springframework.data.domain.Page<Pemetaan> pemetaanPage;

        if (filterTahun == 0) {
            // pemetaanPage = pemetaanRepository.findByKeyword(pageable,
            // pagingRequest.getSearch().getValue());
            pemetaanPage = pemetaanRepository.findByKeyword(pageable, keyword);
        } else {
            // pemetaanPage = pemetaanRepository.findByKeywordAndTahun(pageable,
            // pagingRequest.getSearch().getValue(),
            // filterTahun);
            pemetaanPage = pemetaanRepository.findByKeywordAndTahun(pageable,
                    filterTahun, keyword);
        }

        Page<PemetaanDTO> page = new Page<>(
                pemetaanPage.getContent().stream().map(this::convertToDTO).collect(Collectors.toList()));
        page.setRecordsFiltered((int) pemetaanPage.getTotalElements());
        page.setRecordsTotal((int) pemetaanPage.getTotalElements());
        page.setDraw(pagingRequest.getDraw());

        return page;
    }

    private PemetaanDTO convertToDTO(Pemetaan pemetaan) {
        return modelMapper.map(pemetaan, PemetaanDTO.class);
    }

    public Optional<Pemetaan> findById(Long id) {
        return pemetaanRepository.findById(id);
    }

    public ByteArrayInputStream exportExcel(PagingRequest pagingRequest) throws IOException {
        Page<PemetaanDTO> pemetaan = getPemetaan(pagingRequest, 0, 1000000);
        List<PemetaanDTO> pemetaanList = pemetaan.getData();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        sheet.setColumnWidth(3, 30 * 256);
        sheet.setColumnWidth(4, 20 * 256);
        sheet.setColumnWidth(5, 40 * 256);
        sheet.setColumnWidth(6, 30 * 256);
        sheet.setColumnWidth(7, 40 * 256);
        sheet.setColumnWidth(8, 30 * 256);
        sheet.setColumnWidth(9, 30 * 256);
        sheet.setColumnWidth(10, 20 * 256);
        sheet.setColumnWidth(15, 30 * 256);
        sheet.setColumnWidth(16, 30 * 256);

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

        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints((float) 40.0);
        int col = 0;

        Cell cell = row.createCell(col++);
        cell.setCellValue("No.");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Tahun");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Bagian");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Perangkat Daerah");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Urusan Pemerintahan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Kegiatan / Program Kerja Sama");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Jangka Waktu");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Nama Calon Mitra");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Nomor Kontrak");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Waktu Pelaksanaan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Skala Prioritas");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("TW1");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("TW2");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("TW3");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("TW4");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("PIC");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(col++);
        cell.setCellValue("Pembiayaan");
        cell.setCellStyle(headerCellStyle);

        rowIdx++;

        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);

        for (PemetaanDTO pemetaanDTO : pemetaanList) {
            col = 0;
            row = sheet.createRow(rowIdx++);
            row.setHeight((short) -1);

            row.createCell(col++).setCellValue(rowIdx - 1);
            row.createCell(col++).setCellValue(pemetaanDTO.getTahun());

            Cell bagianCell = row.createCell(col++);
            Bagian bagian = pemetaanDTO.getBagian();
            bagianCell.setCellValue(bagian != null ? bagian.getNama() : "");
            bagianCell.setCellStyle(wrapStyle);

            Cell opdCell = row.createCell(col++);
            OPD opd = pemetaanDTO.getOpd();
            opdCell.setCellValue(opd != null ? opd.getNama() : "");
            opdCell.setCellStyle(wrapStyle);

            Cell urusan = row.createCell(col++);
            urusan.setCellValue(pemetaanDTO.getUrusanPemerintahan());
            urusan.setCellStyle(wrapStyle);

            Cell kegiatan = row.createCell(col++);
            kegiatan.setCellValue(pemetaanDTO.getKegiatan());
            kegiatan.setCellStyle(wrapStyle);

            Cell jangkaWaktu = row.createCell(col++);
            jangkaWaktu.setCellValue(pemetaanDTO.getJangkaWaktu());
            jangkaWaktu.setCellStyle(wrapStyle);

            Cell calonMitra = row.createCell(col++);
            calonMitra.setCellValue(pemetaanDTO.getNamaCalonMitra());
            calonMitra.setCellStyle(wrapStyle);

            Cell noKontrak = row.createCell(col++);
            noKontrak.setCellValue(pemetaanDTO.getNoKontrakCalonMitra());
            noKontrak.setCellStyle(wrapStyle);

            Cell waktuPelaksanaan = row.createCell(col++);
            waktuPelaksanaan.setCellValue(pemetaanDTO.getWaktuPelaksanaan());
            waktuPelaksanaan.setCellStyle(wrapStyle);

            Cell prioritas = row.createCell(col++);
            prioritas.setCellValue(pemetaanDTO.getSkalaPrioritas());
            prioritas.setCellStyle(wrapStyle);

            Cell TW1 = row.createCell(col++);
            TW1.setCellValue(pemetaanDTO.getWaktuPelaksanaanTW1());
            TW1.setCellStyle(wrapStyle);

            Cell TW2 = row.createCell(col++);
            TW2.setCellValue(pemetaanDTO.getWaktuPelaksanaanTW2());
            TW2.setCellStyle(wrapStyle);

            Cell TW3 = row.createCell(col++);
            TW3.setCellValue(pemetaanDTO.getWaktuPelaksanaanTW3());
            TW3.setCellStyle(wrapStyle);

            Cell TW4 = row.createCell(col++);
            TW4.setCellValue(pemetaanDTO.getWaktuPelaksanaanTW4());
            TW4.setCellStyle(wrapStyle);

            Cell PIC = row.createCell(col++);
            PIC.setCellValue(pemetaanDTO.getPIC());
            PIC.setCellStyle(wrapStyle);

            Cell pembiayaan = row.createCell(col++);
            pembiayaan.setCellValue(pemetaanDTO.getSumberPembiayaan());
            pembiayaan.setCellStyle(wrapStyle);

        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());

    }

    public ByteArrayInputStream exportExcelPerPerangkat(PagingRequest pagingRequest) throws IOException {
        Page<PemetaanDTO> pemetaan = getPemetaan(pagingRequest, 0, 1000000);
        List<PemetaanDTO> pemetaanList = pemetaan.getData();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        sheet.setColumnWidth(0, 5 * 256);
        sheet.setColumnWidth(1, 5 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 25 * 256);
        sheet.setColumnWidth(4, 20 * 256);
        sheet.setColumnWidth(5, 20 * 256);
        sheet.setColumnWidth(6, 20 * 256);
        sheet.setColumnWidth(7, 20 * 256);
        sheet.setColumnWidth(8, 20 * 256);
        sheet.setColumnWidth(9, 20 * 256);
        sheet.setColumnWidth(10, 20 * 256);

        sheet.setColumnWidth(13, 20 * 256);
        sheet.setColumnWidth(14, 20 * 256);

        int rowIdx = 0;
        int counter = 1;
        int counterBagian = 1;
        OPD currentOPD = null;
        Bagian currentBagian = null;
        int detailRow = 0;

        for (PemetaanDTO pemetaanDTO : pemetaanList) {
            OPD opd = pemetaanDTO.getOpd();
            Bagian bagian = pemetaanDTO.getBagian();

            if ((currentOPD == null) || (opd != currentOPD)) {
                rowIdx = printHeaderOPD(workbook, sheet, opd, rowIdx, counter++);
                detailRow = 0;
            }

            if ((currentBagian == null) || (bagian != currentBagian)) {
                rowIdx = printHeaderBagian(workbook, sheet, rowIdx, pemetaanDTO);
                detailRow = 0;
            }

            detailRow++;

            rowIdx = printDetailRow(workbook, sheet, rowIdx, pemetaanDTO, detailRow);

            currentOPD = pemetaanDTO.getOpd();
            currentBagian = pemetaanDTO.getBagian();

        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());

    }

    private int printDetailRow(Workbook workbook, Sheet sheet, int rowIdx, PemetaanDTO pemetaanDTO, int detailRow) {
        Bagian bagianKLN = bagianService.findBySingkatan("KLN").get();
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();
        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();

        Bagian current = pemetaanDTO.getBagian();

        if (current.equals(bagianKLN)) {
            rowIdx = printDetailRowKLN(workbook, sheet, rowIdx, pemetaanDTO, detailRow);
        } else if (current.equals(bagianKDN)) {
            rowIdx = printDetailRowKDNatauKPT(workbook, sheet, rowIdx, pemetaanDTO, detailRow);
        } else {
            rowIdx = printDetailRowKDNatauKPT(workbook, sheet, rowIdx, pemetaanDTO, detailRow);
        }

        return rowIdx;
    }

    private int printDetailRowKLN(Workbook workbook, Sheet sheet, int rowIdx, PemetaanDTO pemetaanDTO, int detailRow) {
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);
        wrapStyle.setBorderBottom(BorderStyle.THIN);
        wrapStyle.setBorderLeft(BorderStyle.THIN);
        wrapStyle.setBorderRight(BorderStyle.THIN);
        wrapStyle.setBorderTop(BorderStyle.THIN);

        Row row = sheet.createRow(rowIdx);

        Cell no = row.createCell(1);
        no.setCellValue(detailRow);
        no.setCellStyle(wrapStyle);

        Cell perangkat = row.createCell(2);
        OPD opd = pemetaanDTO.getOpd();
        perangkat.setCellValue(opd != null ? opd.getNama() : "");
        perangkat.setCellStyle(wrapStyle);

        Cell urusan = row.createCell(3);
        urusan.setCellValue(pemetaanDTO.getUrusanPemerintahan());
        urusan.setCellStyle(wrapStyle);

        Cell kegiatan = row.createCell(4);
        kegiatan.setCellValue(pemetaanDTO.getKegiatan());
        kegiatan.setCellStyle(wrapStyle);

        Cell jangkaWaktu = row.createCell(5);
        jangkaWaktu.setCellValue(pemetaanDTO.getJangkaWaktu());
        jangkaWaktu.setCellStyle(wrapStyle);

        Cell nama = row.createCell(6);
        nama.setCellValue(pemetaanDTO.getNamaCalonMitra());
        nama.setCellStyle(wrapStyle);

        Cell noKontrak = row.createCell(7);
        noKontrak.setCellValue(pemetaanDTO.getNoKontrakCalonMitra());
        noKontrak.setCellStyle(wrapStyle);

        Cell waktuPelaksanaan = row.createCell(8);
        waktuPelaksanaan.setCellValue(pemetaanDTO.getWaktuPelaksanaan());
        waktuPelaksanaan.setCellStyle(wrapStyle);

        Cell pic = row.createCell(9);
        pic.setCellValue(pemetaanDTO.getPIC());
        pic.setCellStyle(wrapStyle);

        Cell biaya = row.createCell(10);
        biaya.setCellValue(pemetaanDTO.getSumberPembiayaan());
        biaya.setCellStyle(wrapStyle);

        rowIdx++;

        return rowIdx;

    }

    private int printDetailRowKDNatauKPT(Workbook workbook, Sheet sheet, int rowIdx, PemetaanDTO pemetaanDTO,
            int detailRow) {
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);
        wrapStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        wrapStyle.setBorderBottom(BorderStyle.THIN);
        wrapStyle.setBorderLeft(BorderStyle.THIN);
        wrapStyle.setBorderRight(BorderStyle.THIN);
        wrapStyle.setBorderTop(BorderStyle.THIN);

        Row row = sheet.createRow(rowIdx);
        row.setHeight((short) -1);

        Cell no = row.createCell(1);
        no.setCellValue(detailRow);
        no.setCellStyle(wrapStyle);

        Cell perangkat = row.createCell(2);
        OPD opd = pemetaanDTO.getOpd();
        perangkat.setCellValue(opd != null ? opd.getNama() : "");
        perangkat.setCellStyle(wrapStyle);

        Cell urusan = row.createCell(3);
        urusan.setCellValue(pemetaanDTO.getUrusanPemerintahan());
        urusan.setCellStyle(wrapStyle);

        Cell kegiatan = row.createCell(4);
        kegiatan.setCellValue(pemetaanDTO.getKegiatan());
        kegiatan.setCellStyle(wrapStyle);

        Cell jangkaWaktu = row.createCell(5);
        jangkaWaktu.setCellValue(pemetaanDTO.getJangkaWaktu());
        jangkaWaktu.setCellStyle(wrapStyle);

        Cell nama = row.createCell(6);
        nama.setCellValue(pemetaanDTO.getNamaCalonMitra());
        nama.setCellStyle(wrapStyle);

        Cell noKontrak = row.createCell(7);
        noKontrak.setCellValue(pemetaanDTO.getNoKontrakCalonMitra());
        noKontrak.setCellStyle(wrapStyle);

        Cell skala = row.createCell(8);
        skala.setCellValue(pemetaanDTO.getSkalaPrioritas());
        skala.setCellStyle(wrapStyle);

        Cell TW1 = row.createCell(9);
        TW1.setCellValue(pemetaanDTO.getWaktuPelaksanaanTW1());
        TW1.setCellStyle(wrapStyle);

        Cell TW2 = row.createCell(10);
        TW2.setCellValue(pemetaanDTO.getWaktuPelaksanaanTW2());
        TW2.setCellStyle(wrapStyle);

        Cell TW3 = row.createCell(11);
        TW3.setCellValue(pemetaanDTO.getWaktuPelaksanaanTW3());
        TW3.setCellStyle(wrapStyle);

        Cell TW4 = row.createCell(12);
        TW4.setCellValue(pemetaanDTO.getWaktuPelaksanaanTW4());
        TW4.setCellStyle(wrapStyle);

        Cell pic = row.createCell(13);
        pic.setCellValue(pemetaanDTO.getPIC());
        pic.setCellStyle(wrapStyle);

        Cell biaya = row.createCell(14);
        biaya.setCellValue(pemetaanDTO.getSumberPembiayaan());
        biaya.setCellStyle(wrapStyle);

        rowIdx++;

        return rowIdx;
    }

    private int printHeaderBagian(Workbook workbook, Sheet sheet, int rowIdx, PemetaanDTO pemetaanDTO) {
        Bagian bagianKLN = bagianService.findBySingkatan("KLN").get();
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();
        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();

        CellStyle headerCellStyle = workbook.createCellStyle();
        XSSFFont boldFont = (XSSFFont) workbook.createFont();
        boldFont.setBold(true);
        headerCellStyle.setFont(boldFont);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setWrapText(true);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle justBoldStyle = workbook.createCellStyle();
        justBoldStyle.setFont(boldFont);

        Bagian current = pemetaanDTO.getBagian();

        Row row = sheet.createRow(rowIdx++);
        row = sheet.createRow(rowIdx++);

        if (current.equals(bagianKLN)) {
            Cell heading = row.createCell(1);

            heading.setCellValue(
                    "Rencana Kerja sama Pemerintah Provinsi DKI Jakarta dengan Pemerintah Luar Negeri dan Lembaga Luar Negeri");
            // heading.setCellStyle(justBoldStyle);

            row = sheet.createRow(rowIdx);
            Cell no = row.createCell(1);
            no.setCellValue("No");
            no.setCellStyle(headerCellStyle);
            sheet.createRow(rowIdx + 1).createCell(1).setCellStyle(headerCellStyle);

            Cell perangkat = row.createCell(2);
            perangkat.setCellValue("Perangkat Daerah");
            perangkat.setCellStyle(headerCellStyle);
            sheet.getRow(rowIdx + 1).createCell(2).setCellStyle(headerCellStyle);

            Cell urusan = row.createCell(3);
            urusan.setCellValue("Urusan Pemerintahan");
            urusan.setCellStyle(headerCellStyle);
            sheet.getRow(rowIdx + 1).createCell(3).setCellStyle(headerCellStyle);

            Cell kegiatan = row.createCell(4);
            kegiatan.setCellValue("Kegiatan/Program Kerja Sama");
            kegiatan.setCellStyle(headerCellStyle);
            sheet.getRow(rowIdx + 1).createCell(4).setCellStyle(headerCellStyle);

            Cell waktu = row.createCell(5);
            waktu.setCellValue("Jangka Waktu");
            waktu.setCellStyle(headerCellStyle);
            sheet.getRow(rowIdx + 1).createCell(5).setCellStyle(headerCellStyle);

            Cell pemerintah = row.createCell(6);
            pemerintah.setCellValue("Pemerintah dan Lembaga LN");
            pemerintah.setCellStyle(headerCellStyle);
            sheet.getRow(rowIdx).createCell(7).setCellStyle(headerCellStyle);

            Cell pelaksanaan = row.createCell(8);
            pelaksanaan.setCellValue("Waktu Pelaksanaan");
            pelaksanaan.setCellStyle(headerCellStyle);
            sheet.getRow(rowIdx + 1).createCell(8).setCellStyle(headerCellStyle);

            Cell pic = row.createCell(9);
            pic.setCellValue("PIC (Min Es. IV) Nama dan Jabatan");
            pic.setCellStyle(headerCellStyle);
            sheet.getRow(rowIdx + 1).createCell(9).setCellStyle(headerCellStyle);

            Cell biaya = row.createCell(10);
            biaya.setCellValue("Sumber Pembiayaan");
            biaya.setCellStyle(headerCellStyle);
            sheet.getRow(rowIdx + 1).createCell(10).setCellStyle(headerCellStyle);

            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 2, 2));
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 3, 3));
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 4, 4));
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 5, 5));
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 6, 7));
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 8, 8));
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 9, 9));
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 10, 10));

            rowIdx++;

            row = sheet.getRow(rowIdx++);
            Cell nama = row.createCell(6);
            nama.setCellValue("Nama");
            nama.setCellStyle(headerCellStyle);

            Cell noKontrak = row.createCell(7);
            noKontrak.setCellValue("No Kontrak");
            noKontrak.setCellStyle(headerCellStyle);

        } else if (current.equals(bagianKDN)) {
            row.createCell(1).setCellValue(
                    "Rencana Kerja sama Pemerintah Provinsi DKI Jakarta dengan Daerah Lain dan Instansi Pemerintah");
            rowIdx = printHeaderBagianKDNatauKPT(workbook, sheet, rowIdx, row);
        } else {
            row.createCell(1).setCellValue("Rencana Kerja sama Pemerintah Provinsi DKI Jakarta dengan Pihak Ketiga");
            rowIdx = printHeaderBagianKDNatauKPT(workbook, sheet, rowIdx, row);
        }

        return rowIdx;
    }

    private int printHeaderBagianKDNatauKPT(Workbook workbook, Sheet sheet, int rowIdx, Row row) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        XSSFFont boldFont = (XSSFFont) workbook.createFont();
        boldFont.setBold(true);
        headerCellStyle.setFont(boldFont);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setWrapText(true);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        row = sheet.createRow(rowIdx);
        Cell no = row.createCell(1);
        no.setCellValue("No");
        no.setCellStyle(headerCellStyle);
        sheet.createRow(rowIdx + 1).createCell(1).setCellStyle(headerCellStyle);

        Cell perangkat = row.createCell(2);
        perangkat.setCellValue("Perangkat Daerah");
        perangkat.setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx + 1).createCell(2).setCellStyle(headerCellStyle);

        Cell urusan = row.createCell(3);
        urusan.setCellValue("Urusan Pemerintahan");
        urusan.setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx + 1).createCell(3).setCellStyle(headerCellStyle);

        Cell kegiatan = row.createCell(4);
        kegiatan.setCellValue("Kegiatan/Program Kerja Sama");
        kegiatan.setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx + 1).createCell(4).setCellStyle(headerCellStyle);

        Cell waktu = row.createCell(5);
        waktu.setCellValue("Jangka Waktu");
        waktu.setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx + 1).createCell(5).setCellStyle(headerCellStyle);

        Cell calonMitra = row.createCell(6);
        calonMitra.setCellValue("Calon Mitra Kerja Sama");
        calonMitra.setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx).createCell(7).setCellStyle(headerCellStyle);

        Cell skala = row.createCell(8);
        skala.setCellValue("Skala Prioritas");
        skala.setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx + 1).createCell(8).setCellStyle(headerCellStyle);

        Cell waktuPelaksanaan = row.createCell(9);
        waktuPelaksanaan.setCellValue("Waktu Pelaksanaan");
        waktuPelaksanaan.setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx).createCell(10).setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx).createCell(11).setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx).createCell(12).setCellStyle(headerCellStyle);

        Cell pic = row.createCell(13);
        pic.setCellValue("PIC (Min Es. iV) Nama dan Jabatan");
        pic.setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx + 1).createCell(13).setCellStyle(headerCellStyle);

        Cell sumberPembiayaan = row.createCell(14);
        sumberPembiayaan.setCellValue("Sumber Pembiayaan");
        sumberPembiayaan.setCellStyle(headerCellStyle);
        sheet.getRow(rowIdx + 1).createCell(14).setCellStyle(headerCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 4, 4));
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 5, 5));
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 6, 7));
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 8, 8));
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 9, 12));
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 13, 13));
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 14, 14));

        rowIdx++;

        row = sheet.getRow(rowIdx++);
        Cell nama = row.createCell(6);
        nama.setCellValue("Nama");
        nama.setCellStyle(headerCellStyle);

        Cell noKontrak = row.createCell(7);
        noKontrak.setCellValue("No Kontrak");
        noKontrak.setCellStyle(headerCellStyle);

        Cell TW1 = row.createCell(9);
        TW1.setCellValue("TW1");
        TW1.setCellStyle(headerCellStyle);

        Cell TW2 = row.createCell(10);
        TW2.setCellValue("TW2");
        TW2.setCellStyle(headerCellStyle);

        Cell TW3 = row.createCell(11);
        TW3.setCellValue("TW3");
        TW3.setCellStyle(headerCellStyle);

        Cell TW4 = row.createCell(12);
        TW4.setCellValue("TW4");
        TW4.setCellStyle(headerCellStyle);

        // applyBorderToRegions(workbook, sheet);

        // rowIdx++;
        return rowIdx;
    }

    private void applyBorderToRegions(Workbook book, Sheet sheet) {
        List<CellRangeAddress> regions = sheet.getMergedRegions();
        for (CellRangeAddress address : regions) {
            RegionUtil.setBorderTop(BorderStyle.THIN, address, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, address, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, address, sheet);
            RegionUtil.setBorderBottom(BorderStyle.THIN, address, sheet);
        }

    }

    private int printHeaderOPD(Workbook workbook, Sheet sheet, OPD opd, int rowIdx, int counter) {
        XSSFFont boldFont = (XSSFFont) workbook.createFont();
        boldFont.setBold(true);

        CellStyle justBoldStyle = workbook.createCellStyle();
        justBoldStyle.setFont(boldFont);

        Row row = sheet.createRow(rowIdx++);
        row = sheet.createRow(rowIdx);
        Cell headerOPD = row.createCell(0);
        headerOPD.setCellValue(String.valueOf(counter) + ". " + opd.getNama().toUpperCase());
        headerOPD.setCellStyle(justBoldStyle);

        rowIdx++;

        return rowIdx;
    }

}
