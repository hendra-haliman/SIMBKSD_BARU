package id.go.jakarta.bksd.dokumen.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.JenisMitra;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.Rekapitulasi;
import id.go.jakarta.bksd.dokumen.domain.Search;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.DokumenKerjaSamaDTO;
import id.go.jakarta.bksd.dokumen.domain.dto.DokumenKerjaSamaLuarNegeriDTO;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskah;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskahLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.Kolaborasi;

@Service
public class LaporanService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private DokumenKerjaSamaService dokumenKerjaSamaService;
    private DokumenKerjaSamaLuarNegeriService dokumenKerjaSamaLuarNegeriService;
    private BagianService bagianService;
    private JenisNaskahService jenisNaskahService;
    private JenisMitraService jenisMitraService;
    private JenisNaskahLuarNegeriService jenisNaskahLuarNegeriService;
    private KolaborasiService kolaborasiService;
    private AsistenSekretarisDaerahService asistenSekretarisDaerahService;

    public LaporanService(DokumenKerjaSamaService dokumenKerjaSamaService,
            DokumenKerjaSamaLuarNegeriService dokumenKerjaSamaLuarNegeriService,
            BagianService bagianService,
            JenisNaskahService jenisNaskahService,
            JenisMitraService jenisMitraService,
            JenisNaskahLuarNegeriService jenisNaskahLuarNegeriService,
            KolaborasiService kolaborasiService,
            AsistenSekretarisDaerahService asistenSekretarisDaerahService) {
        this.dokumenKerjaSamaService = dokumenKerjaSamaService;
        this.dokumenKerjaSamaLuarNegeriService = dokumenKerjaSamaLuarNegeriService;
        this.bagianService = bagianService;
        this.jenisNaskahService = jenisNaskahService;
        this.jenisMitraService = jenisMitraService;
        this.jenisNaskahLuarNegeriService = jenisNaskahLuarNegeriService;
        this.kolaborasiService = kolaborasiService;
        this.asistenSekretarisDaerahService = asistenSekretarisDaerahService;
    }

    public ByteArrayInputStream rekapTahunan(int tahun) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        // Sheet sheet = workbook.createSheet();

        createSheetRekapV2(workbook, tahun);
        createSheetKPT(workbook, tahun);
        createSheetKDN(workbook, tahun);
        createSheetKLN(workbook, tahun);
        createSheetNPBK(workbook, tahun);
        createSheetKolaborasi(workbook, tahun);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());

    }

    /**
     * Rekap sheet berdasarkan Excel 2023
     * 
     * @param workbook
     * @param tahun
     */
    public void createSheetRekap(Workbook workbook, int tahun) {
        Sheet sheet = workbook.createSheet("Rekap Dokumen Kerja Sama " + tahun);

        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 20 * 256);

        sheet.setColumnWidth(3, 40 * 256);
        sheet.setColumnWidth(4, 50 * 256);
        sheet.setColumnWidth(5, 30 * 256);
        sheet.setColumnWidth(6, 14 * 256);
        sheet.setColumnWidth(7, 30 * 256);
        sheet.setColumnWidth(8, 30 * 256);
        sheet.setColumnWidth(9, 40 * 256);
        sheet.setColumnWidth(10, 40 * 256);
        sheet.setColumnWidth(11, 40 * 256);

        CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 24);
        titleCellStyle.setFont(titleFont);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setWrapText(true);
        titleCellStyle.setBorderBottom(BorderStyle.THIN);
        titleCellStyle.setBorderTop(BorderStyle.THIN);
        titleCellStyle.setBorderLeft(BorderStyle.THIN);
        titleCellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 15);
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setWrapText(true);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle headerCellStyle2 = workbook.createCellStyle();
        headerCellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle2.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle2.setWrapText(true);
        headerCellStyle2.setBorderBottom(BorderStyle.THIN);
        headerCellStyle2.setBorderTop(BorderStyle.THIN);
        headerCellStyle2.setBorderLeft(BorderStyle.THIN);
        headerCellStyle2.setBorderRight(BorderStyle.THIN);

        int rowIdx = 1;
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints((short) 30);

        Cell cell = row.createCell(0);
        cell.setCellValue("Rekapitulasi Data Kerja Sama Daerah Setda Provinsi DK Jakarta Tahun " + tahun);
        cell.setCellStyle(titleCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 6));

        cell = row.createCell(1);
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(2);
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(3);
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(4);
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(5);
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(6);
        cell.setCellStyle(titleCellStyle);

        rowIdx++;

        row = sheet.createRow(rowIdx);
        cell = row.createCell(0);
        cell.setCellValue("Jenis Dokumen Kerja Sama");
        cell.setCellStyle(headerCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 0, 0));

        cell = row.createCell(1);
        cell.setCellValue("Kerja Sama Luar Negeri (KLN)");
        cell.setCellStyle(headerCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx + 1, 1, 1));

        cell = row.createCell(2);
        cell.setCellValue("Kerja Sama Pihak Ketiga (KPT)");
        cell.setCellStyle(headerCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 2, 4));

        cell = row.createCell(5);
        cell.setCellValue("Kerja Sama Dalam Negeri (KDN)");
        cell.setCellStyle(headerCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 5, 6));

        rowIdx++;

        row = sheet.createRow(rowIdx++);

        cell = row.createCell(0);
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(1);
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Badan Usaha dan Perseorangan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Perguruan Tinggi, Yayasan dan Lembaga Lain");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Jumlah");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Sinergisitas");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Kerja Sama dengan Daerah");
        cell.setCellStyle(headerCellStyle);

        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Perjanjian Kerja Sama");
        cell.setCellStyle(headerCellStyle2);

        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        JenisNaskah jenisDokumenPKS = jenisNaskahService.findByNama("Perjanjian Kerja Sama").get();
        JenisNaskah jenisDokumenAdendumPKS = jenisNaskahService.findByNama("Adendum Perjanjian Kerja Sama").get();
        JenisNaskah jenisDokumenKesepakatanBersama = jenisNaskahService.findByNama("Kesepakatan Bersama").get();
        JenisNaskah jenisDokumenAdendumKesepakatanBersama = jenisNaskahService.findByNama("Adendum Kesepakatan Bersama")
                .get();
        JenisNaskah jenisNaskahNotaKesepakatan = jenisNaskahService.findByNama("Nota Kesepakatan").get();
        JenisNaskah jenisNaskahPerjanjianPinjamPakai = jenisNaskahService.findByNama("Perjanjian Pinjam Pakai").get();
        JenisNaskah jenisNaskahAdendumPerjanjianPinjamPakai = jenisNaskahService
                .findByNama("Adendum Perjanjian Pinjam Pakai").get();
        JenisNaskah jenisNaskahPerubahanPerjanjianPinjamPakai = jenisNaskahService
                .findByNama("Perubahan Perjanjian Pinjam Pakai").get();

        JenisNaskah jenisNaskahNPBK = jenisNaskahService.findByNama("Naskah Perjanjian Bantuan Keuangan").get();

        JenisNaskahLuarNegeri jenisNaskahLNMOU = jenisNaskahLuarNegeriService.findByNama("Memorandum of Understanding")
                .get();
        JenisNaskahLuarNegeri jenisNaskahLNAdendumMOU = jenisNaskahLuarNegeriService
                .findByNama("Adendum Memorandum of Understanding").get();
        JenisNaskahLuarNegeri jenisNaskahLNLoI = jenisNaskahLuarNegeriService.findByNama("Letter of Interest").get();

        JenisMitra jenisMitraBadanUsaha = jenisMitraService.findByNama("Badan Usaha").get();
        JenisMitra jenisMitraYayasan = jenisMitraService.findByNama("Yayasan").get();
        JenisMitra jenisMitraPerguruanTinggi = jenisMitraService.findByNama("Perguruan Tinggi").get();
        JenisMitra jenisMitraSinergisitas = jenisMitraService.findByNama("Sinergisitas").get();

        List<JenisMitra> jenisMitraBUdanPerseorangan = new ArrayList<JenisMitra>();
        jenisMitraBUdanPerseorangan.add(jenisMitraBadanUsaha);

        List<JenisMitra> jenisMitraYayasanPTLembagaLain = new ArrayList<JenisMitra>();
        jenisMitraYayasanPTLembagaLain.add(jenisMitraYayasan);
        jenisMitraYayasanPTLembagaLain.add(jenisMitraPerguruanTinggi);

        List<JenisMitra> jenisMitraSinergisitasList = new ArrayList<JenisMitra>();
        jenisMitraSinergisitasList.add(jenisMitraSinergisitas);

        createCounterFor(row, tahun, jenisDokumenPKS, null, headerCellStyle2);

        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Adendum PKS");
        cell.setCellStyle(headerCellStyle2);

        createCounterFor(row, tahun, jenisDokumenAdendumPKS, null, headerCellStyle2);

        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Kesepakatan Bersama (KB)/MoU");
        cell.setCellStyle(headerCellStyle2);

        createCounterFor(row, tahun, jenisDokumenKesepakatanBersama, jenisNaskahLNMOU, headerCellStyle2);

        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Adendum Kesepakatan Bersama");
        cell.setCellStyle(headerCellStyle2);

        createCounterFor(row, tahun, jenisDokumenAdendumKesepakatanBersama, jenisNaskahLNAdendumMOU, headerCellStyle2);

        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Nota Kesepakatan (NK)/ LoI");
        cell.setCellStyle(headerCellStyle2);

        createCounterFor(row, tahun, jenisNaskahNotaKesepakatan, jenisNaskahLNLoI, headerCellStyle2);

        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Perjanjian Pinjam Pakai");
        cell.setCellStyle(headerCellStyle2);

        createCounterFor(row, tahun, jenisNaskahPerjanjianPinjamPakai, null, headerCellStyle2);

        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Adendum Perjanjian Pinjam Pakai");
        cell.setCellStyle(headerCellStyle2);
        createCounterFor(row, tahun, jenisNaskahAdendumPerjanjianPinjamPakai, null, headerCellStyle2);

        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Perubahan Perjanjian Pinjam Pakai");
        cell.setCellStyle(headerCellStyle2);
        createCounterFor(row, tahun, jenisNaskahPerubahanPerjanjianPinjamPakai, null, headerCellStyle2);

        long count = 0;
        row = sheet.createRow(rowIdx);
        cell = row.createCell(0);
        cell.setCellValue("Naskah Perjanjian Bantuan Keuangan");
        cell.setCellStyle(headerCellStyle2);

        // KDN - NPBK
        count = dokumenKerjaSamaService.getCount(tahun, bagianKDN, jenisNaskahNPBK);
        cell = row.createCell(5);
        cell.setCellValue(count);

        cell.setCellStyle(headerCellStyle2);
        cell = row.createCell(6);
        cell.setCellStyle(headerCellStyle2);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 5, 6));

        rowIdx++;

        // Total per Bagian
        row = sheet.createRow(rowIdx);
        cell = row.createCell(0);
        cell.setCellValue("Total Perbagian");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(1);
        cell.setCellValue(dokumenKerjaSamaLuarNegeriService.getCount(tahun));
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(2);
        cell.setCellValue(dokumenKerjaSamaService.getCount(tahun, bagianKPT));
        cell.setCellStyle(headerCellStyle);
        row.createCell(3).setCellStyle(headerCellStyle);
        row.createCell(4).setCellStyle(headerCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 2, 4));

        cell = row.createCell(5);
        cell.setCellValue(dokumenKerjaSamaService.getCount(tahun, bagianKDN));
        cell.setCellStyle(headerCellStyle);
        row.createCell(6).setCellStyle(headerCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 5, 6));

        rowIdx++;

        // Total Kerja Sama
        row = sheet.createRow(rowIdx);
        cell = row.createCell(0);
        cell.setCellValue("Total Kerja Sama");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(1);
        cell.setCellValue(getCount(tahun));
        cell.setCellStyle(headerCellStyle);
        cell = row.createCell(2);
        cell.setCellStyle(headerCellStyle);
        cell = row.createCell(3);
        cell.setCellStyle(headerCellStyle);
        cell = row.createCell(4);
        cell.setCellStyle(headerCellStyle);
        cell = row.createCell(5);
        cell.setCellStyle(headerCellStyle);
        cell = row.createCell(6);
        cell.setCellStyle(headerCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 6));

        rowIdx = rowIdx + 4;

        rowIdx = createContentKPT(workbook, sheet, tahun, rowIdx);

        rowIdx = rowIdx + 4;

        rowIdx = createContentKDN(workbook, sheet, tahun, rowIdx);

        rowIdx = rowIdx + 4;

        rowIdx = createContentKLN(workbook, sheet, tahun, rowIdx);

    }

    /**
     * Rekap sheet berdasarkan Excel 2024
     * 
     * @param workbook
     * @param tahun
     */
    public void createSheetRekapV2(Workbook workbook, int tahun) {
        Sheet sheet = workbook.createSheet("Rekap Dokumen Kerja Sama " + tahun);

        sheet.setColumnWidth(0, 30 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 20 * 256);

        sheet.setColumnWidth(3, 20 * 256);
        sheet.setColumnWidth(4, 20 * 256);
        sheet.setColumnWidth(5, 25 * 256);
        sheet.setColumnWidth(6, 15 * 256);

        CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont titleFont = (XSSFFont) workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 24);
        titleCellStyle.setFont(titleFont);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setWrapText(true);
        titleCellStyle.setBorderBottom(BorderStyle.THIN);
        titleCellStyle.setBorderTop(BorderStyle.THIN);
        titleCellStyle.setBorderLeft(BorderStyle.THIN);
        titleCellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 15);
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setWrapText(true);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle headerCellStyle2 = workbook.createCellStyle();
        headerCellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle2.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle2.setWrapText(true);
        headerCellStyle2.setBorderBottom(BorderStyle.THIN);
        headerCellStyle2.setBorderTop(BorderStyle.THIN);
        headerCellStyle2.setBorderLeft(BorderStyle.THIN);
        headerCellStyle2.setBorderRight(BorderStyle.THIN);

        int rowIdx = 1;
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints((short) 30);

        Cell cell = row.createCell(0);
        cell.setCellValue("Rekapitulasi Data Kerja Sama Daerah Setda Provinsi DK Jakarta Tahun " + tahun);
        cell.setCellStyle(titleCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 6));

        cell = row.createCell(1);
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(2);
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(3);
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(4);
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(5);
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(6);
        cell.setCellStyle(titleCellStyle);

        rowIdx++;

        row = sheet.createRow(rowIdx);
        cell = row.createCell(0);
        cell.setCellValue("Kelompok Kerja Sama");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Perjanjian Kerja Sama");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Kesepakatan Bersama");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Nota Kesepakatan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Naskah Perjanjian Bantuan Keuangan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Kolaborasi/Kerja Sama Non Perikatan");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Total");
        cell.setCellStyle(headerCellStyle);

        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        JenisNaskah jenisDokumenPKS = jenisNaskahService.findByNama("Perjanjian Kerja Sama").get();
        JenisNaskah jenisDokumenAdendumPKS = jenisNaskahService.findByNama("Adendum Perjanjian Kerja Sama").get();
        JenisNaskah jenisDokumenKesepakatanBersama = jenisNaskahService.findByNama("Kesepakatan Bersama").get();
        JenisNaskah jenisDokumenAdendumKesepakatanBersama = jenisNaskahService.findByNama("Adendum Kesepakatan Bersama")
                .get();
        JenisNaskah jenisNaskahNotaKesepakatan = jenisNaskahService.findByNama("Nota Kesepakatan").get();
        JenisNaskah jenisNaskahPerjanjianPinjamPakai = jenisNaskahService.findByNama("Perjanjian Pinjam Pakai").get();
        JenisNaskah jenisNaskahAdendumPerjanjianPinjamPakai = jenisNaskahService
                .findByNama("Adendum Perjanjian Pinjam Pakai").get();
        JenisNaskah jenisNaskahPerubahanPerjanjianPinjamPakai = jenisNaskahService
                .findByNama("Perubahan Perjanjian Pinjam Pakai").get();

        JenisNaskah jenisNaskahNPBK = jenisNaskahService.findByNama("Naskah Perjanjian Bantuan Keuangan").get();

        JenisNaskahLuarNegeri jenisNaskahLNMOU = jenisNaskahLuarNegeriService.findByNama("Memorandum of Understanding")
                .get();
        JenisNaskahLuarNegeri jenisNaskahLNAdendumMOU = jenisNaskahLuarNegeriService
                .findByNama("Adendum Memorandum of Understanding").get();
        JenisNaskahLuarNegeri jenisNaskahLNLoI = jenisNaskahLuarNegeriService.findByNama("Letter of Interest").get();

        JenisMitra jenisMitraBadanUsaha = jenisMitraService.findByNama("Badan Usaha").get();
        JenisMitra jenisMitraYayasan = jenisMitraService.findByNama("Yayasan").get();
        JenisMitra jenisMitraPerguruanTinggi = jenisMitraService.findByNama("Perguruan Tinggi").get();
        JenisMitra jenisMitraSinergisitas = jenisMitraService.findByNama("Sinergisitas").get();
        JenisMitra jenisMitraLembagaLain = jenisMitraService.findByNama("Lembaga Lain").get();
        JenisMitra jenisMitraPerseorangan = jenisMitraService.findByNama("Perseorangan").get();
        JenisMitra jenisMitraSinergi = jenisMitraService.findByNama("Sinergisitas").get();
        JenisMitra jenisMitraKerjaSamaDenganDaerah = jenisMitraService.findByNama("Kerja Sama dengan Daerah").get();
        JenisMitra jenisMitraPinjamPakaiDanBentukLain = jenisMitraService
                .findByNama("Kerja Sama Pinjam Pakai dan Bentuk Perjanjian dengan Instansi Lain").get();

        List<JenisMitra> jenisMitraBUdanPerseorangan = new ArrayList<JenisMitra>();
        jenisMitraBUdanPerseorangan.add(jenisMitraBadanUsaha);

        List<JenisMitra> jenisMitraYayasanPTLembagaLain = new ArrayList<JenisMitra>();
        jenisMitraYayasanPTLembagaLain.add(jenisMitraYayasan);
        jenisMitraYayasanPTLembagaLain.add(jenisMitraPerguruanTinggi);

        List<JenisMitra> jenisMitraSinergisitasList = new ArrayList<JenisMitra>();
        jenisMitraSinergisitasList.add(jenisMitraSinergisitas);

        rowIdx++;

        // Badan Usaha
        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Badan Usaha");
        cell.setCellStyle(headerCellStyle2);
        createCounterForV2(row, tahun, List.of(jenisMitraBadanUsaha), headerCellStyle2);

        // Perguruan Tinggi
        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Perguruan Tinggi");
        cell.setCellStyle(headerCellStyle2);
        createCounterForV2(row, tahun, List.of(jenisMitraPerguruanTinggi), headerCellStyle2);

        // Yayasan
        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Yayasan");
        cell.setCellStyle(headerCellStyle2);
        createCounterForV2(row, tahun, List.of(jenisMitraYayasan), headerCellStyle2);

        // Lembaga Lain
        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Lembaga Lain");
        cell.setCellStyle(headerCellStyle2);
        createCounterForV2(row, tahun, List.of(jenisMitraLembagaLain), headerCellStyle2);

        // Perseorangan
        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Perseorangan");
        cell.setCellStyle(headerCellStyle2);
        createCounterForV2(row, tahun, List.of(jenisMitraPerseorangan), headerCellStyle2);

        // Sinergi
        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Sinergi");
        cell.setCellStyle(headerCellStyle2);
        createCounterForV2(row, tahun, List.of(jenisMitraSinergi), headerCellStyle2);

        // Kerja Sama dengan Daerah
        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Kerja Sama dengan Daerah");
        cell.setCellStyle(headerCellStyle2);
        createCounterForV2(row, tahun, List.of(jenisMitraKerjaSamaDenganDaerah), headerCellStyle2);

        // Kerja Sama Pinjam Pakai dan Bentuk Perjanjian dengan Instansi Lain
        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Kerja Sama Pinjam Pakai dan Bentuk Perjanjian dengan Instansi Lain");
        cell.setCellStyle(headerCellStyle2);
        createCounterForV2(row, tahun, List.of(jenisMitraPinjamPakaiDanBentukLain), headerCellStyle2);

        // Kerja Sama Luar Negeri
        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Kerja Sama Luar Negeri");
        cell.setCellStyle(headerCellStyle2);
        createCounterForV2(row, tahun, List.of(jenisMitraPinjamPakaiDanBentukLain), headerCellStyle2);

        // Total
        row = sheet.createRow(rowIdx++);
        cell = row.createCell(0);
        cell.setCellValue("Total Naskah Kerja");
        cell.setCellStyle(headerCellStyle2);
        createCounterForV2(row, tahun, null, headerCellStyle2);

    }

    public long getCount(int tahun) {
        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        long countKLN = dokumenKerjaSamaLuarNegeriService.getCount(tahun);
        long countKPT = dokumenKerjaSamaService.getCount(tahun, bagianKPT);
        long countKDN = dokumenKerjaSamaService.getCount(tahun, bagianKDN);

        long total = countKLN + countKPT + countKDN;

        return total;
    }

    public void createCounterForV2(Row row, int tahun, List<JenisMitra> jenisMitraList, CellStyle cellStyle) {
        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        JenisNaskah jenisDokumenPKS = jenisNaskahService.findByNama("Perjanjian Kerja Sama").get();
        JenisNaskah jenisDokumenAdendumPKS = jenisNaskahService.findByNama("Adendum Perjanjian Kerja Sama").get();
        JenisNaskah jenisDokumenKesepakatanBersama = jenisNaskahService.findByNama("Kesepakatan Bersama").get();
        JenisNaskah jenisDokumenAdendumKesepakatanBersama = jenisNaskahService.findByNama("Adendum Kesepakatan Bersama")
                .get();
        JenisNaskah jenisNaskahNotaKesepakatan = jenisNaskahService.findByNama("Nota Kesepakatan").get();
        JenisNaskah jenisNaskahPerjanjianPinjamPakai = jenisNaskahService.findByNama("Perjanjian Pinjam Pakai").get();
        JenisNaskah jenisNaskahAdendumPerjanjianPinjamPakai = jenisNaskahService
                .findByNama("Adendum Perjanjian Pinjam Pakai").get();
        JenisNaskah jenisNaskahPerubahanPerjanjianPinjamPakai = jenisNaskahService
                .findByNama("Perubahan Perjanjian Pinjam Pakai").get();

        JenisNaskah jenisNaskahNPBK = jenisNaskahService.findByNama("Naskah Perjanjian Bantuan Keuangan").get();

        JenisNaskahLuarNegeri jenisNaskahLNMOU = jenisNaskahLuarNegeriService.findByNama("Memorandum of Understanding")
                .get();
        JenisNaskahLuarNegeri jenisNaskahLNAdendumMOU = jenisNaskahLuarNegeriService
                .findByNama("Adendum Memorandum of Understanding").get();
        JenisNaskahLuarNegeri jenisNaskahLNLoI = jenisNaskahLuarNegeriService.findByNama("Letter of Interest").get();

        long count = 0;

        Cell cell;

        // PKS
        if (jenisMitraList != null) {
            count = dokumenKerjaSamaService.getCount(tahun, jenisDokumenPKS, jenisMitraList);
        } else {
            count = dokumenKerjaSamaService.getCount(tahun, jenisDokumenPKS);
        }
        cell = row.createCell(1);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(cellStyle);

        // KB
        if (jenisMitraList != null) {
            count = dokumenKerjaSamaService.getCount(tahun, jenisDokumenKesepakatanBersama, jenisMitraList);
        } else {
            count = dokumenKerjaSamaService.getCount(tahun, jenisDokumenKesepakatanBersama);
        }
        cell = row.createCell(2);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(cellStyle);

        // Nota Kesepakatan
        if (jenisMitraList != null) {
            count = dokumenKerjaSamaService.getCount(tahun, jenisNaskahNotaKesepakatan, jenisMitraList);
        } else {
            count = dokumenKerjaSamaService.getCount(tahun, jenisNaskahNotaKesepakatan);
        }
        cell = row.createCell(3);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(cellStyle);

        // NPBK
        if (jenisMitraList != null) {
            count = dokumenKerjaSamaService.getCount(tahun, jenisNaskahNPBK, jenisMitraList);
        } else {
            count = dokumenKerjaSamaService.getCount(tahun, jenisNaskahNPBK);
        }
        cell = row.createCell(4);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(cellStyle);

        // Kolaborasi/Kerja Sama Non Perikatan
        if (jenisMitraList == null) {
            cell = row.createCell(5);
            count = kolaborasiService.getCount(tahun);
            cell.setCellValue(count);
            cell.setCellStyle(cellStyle);
        }

        // Total
        if (jenisMitraList != null) {
            count = dokumenKerjaSamaService.getCount(tahun, jenisMitraList);
        } else {
            count = getCount(tahun);
        }
        cell = row.createCell(6);
        cell.setCellValue(String.valueOf(count));
        cell.setCellStyle(cellStyle);

    }

    public void createCounterForKLN(Row row, int tahun, CellStyle cellStyle) {
        JenisNaskahLuarNegeri jenisNaskahLNMOU = jenisNaskahLuarNegeriService.findByNama("Memorandum of Understanding")
                .get();
        JenisNaskahLuarNegeri jenisNaskahLNAdendumMOU = jenisNaskahLuarNegeriService
                .findByNama("Adendum Memorandum of Understanding").get();
        JenisNaskahLuarNegeri jenisNaskahLNLoI = jenisNaskahLuarNegeriService.findByNama("Letter of Interest").get();

        long count = 0;

        Cell cell;

        // MOU
        count = dokumenKerjaSamaLuarNegeriService.getCount(tahun, jenisNaskahLNMOU);
        cell = row.createCell(2);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(cellStyle);

        // LOI
        count = dokumenKerjaSamaLuarNegeriService.getCount(tahun, jenisNaskahLNLoI);
        cell = row.createCell(3);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(cellStyle);

        // Adendum MOU ... TODO: taruh dimana ?
        count = dokumenKerjaSamaLuarNegeriService.getCount(tahun, jenisNaskahLNAdendumMOU);

    }

    public void createCounterFor(Row row, int tahun, JenisNaskah jenisNaskah,
            JenisNaskahLuarNegeri jenisNaskahLuarNegeri, CellStyle style) {

        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        JenisMitra jenisMitraBadanUsaha = jenisMitraService.findByNama("Badan Usaha").get();
        JenisMitra jenisMitraYayasan = jenisMitraService.findByNama("Yayasan").get();
        JenisMitra jenisMitraPerguruanTinggi = jenisMitraService.findByNama("Perguruan Tinggi").get();
        JenisMitra jenisMitraSinergisitas = jenisMitraService.findByNama("Sinergisitas").get();
        JenisMitra jenisMitraKerjaSamaDenganDaerah = jenisMitraService.findByNama("Kerja Sama dengan Daerah").get();

        List<JenisMitra> jenisMitraBUdanPerseorangan = new ArrayList<JenisMitra>();
        jenisMitraBUdanPerseorangan.add(jenisMitraBadanUsaha);

        List<JenisMitra> jenisMitraYayasanPTLembagaLain = new ArrayList<JenisMitra>();
        jenisMitraYayasanPTLembagaLain.add(jenisMitraYayasan);
        jenisMitraYayasanPTLembagaLain.add(jenisMitraPerguruanTinggi);

        List<JenisMitra> jenisMitraSinergisitasList = new ArrayList<JenisMitra>();
        jenisMitraSinergisitasList.add(jenisMitraSinergisitas);

        List<JenisMitra> jenisMitraKerjaSamaDenganDaerahList = new ArrayList<JenisMitra>();
        jenisMitraKerjaSamaDenganDaerahList.add(jenisMitraKerjaSamaDenganDaerah);

        long count = 0;

        Cell cell;

        if (jenisNaskahLuarNegeri != null) {
            // PKS - KLN
            count = dokumenKerjaSamaLuarNegeriService.getCount(tahun, jenisNaskahLuarNegeri);
            cell = row.createCell(1);
            cell.setCellValue(count > 0 ? String.valueOf(count) : "");
            cell.setCellStyle(style);
        }

        // PKS - KPT - Badan Usaha dan Perseorangan Count
        count = dokumenKerjaSamaService.getCount(tahun, bagianKPT, jenisNaskah, jenisMitraBUdanPerseorangan);
        cell = row.createCell(2);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(style);

        // PKS - KPT - Perguruan Tinggi, Yayasan dan Lembaga Lain
        count = dokumenKerjaSamaService.getCount(tahun, bagianKPT, jenisNaskah, jenisMitraYayasanPTLembagaLain);
        cell = row.createCell(3);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(style);

        // PKS - KPT - Total
        count = dokumenKerjaSamaService.getCount(tahun, bagianKPT, jenisNaskah);
        cell = row.createCell(4);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(style);

        // PKS - KDN - Sinergisitas
        count = dokumenKerjaSamaService.getCount(tahun, bagianKDN, jenisNaskah, jenisMitraSinergisitasList);
        cell = row.createCell(5);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(style);

        // PKS - KDN - KErja Sama Dengan Daerah
        count = dokumenKerjaSamaService.getCount(tahun, bagianKDN, jenisNaskah, jenisMitraKerjaSamaDenganDaerahList);
        cell = row.createCell(6);
        cell.setCellValue(count > 0 ? String.valueOf(count) : "");
        cell.setCellStyle(style);

    }

    public int createSheetKPT(Workbook workbook, int tahun) {
        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();

        PagingRequest pagingRequest = createPagingRequest(tahun, bagianKPT);

        Page<DokumenKerjaSamaDTO> dokumenKerjaSama = dokumenKerjaSamaService.getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaDTO> dokumenKerjaSamaList = dokumenKerjaSama.getData();

        Sheet sheet = workbook.createSheet("KPT");

        return dokumenKerjaSamaService.writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList);

    }

    private int createContentKPT(Workbook workbook, Sheet sheet, int tahun, int rowIdx) {
        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();

        PagingRequest pagingRequest = createPagingRequest(tahun, bagianKPT);

        Page<DokumenKerjaSamaDTO> dokumenKerjaSama = dokumenKerjaSamaService.getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaDTO> dokumenKerjaSamaList = dokumenKerjaSama.getData();

        return dokumenKerjaSamaService.writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, rowIdx);

    }

    public int createSheetKDN(Workbook workbook, int tahun) {
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        PagingRequest pagingRequest = createPagingRequest(tahun, bagianKDN);

        Page<DokumenKerjaSamaDTO> dokumenKerjaSama = dokumenKerjaSamaService.getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaDTO> dokumenKerjaSamaList = dokumenKerjaSama.getData();

        Sheet sheet = workbook.createSheet("KDN");

        return dokumenKerjaSamaService.writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList);
    }

    private int createContentKDN(Workbook workbook, Sheet sheet, int tahun, int rowIdx) {
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        PagingRequest pagingRequest = createPagingRequest(tahun, bagianKDN);

        Page<DokumenKerjaSamaDTO> dokumenKerjaSama = dokumenKerjaSamaService.getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaDTO> dokumenKerjaSamaList = dokumenKerjaSama.getData();

        return dokumenKerjaSamaService.writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, rowIdx);

    }

    public int createSheetKLN(Workbook workbook, int tahun) {
        int year = tahun - 1900;
        Sheet sheet = workbook.createSheet("KLN");
        PagingRequest pagingRequest = new PagingRequest();
        pagingRequest.setFilterStartDate(new Date(year, 01, 01));
        pagingRequest.setFilterEndDate(new Date(year, 12, 31));

        Search search = new Search();
        search.setValue("");
        pagingRequest.setSearch(search);

        Page<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSamaLN = dokumenKerjaSamaLuarNegeriService
                .getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSamaLNList = dokumenKerjaSamaLN.getData();

        return dokumenKerjaSamaLuarNegeriService.writeToExcel(workbook, sheet, dokumenKerjaSamaLNList);

    }

    private int createContentKLN(Workbook workbook, Sheet sheet, int tahun, int rowIdx) {
        int year = tahun - 1900;
        PagingRequest pagingRequest = new PagingRequest();
        pagingRequest.setFilterStartDate(new Date(year, 01, 01));
        pagingRequest.setFilterEndDate(new Date(year, 12, 31));

        Search search = new Search();
        search.setValue("");
        pagingRequest.setSearch(search);

        Page<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSamaLN = dokumenKerjaSamaLuarNegeriService
                .getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSamaLNList = dokumenKerjaSamaLN.getData();

        return dokumenKerjaSamaLuarNegeriService.writeToExcel(workbook, sheet, dokumenKerjaSamaLNList, rowIdx);

    }

    private void createSheetNPBK(Workbook workbook, int tahun) {

        int year = tahun - 1900;

        PagingRequest pagingRequest = new PagingRequest();
        pagingRequest.setFilterAsisten(new ArrayList<Long>());
        pagingRequest.setFilterBagian(new ArrayList<Long>());
        pagingRequest.setFilterStartDate(new Date(year, 01, 01));
        pagingRequest.setFilterEndDate(new Date(year, 12, 31));
        pagingRequest.setFilterJenisMitra(new ArrayList<Long>());
        pagingRequest.setFilterOPD(new ArrayList<Long>());
        pagingRequest.setFilterTahun(tahun);
        pagingRequest.setFilterTglBerakhirEndDate(null);
        pagingRequest.setFilterTglBerakhirStartDate(null);

        pagingRequest.setFilterTglBerakhirEndDate(new Date(2000, 12, 31));
        pagingRequest.setFilterTglBerakhirStartDate(new Date(70, 01, 01));

        pagingRequest.setIncludeNullTglBerakhir(true);

        JenisNaskah jenisNaskah = jenisNaskahService.findByNama("Naskah Perjanjian Bantuan Keuangan").get();

        pagingRequest.setFilterJenisNaskah(Arrays.asList(jenisNaskah.getId()));
        // pagingRequest.setFilterJenisNaskah(Arrays.asList(63L));

        Search search = new Search();
        search.setValue("");
        pagingRequest.setSearch(search);

        Page<DokumenKerjaSamaDTO> dokumenKerjaSama = dokumenKerjaSamaService.getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaDTO> dokumenKerjaSamaList = dokumenKerjaSama.getData();

        if ((dokumenKerjaSamaList != null) && (dokumenKerjaSamaList.size() > 0)) {
            Sheet sheet = workbook.createSheet("NPBK");

            dokumenKerjaSamaService.writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList);
        }
    }

    private void createSheetKolaborasi(Workbook workbook, int tahun) {
        int year = tahun - 1900;

        PagingRequest pagingRequest = new PagingRequest();
        pagingRequest.setFilterTahun(tahun);
        pagingRequest.setStart(0);
        pagingRequest.setLength(10000000);

        Search search = new Search();
        search.setValue("");
        pagingRequest.setSearch(search);

        Page<Kolaborasi> kolaborasi = kolaborasiService.getKolaborasi(pagingRequest);
        List<Kolaborasi> kolaborasiList = kolaborasi.getData();

        if ((kolaborasiList != null) && (kolaborasiList.size() > 0)) {
            Sheet sheet = workbook.createSheet("Kolaborasi");

            kolaborasiService.writeDokumenKerjasamaToExcel(workbook, sheet, kolaborasiList);
        }

    }

    private PagingRequest createPagingRequest(int tahun, Bagian bagian) {
        int year = tahun - 1900;

        PagingRequest pagingRequest = new PagingRequest();
        pagingRequest.setFilterAsisten(new ArrayList<Long>());
        pagingRequest.setFilterBagian(Arrays.asList(bagian.getId()));
        pagingRequest.setFilterStartDate(new Date(year, 01, 01));
        pagingRequest.setFilterEndDate(new Date(year, 12, 31));
        pagingRequest.setFilterJenisMitra(new ArrayList<Long>());
        pagingRequest.setFilterOPD(new ArrayList<Long>());
        pagingRequest.setFilterTahun(tahun);
        pagingRequest.setFilterTglBerakhirEndDate(null);
        pagingRequest.setFilterTglBerakhirStartDate(null);
        pagingRequest.setFilterJenisNaskah(new ArrayList<Long>());

        pagingRequest.setFilterTglBerakhirEndDate(new Date(2000, 12, 31));
        pagingRequest.setFilterTglBerakhirStartDate(new Date(70, 01, 01));

        pagingRequest.setIncludeNullTglBerakhir(true);

        Search search = new Search();
        search.setValue("");
        pagingRequest.setSearch(search);

        return pagingRequest;
    }

    /**
     * Membuat worbook berisi kerja sama yang belum expired.
     * 
     * @param asistenIds
     * @return
     */
    public ByteArrayInputStream dokumenBelumExpired(List<Long> asistenIds) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // createSheetBelumExpiredRekap(workbook, asistenIds);
        createSheetBelumExpiredKPT(workbook, asistenIds);
        createSheetBelumExpiredKDN(workbook, asistenIds);
        createSheetBelumExpiredKLN(workbook, asistenIds);
        // createSheetBelumExpiredNPBK(workbook, asistenIds);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());

    }

    private PagingRequest createBelumExpiredPagingRequestFor(Bagian bagian, List<Long> asistenIds) {
        PagingRequest pagingRequest = new PagingRequest();
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        Date tomorrowDate = Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant());

        pagingRequest.setFilterTglBerakhirStartDate(tomorrowDate);
        pagingRequest.setIncludeNullTglBerakhir(true);

        pagingRequest.setFilterBagian(Arrays.asList(bagian.getId()));
        pagingRequest.setFilterAsisten(asistenIds != null ? asistenIds : new ArrayList<Long>());
        pagingRequest.setFilterJenisMitra(new ArrayList<Long>());
        pagingRequest.setFilterOPD(new ArrayList<Long>());
        pagingRequest.setFilterJenisNaskah(new ArrayList<Long>());

        Search search = new Search();
        search.setValue("");
        pagingRequest.setSearch(search);

        return pagingRequest;

    }

    private int createSheetBelumExpiredKPT(Workbook workbook, List<Long> asistenIds) {
        Sheet sheet = workbook.createSheet("KPT yg belum expired");

        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();

        PagingRequest pagingRequest = createBelumExpiredPagingRequestFor(bagianKPT, asistenIds);

        Page<DokumenKerjaSamaDTO> dokumenKerjaSama = dokumenKerjaSamaService.getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaDTO> dokumenKerjaSamaList = dokumenKerjaSama.getData();

        LocalDate today = LocalDate.now();
        String title = "KPT yang Belum Expired per " + today.format(DateTimeFormatter.ISO_LOCAL_DATE);

        return dokumenKerjaSamaService.writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, 0, title);

    }

    private int createSheetBelumExpiredKDN(Workbook workbook, List<Long> asistenIds) {
        Sheet sheet = workbook.createSheet("KDN yg belum expired");

        Bagian bagianKPT = bagianService.findBySingkatan("KDN").get();

        PagingRequest pagingRequest = createBelumExpiredPagingRequestFor(bagianKPT, asistenIds);

        Page<DokumenKerjaSamaDTO> dokumenKerjaSama = dokumenKerjaSamaService.getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaDTO> dokumenKerjaSamaList = dokumenKerjaSama.getData();

        LocalDate today = LocalDate.now();
        String title = "KDN yang Belum Expired per " + today.format(DateTimeFormatter.ISO_LOCAL_DATE);

        return dokumenKerjaSamaService.writeDokumenKerjasamaToExcel(workbook, sheet, dokumenKerjaSamaList, 0, title);

    }

    private int createSheetBelumExpiredKLN(Workbook workbook, List<Long> asistenIds) {
        Sheet sheet = workbook.createSheet("KLN yg belum expired");
        PagingRequest pagingRequest = new PagingRequest();

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        Date tomorrowDate = Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant());

        pagingRequest.setFilterTglBerakhirStartDate(tomorrowDate);
        pagingRequest.setIncludeNullTglBerakhir(true);

        pagingRequest.setFilterAsisten(asistenIds != null ? asistenIds : new ArrayList<Long>());
        pagingRequest.setFilterJenisMitra(new ArrayList<Long>());
        pagingRequest.setFilterOPD(new ArrayList<Long>());
        pagingRequest.setFilterJenisNaskah(new ArrayList<Long>());

        Search search = new Search();
        search.setValue("");
        pagingRequest.setSearch(search);

        Page<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSamaLN = dokumenKerjaSamaLuarNegeriService
                .getDokumen2(pagingRequest, 0, 1000000);
        List<DokumenKerjaSamaLuarNegeriDTO> dokumenKerjaSamaLNList = dokumenKerjaSamaLN.getData();

        String title = "KLN yang Belum Expired per " + today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        ;

        return dokumenKerjaSamaLuarNegeriService.writeToExcel(workbook, sheet, dokumenKerjaSamaLNList, 0, title);

    }

    public List<Rekapitulasi> getRekapitulasiTahunan() {
        int start = 2020;
        int currentYear = Year.now().getValue();
        List<Rekapitulasi> rekapList = new ArrayList<>();

        JenisMitra jenisMitraBadanUsaha = jenisMitraService.findByNama("Badan Usaha").get();
        JenisMitra jenisMitraYayasan = jenisMitraService.findByNama("Yayasan").get();
        JenisMitra jenisMitraPerguruanTinggi = jenisMitraService.findByNama("Perguruan Tinggi").get();
        JenisMitra jenisMitraSinergisitas = jenisMitraService.findByNama("Sinergisitas").get();
        JenisMitra jenisMitraLembagaLain = jenisMitraService.findByNama("Lembaga Lain").get();
        JenisMitra jenisMitraPerseorangan = jenisMitraService.findByNama("Perseorangan").get();
        JenisMitra jenisMitraKerjaSamaDenganDaerah = jenisMitraService.findByNama("Kerja Sama dengan Daerah").get();
        JenisMitra jenisMitraPinjamPakai = jenisMitraService
                .findByNama("Kerja Sama Pinjam Pakai dan Bentuk Perjanjian dengan Instansi Lain").get();

        List<JenisMitra> jenisMitraBUdanPerseorangan = new ArrayList<JenisMitra>();
        jenisMitraBUdanPerseorangan.add(jenisMitraBadanUsaha);

        List<JenisMitra> jenisMitraYayasanPTLembagaLain = new ArrayList<JenisMitra>();
        jenisMitraYayasanPTLembagaLain.add(jenisMitraYayasan);
        jenisMitraYayasanPTLembagaLain.add(jenisMitraPerguruanTinggi);

        List<JenisMitra> jenisMitraSinergisitasList = new ArrayList<JenisMitra>();
        jenisMitraSinergisitasList.add(jenisMitraSinergisitas);

        List<JenisMitra> jenisMitraKerjaSamaDenganDaerahList = new ArrayList<JenisMitra>();
        jenisMitraKerjaSamaDenganDaerahList.add(jenisMitraKerjaSamaDenganDaerah);

        JenisNaskah jenisDokumenPKS = jenisNaskahService.findByNama("Perjanjian Kerja Sama").get();
        JenisNaskah jenisDokumenAdendumPKS = jenisNaskahService.findByNama("Adendum Perjanjian Kerja Sama").get();

        JenisNaskah jenisNaskahNonPKS = jenisNaskahService.findByNama("Non-PKS").get();

        List<JenisNaskah> jenisDokumenPKSdanAddendum = List.of(jenisDokumenPKS, jenisDokumenAdendumPKS);

        JenisNaskah jenisDokumenKesepakatanBersama = jenisNaskahService.findByNama("Kesepakatan Bersama").get();
        JenisNaskah jenisDokumenAdendumKesepakatanBersama = jenisNaskahService.findByNama("Adendum Kesepakatan Bersama")
                .get();
        JenisNaskah jenisNaskahNotaKesepakatan = jenisNaskahService.findByNama("Nota Kesepakatan").get();
        JenisNaskah jenisNaskahPerjanjianPinjamPakai = jenisNaskahService.findByNama("Perjanjian Pinjam Pakai").get();
        JenisNaskah jenisNaskahAdendumPerjanjianPinjamPakai = jenisNaskahService
                .findByNama("Adendum Perjanjian Pinjam Pakai").get();
        JenisNaskah jenisNaskahPerubahanPerjanjianPinjamPakai = jenisNaskahService
                .findByNama("Perubahan Perjanjian Pinjam Pakai").get();

        JenisNaskah jenisNaskahNPBK = jenisNaskahService.findByNama("Naskah Perjanjian Bantuan Keuangan").get();
        JenisNaskah jenisNaskahNP   = jenisNaskahService.findByNama("Perjanjian Kerahasiaan").get();

        JenisNaskahLuarNegeri jenisNaskahLNMOU = jenisNaskahLuarNegeriService.findByNama("Memorandum of Understanding")
                .get();
        JenisNaskahLuarNegeri jenisNaskahLNAdendumMOU = jenisNaskahLuarNegeriService
                .findByNama("Adendum Memorandum of Understanding").get();
        JenisNaskahLuarNegeri jenisNaskahLNLoI = jenisNaskahLuarNegeriService.findByNama("Letter of Interest").get();

        Bagian kptBagian = bagianService.findBySingkatan("KPT").get();
        Bagian kdnBagian = bagianService.findBySingkatan("KDN").get();

        for (int year = start; year <= currentYear; year++) {
            Rekapitulasi rekap = new Rekapitulasi();
            rekap.setYear(year);

            // Badan Usaha
            rekap.setKptBadanUsahaPKSCount(
                    dokumenKerjaSamaService.getCount(year, jenisDokumenPKSdanAddendum, List.of(jenisMitraBadanUsaha)));
            rekap.setKptBadanUsahaKBCount(dokumenKerjaSamaService.getCount(year,
                    List.of(jenisDokumenKesepakatanBersama, jenisDokumenAdendumKesepakatanBersama),
                    List.of(jenisMitraBadanUsaha)));
            rekap.setKptBadanUsahaNKCount(
                    dokumenKerjaSamaService.getCount(year, jenisNaskahNotaKesepakatan, List.of(jenisMitraBadanUsaha)));
            rekap.setKptBadanUsahaCount(dokumenKerjaSamaService.getCount(year, List.of(jenisMitraBadanUsaha)));

            // Perguruan Tinggi
            rekap.setKptPTPKSCount(
                    dokumenKerjaSamaService.getCount(year, jenisDokumenPKSdanAddendum,
                            List.of(jenisMitraPerguruanTinggi)));
            rekap.setKptPTKBCount(dokumenKerjaSamaService.getCount(year,
                    List.of(jenisDokumenKesepakatanBersama, jenisDokumenAdendumKesepakatanBersama),
                    List.of(jenisMitraPerguruanTinggi)));
            rekap.setKptPTNKCount(dokumenKerjaSamaService.getCount(year, jenisNaskahNotaKesepakatan,
                    List.of(jenisMitraPerguruanTinggi)));
            rekap.setKptPTCount(dokumenKerjaSamaService.getCount(year, List.of(jenisMitraPerguruanTinggi)));

            // Yayasan
            rekap.setKptYayasanPKSCount(
                    dokumenKerjaSamaService.getCount(year, jenisDokumenPKSdanAddendum, List.of(jenisMitraYayasan)));
            rekap.setKptYayasanKBCount(
                    dokumenKerjaSamaService.getCount(year,
                            List.of(jenisDokumenKesepakatanBersama, jenisDokumenAdendumKesepakatanBersama),
                            List.of(jenisMitraYayasan)));
            rekap.setKptYayasanNKCount(
                    dokumenKerjaSamaService.getCount(year, jenisNaskahNotaKesepakatan, List.of(jenisMitraYayasan)));
            rekap.setKptYayasanCount(dokumenKerjaSamaService.getCount(year, List.of(jenisMitraYayasan)));

            // Lembaga Lain
            rekap.setKptLembagaLainPKSCount(
                    dokumenKerjaSamaService.getCount(year, jenisDokumenPKSdanAddendum, List.of(jenisMitraLembagaLain)));
            rekap.setKptLembagaLainKBCount(dokumenKerjaSamaService.getCount(year,
                    List.of(jenisDokumenKesepakatanBersama, jenisDokumenAdendumKesepakatanBersama),
                    List.of(jenisMitraLembagaLain)));
            rekap.setKptLembagaLainNKCount(
                    dokumenKerjaSamaService.getCount(year, jenisNaskahNotaKesepakatan, List.of(jenisMitraLembagaLain)));
            rekap.setKptLembagaLainCount(dokumenKerjaSamaService.getCount(year, List.of(jenisMitraLembagaLain)));

            // Perseorangan
            rekap.setKptPerseoranganPKSCount(
                    dokumenKerjaSamaService.getCount(year, jenisDokumenPKSdanAddendum,
                            List.of(jenisMitraPerseorangan)));
            rekap.setKptPerseoranganKBCount(dokumenKerjaSamaService.getCount(year,
                    List.of(jenisDokumenKesepakatanBersama, jenisDokumenAdendumKesepakatanBersama),
                    List.of(jenisMitraPerseorangan)));
            rekap.setKptPerseoranganNKCount(dokumenKerjaSamaService.getCount(year, jenisNaskahNotaKesepakatan,
                    List.of(jenisMitraPerseorangan)));
            rekap.setKptPerseoranganCount(dokumenKerjaSamaService.getCount(year, List.of(jenisMitraPerseorangan)));

            // Sinergi
            rekap.setKdnSinergiPKSCount(
                    dokumenKerjaSamaService.getCount(year, jenisDokumenPKSdanAddendum,
                            List.of(jenisMitraSinergisitas)));
            rekap.setKdnSinergiKBCount(dokumenKerjaSamaService.getCount(year,
                    List.of(jenisDokumenKesepakatanBersama, jenisDokumenAdendumKesepakatanBersama),
                    List.of(jenisMitraSinergisitas)));
            rekap.setKdnSinergiNKCount(dokumenKerjaSamaService.getCount(year, jenisNaskahNotaKesepakatan,
                    List.of(jenisMitraSinergisitas)));
            rekap.setKdnSinergiNPCount(dokumenKerjaSamaService.getCount(year, jenisNaskahNP,
                    List.of(jenisMitraSinergisitas)));
            rekap.setKdnSinergiCount(dokumenKerjaSamaService.getCount(year, List.of(jenisMitraSinergisitas)));

            // Daerah Lain
            rekap.setKdnDaerahPKSCount(
                    dokumenKerjaSamaService.getCount(year, jenisDokumenPKSdanAddendum,
                            List.of(jenisMitraKerjaSamaDenganDaerah)));
            rekap.setKdnDaerahKBCount(dokumenKerjaSamaService.getCount(year,
                    List.of(jenisDokumenKesepakatanBersama, jenisDokumenAdendumKesepakatanBersama),
                    List.of(jenisMitraKerjaSamaDenganDaerah)));
            rekap.setKdnDaerahNKCount(dokumenKerjaSamaService.getCount(year, jenisNaskahNotaKesepakatan,
                    List.of(jenisMitraKerjaSamaDenganDaerah)));
            rekap.setKdnDaerahNPBKCount(dokumenKerjaSamaService.getCount(year, jenisNaskahNPBK));
            rekap.setKdnDaerahCount(dokumenKerjaSamaService.getCount(year, List.of(jenisMitraKerjaSamaDenganDaerah)));

            // Pinjam Pakai & Bentuk Perjanjian dengan instansi lain
            rekap.setKdnPinjamPakaiInstansiLainPKSCount(dokumenKerjaSamaService.getCount(year,
                    List.of(jenisNaskahPerjanjianPinjamPakai, jenisNaskahAdendumPerjanjianPinjamPakai,
                            jenisNaskahPerubahanPerjanjianPinjamPakai),
                    List.of(jenisMitraPinjamPakai)));
            rekap.setKdnPinjamPakaiInstansiLainKBCount(dokumenKerjaSamaService.getCount(year,
                    List.of(jenisDokumenKesepakatanBersama, jenisDokumenAdendumKesepakatanBersama),
                    List.of(jenisMitraPinjamPakai)));
            rekap.setKdnPinjamPakaiInstansiLainNKCount(dokumenKerjaSamaService.getCount(year,
                    jenisNaskahNotaKesepakatan, List.of(jenisMitraPinjamPakai)));
            rekap.setKdnPinjamPakaiInstansiLainNonPKSCount(dokumenKerjaSamaService.getCount(year, jenisNaskahNonPKS, List.of(jenisMitraPinjamPakai)));        
            rekap.setKdnPinjamPakaiInstansiLainCount(dokumenKerjaSamaService.getCount(year,
                    List.of(jenisMitraPinjamPakai)));

            rekap.setKlnNonPerikatanCount(dokumenKerjaSamaLuarNegeriService.getCount(year));

            rekap.setKptTotal(dokumenKerjaSamaService.getCount(year, kptBagian));
            rekap.setKdnTotal(dokumenKerjaSamaService.getCount(year, kdnBagian));
            rekap.setKlnTotal(dokumenKerjaSamaLuarNegeriService.getCount(year));
            rekap.setTotal(getCount(year));

            rekap.setPksTotal(
                    dokumenKerjaSamaService.getCount(year,
                            List.of(jenisDokumenPKS, jenisDokumenAdendumPKS, jenisNaskahPerjanjianPinjamPakai,
                                    jenisNaskahAdendumPerjanjianPinjamPakai, jenisNaskahPerubahanPerjanjianPinjamPakai),
                            0));
            rekap.setKbTotal(dokumenKerjaSamaService.getCount(year,
                    List.of(jenisDokumenKesepakatanBersama, jenisDokumenAdendumKesepakatanBersama), 0));
            rekap.setNkTotal(dokumenKerjaSamaService.getCount(year, jenisNaskahNotaKesepakatan));
            rekap.setNonPksTotal(dokumenKerjaSamaService.getCount(year, jenisNaskahNonPKS));

            rekapList.add(rekap);
        }

        return rekapList;
    }

}
