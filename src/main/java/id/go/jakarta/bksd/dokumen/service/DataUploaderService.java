package id.go.jakarta.bksd.dokumen.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import id.go.jakarta.bksd.dokumen.domain.AsistenSekretarisDaerah;
import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSama;
import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSamaLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.JenisMitra;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskah;
import id.go.jakarta.bksd.dokumen.domain.Kolaborasi;
import id.go.jakarta.bksd.dokumen.domain.OPD;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.Pemetaan;
import id.go.jakarta.bksd.dokumen.domain.RekapExcelDefinition;
import id.go.jakarta.bksd.dokumen.repository.DokumenKerjasamaRepository;
import id.go.jakarta.bksd.dokumen.repository.PemakaiRepository;

@Service
public class DataUploaderService {
    Logger logger = LoggerFactory.getLogger(getClass());

    private PemakaiRepository pemakaiRepository;
    private DokumenKerjasamaRepository dokumenKerjasamaRepository;
    private JenisNaskahService jenisNaskahService;
    private JenisMitraService jenisMitraService;
    private PemetaanService pemetaanService;
    private OPDService opdService;
    private BagianService bagianService;
    private DokumenKerjaSamaLuarNegeriService dokumenKerjaSamaLuarNegeriService;
    private KolaborasiService kolaborasiService;
    private AsistenSekretarisDaerahService asistenSekretarisDaerahService;

    public DataUploaderService(PemakaiRepository pemakaiRepository,
            DokumenKerjasamaRepository dokumenKerjasamaRepository,
            JenisNaskahService jenisNaskahService,
            JenisMitraService jenisMitraService,
            PemetaanService pemetaanService,
            OPDService opdService,
            BagianService bagianService,
            DokumenKerjaSamaLuarNegeriService dokumenKerjaSamaLuarNegeriService,
            KolaborasiService kolaborasiService,
            AsistenSekretarisDaerahService asistenSekretarisDaerahService) {
        this.pemakaiRepository = pemakaiRepository;
        this.dokumenKerjasamaRepository = dokumenKerjasamaRepository;
        this.jenisNaskahService = jenisNaskahService;
        this.jenisMitraService = jenisMitraService;
        this.pemetaanService = pemetaanService;
        this.opdService = opdService;
        this.bagianService = bagianService;
        this.dokumenKerjaSamaLuarNegeriService = dokumenKerjaSamaLuarNegeriService;
        this.kolaborasiService = kolaborasiService;
        this.asistenSekretarisDaerahService = asistenSekretarisDaerahService;
    }

    public void uploadData(MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0); // Assuming data starts from the first sheet

        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                Pemakai data = new Pemakai();
                // Read data from each cell and set it to the corresponding field in MyData
                // object
                data.setTitle(row.getCell(0).getStringCellValue());
                data.setNamaDepan(row.getCell(1).getStringCellValue());
                data.setNamaKeluarga(row.getCell(2).getStringCellValue());
                // ... (Handle other columns)
                pemakaiRepository.save(data); // Save the data object to the database
            }
        }
        workbook.close();
    }

    public void uploadKDN2022() throws IOException, InvalidFormatException {
        File file = new File("D:/Biro KSD/ksd2022.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(2);

        for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                // String jenisMitraStr = row.getCell(0).getStringCellValue();
                // JenisMitra jenisMitra = jenisMitraService.findByNama(jenisMitraStr).get();
                // dokumenKerjaSama.setJenisMitra(jenisMitra);

                dokumenKerjaSama.setBagian(
                        bagianService.findByNama("Kerja Sama Dalam Negeri dan Fasilitasi Korps Diplomatik").get());
                dokumenKerjaSama.setMitraKerjaSama(row.getCell(2).getStringCellValue());
                dokumenKerjaSama.setJudulDokumenPerjanjian(row.getCell(3).getStringCellValue());
                dokumenKerjaSama.setNoDokumenPerjanjian(row.getCell(4).getStringCellValue());

                try {
                    dokumenKerjaSama.setTglPenetapan(row.getCell(5).getDateCellValue());
                } catch (Exception e) {
                    dokumenKerjaSama.setTglPenetapan(null);
                }

                dokumenKerjaSama.setUrusanPemerintahanYgDikerjakan(row.getCell(6).getStringCellValue());

                String jenisNaskahStr = row.getCell(7).getStringCellValue();
                JenisNaskah jenisNaskah = jenisNaskahService.findByNama(jenisNaskahStr).get();

                logger.info("Jenis Naskah **** {}, [{}]", jenisNaskah, jenisNaskahStr);
                dokumenKerjaSama.setJenisDokumen(jenisNaskah);

                DataFormatter dataFormatter = new DataFormatter();

                dokumenKerjaSama.setJangkaWaktuPelaksanaan(dataFormatter.formatCellValue(row.getCell(8)));
                // dokumenKerjaSama.setPembiayaan(row.getCell(9).getStringCellValue());

                // TODO: Deal with dokumen (multiple maybe)
                // dokumenKerjaSama.getDokumen().add(null)
                XSSFHyperlink doclink = row.getCell(9).getHyperlink();
                if (doclink != null) {
                    dokumenKerjaSama.setKeterangan(row.getCell(9).getHyperlink().getAddress());
                    dokumenKerjaSama.setKeterangan2(row.getCell(9).getStringCellValue());

                    // Dokumen dokumen = new Dokumen();
                    // dokumen.setFilename(row.getCell(10).getStringCellValue());

                    // dokumenKerjaSama.getDokumen().add(dokumen);

                    // File docfile = new File(doclink.getLocation());
                    // File destfile = new File("D:/ksd_upload");
                    // FileUtils.copyFile(docfile, destfile );
                }

                XSSFCell keterangan3 = row.getCell(10);
                dokumenKerjaSama.setKeterangan3(keterangan3 != null ? keterangan3.getStringCellValue() : "");

                XSSFCell keterangan4 = row.getCell(11);
                dokumenKerjaSama.setKeterangan4(keterangan4 != null ? keterangan4.getStringCellValue() : "");

                dokumenKerjasamaRepository.save(dokumenKerjaSama);
            }
        }

        workbook.close();
    }

    public void uploadNPBK2022() throws IOException, InvalidFormatException {
        File file = new File("D:/Biro KSD/ksd2022.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(3);

        for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                // String jenisMitraStr = row.getCell(0).getStringCellValue();
                // JenisMitra jenisMitra = jenisMitraService.findByNama(jenisMitraStr).get();
                // dokumenKerjaSama.setJenisMitra(jenisMitra);

                dokumenKerjaSama.setBagian(
                        bagianService.findByNama("Kerja Sama Dalam Negeri dan Fasilitasi Korps Diplomatik").get());

                dokumenKerjaSama.setMitraKerjaSama(row.getCell(1).getStringCellValue());
                String judul = "Pemberian Bantuan Keuangan Dalam Bentuk Uang";
                dokumenKerjaSama.setJudulDokumenPerjanjian(judul);
                // dokumenKerjaSama.setNoDokumenPerjanjian(row.getCell(4).getStringCellValue());

                try {
                    dokumenKerjaSama.setTglPenetapan(row.getCell(3).getDateCellValue());
                } catch (Exception e) {
                    dokumenKerjaSama.setTglPenetapan(null);
                }

                // dokumenKerjaSama.setUrusanPemerintahanYgDikerjakan(row.getCell(6).getStringCellValue());

                String jenisNaskahStr = "Naskah Perjanjian Bantuan Keuangan";
                JenisNaskah jenisNaskah = jenisNaskahService.findByNama(jenisNaskahStr).get();

                logger.info("Jenis Naskah **** {}, [{}]", jenisNaskah, jenisNaskahStr);
                dokumenKerjaSama.setJenisDokumen(jenisNaskah);

                DataFormatter dataFormatter = new DataFormatter();

                dokumenKerjaSama.setJangkaWaktuPelaksanaan(dataFormatter.formatCellValue(row.getCell(5)));
                // dokumenKerjaSama.setPembiayaan(row.getCell(9).getStringCellValue());

                // TODO: Deal with dokumen (multiple maybe)
                // dokumenKerjaSama.getDokumen().add(null)
                XSSFHyperlink doclink = row.getCell(6).getHyperlink();
                if (doclink != null) {
                    dokumenKerjaSama.setKeterangan(row.getCell(6).getHyperlink().getAddress());
                    dokumenKerjaSama.setKeterangan2(row.getCell(6).getStringCellValue());

                    // Dokumen dokumen = new Dokumen();
                    // dokumen.setFilename(row.getCell(10).getStringCellValue());

                    // dokumenKerjaSama.getDokumen().add(dokumen);

                    // File docfile = new File(doclink.getLocation());
                    // File destfile = new File("D:/ksd_upload");
                    // FileUtils.copyFile(docfile, destfile );
                }

                dokumenKerjasamaRepository.save(dokumenKerjaSama);
            }
        }

        workbook.close();
    }

    public void uploadKPT2022() throws IOException, InvalidFormatException {
        File file = new File("D:/Biro KSD/ksd2022.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(1);

        for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                String jenisMitraStr = row.getCell(0).getStringCellValue();
                JenisMitra jenisMitra = jenisMitraService.findByNama(jenisMitraStr).get();
                dokumenKerjaSama.setJenisMitra(jenisMitra);

                dokumenKerjaSama.setMitraKerjaSama(row.getCell(2).getStringCellValue());
                dokumenKerjaSama.setJudulDokumenPerjanjian(row.getCell(3).getStringCellValue());
                dokumenKerjaSama.setNoDokumenPerjanjian(row.getCell(4).getStringCellValue());
                dokumenKerjaSama.setTglPenetapan(row.getCell(5).getDateCellValue());
                dokumenKerjaSama.setUrusanPemerintahanYgDikerjakan(row.getCell(6).getStringCellValue());

                String jenisNaskahStr = row.getCell(7).getStringCellValue();
                JenisNaskah jenisNaskah = jenisNaskahService.findByNama(jenisNaskahStr).get();

                logger.info("Jenis Naskah **** {}, [{}]", jenisNaskah, jenisNaskahStr);
                dokumenKerjaSama.setJenisDokumen(jenisNaskah);

                DataFormatter dataFormatter = new DataFormatter();

                dokumenKerjaSama.setJangkaWaktuPelaksanaan(dataFormatter.formatCellValue(row.getCell(8)));
                dokumenKerjaSama.setPembiayaan(row.getCell(9).getStringCellValue());

                // TODO: Deal with dokumen (multiple maybe)
                // dokumenKerjaSama.getDokumen().add(null)
                XSSFHyperlink doclink = row.getCell(10).getHyperlink();
                if (doclink != null) {
                    dokumenKerjaSama.setKeterangan(row.getCell(10).getHyperlink().getAddress());
                    dokumenKerjaSama.setKeterangan2(row.getCell(10).getStringCellValue());

                }

                dokumenKerjasamaRepository.save(dokumenKerjaSama);
            }
        }

        workbook.close();
    }

    public void uploadKLN2022() throws IOException, InvalidFormatException {
        File file = new File("D:/Biro KSD/ksd2022.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(4);

        for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                DokumenKerjaSamaLuarNegeri dokumenKerjaSama = new DokumenKerjaSamaLuarNegeri();

                dokumenKerjaSama.setTahun(2022);

                String subjek = row.getCell(1).getStringCellValue();
                dokumenKerjaSama.setSubjek(subjek);
                // dokumenKerjaSama.setTgl(row.getCell(2).getDateCellValue());
                dokumenKerjaSama.setJudulDokumen(row.getCell(3).getStringCellValue());
                dokumenKerjaSama.setKeterangan(row.getCell(4).getStringCellValue());

                XSSFHyperlink doclink = row.getCell(5).getHyperlink();
                dokumenKerjaSama.setLinkDokumen(doclink.getAddress());

                dokumenKerjaSama.setPenanggungJawabTertandaTangan(row.getCell(6).getStringCellValue());
                // dokumenKerjaSama.setJenisDokumenLuarNegeri(null);

                dokumenKerjaSamaLuarNegeriService.save(dokumenKerjaSama);
            }
        }

        workbook.close();
    }

    public void uploadPemetaan2024() throws IOException, InvalidFormatException {
        File file = new File("D:/Biro KSD/Rekap Data Pemetaan Rencana Kerjasama 2024.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Bagian KDNBagian = bagianService.findBySingkatan("KDN").get();
        Bagian KPTBagian = bagianService.findBySingkatan("KPT").get();

        // 1. DINAS KETAHANAN PANGAN, KELAUTAN DAN PERTANIAN
        uploadPemetaanLuarNegri(sheet, "Dinas Ketahanan Pangan, Kelautan dan Pertanian", 6, 6);
        uploadPemetaanDaerahLain(sheet, KDNBagian, "Dinas Ketahanan Pangan, Kelautan dan Pertanian", 11, 18);
        uploadPemetaanDaerahLain(sheet, KPTBagian, "Dinas Ketahanan Pangan, Kelautan dan Pertanian", 25, 28);

        // 2. DINAS BINA MARGA
        uploadPemetaanLuarNegri(sheet, "Dinas Bina Marga", 34, 34);
        uploadPemetaanDaerahLain(sheet, KDNBagian, "Dinas Bina Marga", 39, 39);
        uploadPemetaanDaerahLain(sheet, KPTBagian, "Dinas Bina Marga", 44, 44);

        // 3. Dinas Perindustrian, Perdagangan, Koperasi, Usaha Kecil dan Menengah
        String opd = "Dinas Perindustrian, Perdagangan Koperasi, Usaha Kecl dan Menengah";
        uploadPemetaanLuarNegri(sheet, opd, 51, 51);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 56, 57);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 63, 66);

        // /4. Biro Kesejahteraan Sosial Setda Provinsi DKI Jakarta
        opd = "Dinas Kesejahteraan Sosial";
        uploadPemetaanLuarNegri(sheet, opd, 74, 74);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 79, 79);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 84, 84);

        // 5. Badan Penanggulangan Bencana Daerah
        opd = "Pelaksana Penanggulangan Bencana Daerah";
        uploadPemetaanLuarNegri(sheet, opd, 91, 91);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 96, 96);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 101, 102);

        // 6. Dinas Kesehatan
        opd = "Dinas Kesehatan";
        uploadPemetaanLuarNegri(sheet, opd, 112, 112);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 117, 117);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 122, 124);

        // 7. Dinas Sumber Daya Air
        opd = "Dinas Sumber Daya Air";
        uploadPemetaanLuarNegri(sheet, opd, 132, 132);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 137, 138);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 146, 147);

        // 8. Dinas Sosial
        opd = "Dinas Sosial";
        uploadPemetaanLuarNegri(sheet, opd, 154, 154);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 159, 160);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 165, 166);

        // 9. Dinas Penanaman Modal dan Pelayanan Terpadu Satu Pintu
        opd = "Dinas Penanaman Modal dan Pelayanan Terpadu Satu Pintu";
        uploadPemetaanLuarNegri(sheet, opd, 174, 174);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 179, 179);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 184, 184);

        // 10. Badan Pembinaan Badan Usaha Milik Daerah
        opd = "Badan Pembinaan Badan Usaha Milik Daerah";
        uploadPemetaanLuarNegri(sheet, opd, 191, 191);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 196, 196);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 201, 201);

        // 11. DINAS PERHUBUNGAN
        opd = "Dinas Perhubungan";
        uploadPemetaanLuarNegri(sheet, opd, 208, 208);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 213, 213);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 218, 219);

        // 12. DINAS KEBUDAYAAN
        opd = "Dinas Kebudayaan";
        uploadPemetaanLuarNegri(sheet, opd, 226, 227);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 235, 236);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 243, 244);

        // 13. Biro Umum dan Administrasi Sekretariat Daerah Setda Provinsi DKI Jakarta
        opd = "Biro Umum dan Administrasi Sekretariat Daerah";
        uploadPemetaanLuarNegri(sheet, opd, 252, 252);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 257, 257);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 262, 262);

        // 14. Dinas Tenaga Kerja, Transmigrasi dan Energi
        opd = "Dinas Tenaga Kerja, Transmigrasi dan Energi";
        uploadPemetaanLuarNegri(sheet, opd, 269, 269);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 274, 275);
        // uploadPemetaanDaerahLain(sheet, opd, , 262);

        // 15. Sekretariat DPRD
        opd = "Sekretariat DPRD";
        uploadPemetaanLuarNegri(sheet, opd, 291, 291);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 296, 296);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 301, 301);

        // 16. Sekretariat Kota Administrasi Jakarta Utara
        opd = "Sekretariat Kota Administrasi Jakarta Utara";
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 308, 308);
        // uploadPemetaanDaerahLain(sheet, opd, , 296);
        // uploadPemetaanDaerahLain(sheet, opd, 301, 301);

        // 17. Dinas Pemberdayaan, Perlindungan Anak dan Pengendalian Penduduk
        opd = "Dinas Pemberdayaan, Perlindungan Anak, dan Pengendalian Penduduk";
        uploadPemetaanLuarNegri(sheet, opd, 315, 315);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 320, 320);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 325, 327);

        // 18. Badan Pelayanan Pengadaan Barang/Jasa
        opd = "Badan Pelayanan Pengadaan Barang/Jasa Daerah";
        uploadPemetaanLuarNegri(sheet, opd, 334, 334);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 339, 339);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 344, 344);

        // 19. Biro Pendidikan dan Mental Spiritual Setda Provinsi DKI Jakarta
        opd = "Biro Pendidikan dan Mental Spiritual";
        uploadPemetaanLuarNegri(sheet, opd, 352, 352);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 357, 357);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 362, 362);

        // 20. DINAS LINGKUNGAN HIDUP
        opd = "Dinas Lingkungan Hidup";
        uploadPemetaanLuarNegri(sheet, opd, 369, 369);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 374, 375);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 381, 389);

        // 21. Dinas Penanggulangan Kebakaran dan Penyelamatan
        opd = "Dinas Penanggulangan Kebakaran & Penyelamatan";
        uploadPemetaanLuarNegri(sheet, opd, 396, 396);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 401, 401);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 406, 407);

        // 22. Biro Organisasi dan Reformasi Birokrasi Setda Provinsi DKI Jakarta
        opd = "Biro Organisasi dan reformasi Birokrasi";
        uploadPemetaanLuarNegri(sheet, opd, 416, 416);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 421, 421);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 426, 426);

        // 23. Badan Kesatuan Bangsa dan Politik Provinsi DKI Jakarta
        opd = "Badan Kesatuan Bangsa dan politik";
        uploadPemetaanLuarNegri(sheet, opd, 434, 434);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 439, 439);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 444, 444);

        // 24. Badan Pengelolaan Keuangan Daerah Provinsi DKI Jakarta
        opd = "Badan Pengelolaan Keuangan Daerah";
        uploadPemetaanLuarNegri(sheet, opd, 452, 452);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 457, 457);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 462, 462);

        // 25. Inspektorat
        opd = "Inspektorat";
        uploadPemetaanLuarNegri(sheet, opd, 470, 470);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 475, 477);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 482, 484);

        // 26. Walikota Jakarta Selatan
        opd = "Walikota Jakarta Selatan";
        uploadPemetaanLuarNegri(sheet, opd, 492, 492);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 497, 497);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 502, 502);

        // 27. Biro Kepala Daerah Sekretariat Daerah Setda Provinsi DKI Jakarta
        opd = "Biro Kepala Daerah";
        uploadPemetaanLuarNegri(sheet, opd, 508, 508);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 513, 513);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 518, 518);

        // 28. Dinas Pemuda dan Olahraga
        opd = "Dinas Pemuda dan Olahraga";
        uploadPemetaanLuarNegri(sheet, opd, 524, 524);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 529, 538);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 543, 548);

        // 29. Dinas Perumahan Rakyat Dan Kawasan Permukiman
        opd = "Dinas Perumahan Rakyat dan Kawasan Permukiman";
        uploadPemetaanLuarNegri(sheet, opd, 555, 555);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 560, 560);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 565, 565);

        // 30. Dinas Cipta Karya, Tata Ruang dan Pertanahan
        opd = "Dinas Cipta Karya, Tata Ruang dan Pertanahan";
        uploadPemetaanLuarNegri(sheet, opd, 572, 572);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 577, 577);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 582, 582);

        // 31. Biro Pembangunan dan Lingkungan Hidup
        opd = "Biro Pembangunan dan Lingkungan Hidup";
        uploadPemetaanLuarNegri(sheet, opd, 588, 588);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 593, 593);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 598, 598);

        // 32. Biro Pemerintahan
        opd = "Biro Pemerintahan";
        uploadPemetaanLuarNegri(sheet, opd, 604, 604);
        uploadPemetaanDaerahLain(sheet, KDNBagian, opd, 609, 609);
        uploadPemetaanDaerahLain(sheet, KPTBagian, opd, 614, 614);

        workbook.close();

    }

    private void uploadPemetaanLuarNegri(XSSFSheet sheet, String opd, int startRow, int endRow)
            throws IOException, InvalidFormatException {
        logger.info("OPD : [{}], [LN], startRow [{}], endRow [{}]", opd, startRow, endRow);
        for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                Pemetaan pemetaan = new Pemetaan();

                // String opd = row.getCell(2).getStringCellValue();
                // String opd = "Dinas Ketahanan Pangan, Kelautan dan Pertanian";
                Bagian bagian = bagianService.findBySingkatan("KLN").get();
                pemetaan.setBagian(bagian);
                pemetaan.setOpd(opdService.findByNama(opd).get());
                pemetaan.setUrusanPemerintahan(row.getCell(3).getStringCellValue());
                pemetaan.setKegiatan(row.getCell(4).getStringCellValue());
                pemetaan.setJangkaWaktu(row.getCell(5).getStringCellValue());
                pemetaan.setNamaCalonMitra(row.getCell(6).getStringCellValue());
                pemetaan.setNoKontrakCalonMitra(row.getCell(7).getStringCellValue());
                pemetaan.setWaktuPelaksanaan(row.getCell(8).getRawValue());
                pemetaan.setPic(row.getCell(9).getStringCellValue());
                pemetaan.setSumberPembiayaan(row.getCell(10).getStringCellValue());

                pemetaanService.save(pemetaan);
            }

        }

    }

    private void uploadPemetaanDaerahLain(XSSFSheet sheet, Bagian bagian, String opd, int startRow, int endRow)
            throws IOException, InvalidFormatException {
        logger.info("OPD : [{}], [daerah], startRow [{}], endRow [{}]", opd, startRow, endRow);
        for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                Pemetaan pemetaan = new Pemetaan();

                // String opd = row.getCell(2).getStringCellValue();
                pemetaan.setBagian(bagian);
                pemetaan.setOpd(opdService.findByNama(opd).get());
                pemetaan.setUrusanPemerintahan(row.getCell(3).getStringCellValue());
                pemetaan.setKegiatan(row.getCell(4).getStringCellValue());
                pemetaan.setJangkaWaktu(row.getCell(5).getStringCellValue());
                pemetaan.setNamaCalonMitra(row.getCell(6).getStringCellValue());
                pemetaan.setNoKontrakCalonMitra(row.getCell(7).getStringCellValue());
                pemetaan.setSkalaPrioritas(row.getCell(8).getStringCellValue());
                pemetaan.setWaktuPelaksanaanTW1(row.getCell(9).getStringCellValue());
                pemetaan.setWaktuPelaksanaanTW2(row.getCell(10).getStringCellValue());
                pemetaan.setWaktuPelaksanaanTW3(row.getCell(11).getStringCellValue());
                pemetaan.setWaktuPelaksanaanTW4(row.getCell(12).getStringCellValue());

                XSSFCell pic = row.getCell(13);
                pemetaan.setPic(pic != null ? pic.getStringCellValue() : "");

                XSSFCell pembiayaan = row.getCell(14);
                pemetaan.setSumberPembiayaan(pembiayaan != null ? pembiayaan.getStringCellValue() : "");

                pemetaanService.save(pemetaan);
            }

        }

    }

    /**
     * Download a Document file in PDF format.
     *
     * @param realFileId file ID of any workspace document format file.
     * @return byte array stream if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     * 
     *                     https://developers.google.com/drive/api/guides/manage-downloads
     */
    public ByteArrayOutputStream downloadFile(String realFileId) throws IOException {

        /**
         * Load pre-authorized user credentials from the environment.
         * TODO(developer) - See https://developers.google.com/identity for
         * guides on implementing OAuth2 for your application.
         */
        // GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        // .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));

        // GoogleCredentials credentials = ServiceAccountCredentials
        // .fromStream(new FileInputStream("credentials.json"))
        // .createScoped(DriveScopes.DRIVE_FILE);

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json"))
                .createScoped(DriveScopes.DRIVE_FILE);

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();

        try {
            OutputStream outputStream = new ByteArrayOutputStream();

            // service.files().get(realFileId).executeMediaAndDownloadTo(outputStream);
            service.files().export(realFileId, "application/pdf")
                    .executeMediaAndDownloadTo(outputStream);

            return (ByteArrayOutputStream) outputStream;
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to move file: " + e.getDetails());
            throw e;
        }
    }

    // public ByteArrayOutputStream downloadFile2(String fileId) throws IOException
    // {
    // //JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    // HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    // GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
    // GsonFactory.getDefaultInstance(), new InputStreamReader(new
    // FileInputStream("client_secret.json")));

    // // Set up the OAuth 2.0 flow
    // GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
    // HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), clientSecrets,
    // Collections.singleton(DriveScopes.DRIVE))
    // .setDataStoreFactory(DATA_STORE_FACTORY)
    // .setAccessType("offline")
    // .build();

    // // Authorize your application
    // String authorizationUrl =
    // flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    // System.out.println("Open the following URL in your browser: ");
    // System.out.println(authorizationUrl);
    // System.out.print("Enter the authorization code: ");
    // String code = new BufferedReader(new
    // InputStreamReader(System.in)).readLine();

    // // Exchange the authorization code for credentials
    // GoogleTokenResponse tokenResponse =
    // flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();

    // GoogleCredential credential = new GoogleCredential.Builder()
    // .setTransport(HTTP_TRANSPORT)
    // .setJsonFactory(GsonFactory.getDefaultInstance())
    // .setClientSecrets(clientSecrets)
    // .build()
    // .setFromTokenResponse(tokenResponse);

    // // Create a Drive service using the credentials
    // Drive driveService = new Drive.Builder(HTTP_TRANSPORT,
    // GsonFactory.getDefaultInstance(), credential)
    // .setApplicationName("xx")
    // .build();

    // OutputStream outputStream = new FileOutputStream("downloaded-file.txt");

    // driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);

    // }

    public void uploadKSD2023() throws IOException, InvalidFormatException {
        String fileName = RekapExcelDefinition.REKAP_KSD_2023;

        // uploadKPT2023(fileName);
        // uploadKDN2023(fileName);
        // uploadKLN2023(fileName);
        uploadKolaborasi2023(fileName);

    }

    public void uploadKPT2023(String fileName) throws IOException, InvalidFormatException {
        int tahun = 2023;
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(2);

        int from = 0;
        int to = sheet.getLastRowNum();
        Bagian bagian = bagianService.findBySingkatan("KPT").get();

        for (int rowNum = from; rowNum <= to; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);

            if (!isRowToSkip(row)) {
                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagian, dokumenKerjaSama, row,
                        1, -1, 4, 5, 7,
                        8, -1, 10, 2, 6,
                        11, 12, -1, 15);

                dokumenKerjasamaRepository.save(dokumenKerjaSama);

            }
        }

        workbook.close();

    }

    public void uploadKDN2023(String fileName) throws IOException, InvalidFormatException {
        int tahun = 2023;
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(4);

        int from = 0;
        int to = sheet.getLastRowNum();
        Bagian bagian = bagianService.findBySingkatan("KDN").get();

        for (int rowNum = from; rowNum <= to; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);

            if (!isRowToSkip(row)) {
                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagian, dokumenKerjaSama, row,
                        1, -1, 3, 4, 5,
                        6, -1, 8, 9, 10,
                        11, 12, -1, 14);

                dokumenKerjasamaRepository.save(dokumenKerjaSama);

            }
        }

        workbook.close();

    }

    public void uploadKLN2023(String fileName) throws IOException, InvalidFormatException {
        int tahun = 2023;
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(6);

        for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                DokumenKerjaSamaLuarNegeri dokumenKerjaSamaLuarNegeri = new DokumenKerjaSamaLuarNegeri();

                dokumenKerjaSamaLuarNegeri.setTahun(tahun);
                dokumenKerjaSamaLuarNegeri.setSubjek(row.getCell(1).getStringCellValue());
                // dokumenKerjaSamaLuarNegeri.setTgl(row.getCell(2).getDateCellValue());
                dokumenKerjaSamaLuarNegeri.setJudulDokumen(row.getCell(3).getStringCellValue());
                dokumenKerjaSamaLuarNegeri.setKeterangan(row.getCell(4).getStringCellValue());
                dokumenKerjaSamaLuarNegeri.setLinkDokumen(row.getCell(5).getHyperlink().getAddress());
                dokumenKerjaSamaLuarNegeri.setPenanggungJawabTertandaTangan(row.getCell(6).getStringCellValue());

                dokumenKerjaSamaLuarNegeriService.save(dokumenKerjaSamaLuarNegeri);
            }
        }

        workbook.close();
    }

    public void uploadKolaborasi2023(String fileName) throws IOException, InvalidFormatException {
        int tahun = 2023;
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(7);

        DataFormatter dataFormatter = new DataFormatter();

        for (int rowNum = 4; rowNum <= sheet.getLastRowNum(); rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                Kolaborasi kolaborasi = new Kolaborasi();

                kolaborasi.setTahun(2023);
                kolaborasi.setMitraKerjaSama(row.getCell(1).getStringCellValue());
                kolaborasi.setNamaDanTempatKejadian(row.getCell(2).getStringCellValue());

                kolaborasi.setWaktu(dataFormatter.formatCellValue(row.getCell(3)));

                String bagianStr = row.getCell(4).getStringCellValue();
                kolaborasi.setBagian(parseBagian(bagianStr));

                kolaborasi.setKeterangan(row.getCell(5).getStringCellValue());

                kolaborasiService.save(kolaborasi);
            }
        }

        workbook.close();
    }

    // ******************* Start of 2021 **********************************
    // ********************************************************************

    public void uploadKSD2021() throws IOException, InvalidFormatException {
        String fileName = RekapExcelDefinition.REKAP_KSD_2021;

        // Work in progress
        // uploadKPT2021(fileName);
        // uploadKDN2021(fileName);
        uploadNPBK2021(fileName);

        // uploadKLN2021(fileName);
        // uploadKolaborasi2021(fileName);

    }

    /**
     * Work in progress
     * 
     * @param fileName
     * @param from
     * @param to
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void uploadKPT2021(String fileName) throws IOException, InvalidFormatException {
        int tahun = 2021;
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(2);

        int from = 0;
        int to = sheet.getLastRowNum();
        Bagian bagian = bagianService.findBySingkatan("KPT").get();

        for (int rowNum = from; rowNum <= to; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);

            if (!isRowToSkip(row)) {
                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagian, dokumenKerjaSama, row,
                        -1, 2, 3, 4, 5,
                        6, -1, -1, 8, 10,
                        11, 12, -1, -1);

                dokumenKerjasamaRepository.save(dokumenKerjaSama);

            }
        }

        workbook.close();

    }

    public void uploadKDN2021(String fileName) throws IOException, InvalidFormatException {
        int tahun = 2021;
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(3);

        int from = 0;
        int to = sheet.getLastRowNum();
        Bagian bagian = bagianService.findBySingkatan("KDN").get();

        for (int rowNum = from; rowNum <= to; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);

            if (!isRowToSkip(row)) {
                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagian, dokumenKerjaSama, row,
                        -1, 1, 2, 3, 4, 5, -1, -1, -1, 8, 9, 10, -1, -1);

                dokumenKerjasamaRepository.save(dokumenKerjaSama);

            }
        }

        workbook.close();

    }

    public void uploadNPBK2021(String fileName) throws IOException, InvalidFormatException {
        RekapExcelDefinition NPBKDef2021 = RekapExcelDefinition.getRekapDef(2021, "NPBK");
        int tahun = 2021;
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(4);

        int from = 0;
        int to = sheet.getLastRowNum();
        Bagian bagian = bagianService.findBySingkatan("KDN").get();
        JenisNaskah jenisNaskahNPBK = jenisNaskahService.findByNama("Naskah Perjanjian Bantuan Keuangan").get();

        for (int rowNum = from; rowNum <= to; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);

            if (!isRowToSkip(row)) {
                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagian, dokumenKerjaSama, row,
                        NPBKDef2021.getJenisMitraIdx(),
                        NPBKDef2021.getOpdIdx(),
                        NPBKDef2021.getMitraKerjaSamaIdx(),
                        NPBKDef2021.getJudulIdx(),
                        NPBKDef2021.getNoDokumenIdx(),
                        NPBKDef2021.getTglPenetapanIdx(),
                        NPBKDef2021.getWaktuPelaksanaanBerakhirIdx(),
                        NPBKDef2021.getUrusanIdx(),
                        NPBKDef2021.getJenisNaskahIdx(),
                        NPBKDef2021.getJangkaWaktuIdx(),
                        NPBKDef2021.getPembiayaanIdx(),
                        NPBKDef2021.getDocLinkIdx(),
                        NPBKDef2021.getRumpunOpdIdx(),
                        NPBKDef2021.getKeteranganIdx());
                dokumenKerjaSama.setJenisDokumen(jenisNaskahNPBK);

                dokumenKerjasamaRepository.save(dokumenKerjaSama);

            }
        }

        workbook.close();

    }

    // ******************* End of 2021 **********************************

    // ******************* Start of 2020 ********************************
    public void uploadKSD2020() throws IOException, InvalidFormatException {
        String fileName = RekapExcelDefinition.REKAP_KSD_2020;

        uploadKPT2020(fileName);

        // uploadKDN2020(fileName);

    }

    public void uploadKPT2020(String fileName) throws IOException, InvalidFormatException {
        int tahun = 2020;
        File file = new File("D:/Biro KSD/" + fileName);
        RekapExcelDefinition KPTDef2020 = RekapExcelDefinition.getRekapDef(tahun, "KPT");
        XSSFWorkbook workbook = new XSSFWorkbook(file);


        XSSFSheet sheet = workbook.getSheetAt(KPTDef2020.getSheetNo());
        //XSSFSheet sheet = workbook.getSheet("KPT"); //temporary

        int from = 0;
        int to = sheet.getLastRowNum();
        Bagian bagian = bagianService.findBySingkatan("KPT").get();


        for (int rowNum = from; rowNum <= to; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);

            if (!isRowToSkip(row)) {
                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagian, dokumenKerjaSama, row,
                                                            KPTDef2020.getJenisMitraIdx(),
                                                            KPTDef2020.getOpdIdx(),
                                                            KPTDef2020.getMitraKerjaSamaIdx(),
                                                            KPTDef2020.getJudulIdx(),
                                                            KPTDef2020.getNoDokumenIdx(),
                                                            KPTDef2020.getTglPenetapanIdx(),
                                                            KPTDef2020.getWaktuPelaksanaanBerakhirIdx(),
                                                            KPTDef2020.getUrusanIdx(),
                                                            KPTDef2020.getJenisNaskahIdx(),
                                                            KPTDef2020.getJangkaWaktuIdx(),
                                                            KPTDef2020.getPembiayaanIdx(),
                                                            KPTDef2020.getDocLinkIdx(),
                                                            KPTDef2020.getRumpunOpdIdx(),
                                                            KPTDef2020.getKeteranganIdx());

                dokumenKerjasamaRepository.save(dokumenKerjaSama);

            }
        }

        workbook.close();

    }

    // public void uploadKDN2020(String fileName) throws IOException, InvalidFormatException {
    //     int tahun = 2020;
    //     File file = new File("D:/Biro KSD/" + fileName);
    //     XSSFWorkbook workbook = new XSSFWorkbook(file);
    //     XSSFSheet sheet = workbook.getSheetAt(4);

    //     int from = 0;
    //     int to = sheet.getLastRowNum();
    //     Bagian bagian = bagianService.findBySingkatan("KDN").get();

    //     for (int rowNum = from; rowNum <= to; rowNum++) {
    //         XSSFRow row = sheet.getRow(rowNum);

    //         if (!isRowToSkip(row)) {
    //             DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

    //             dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagian, dokumenKerjaSama, row,
    //                     1, -1, 3, 4, 5,
    //                     6, -1, 8, 9, 10,
    //                     11, 12, -1, 14);

    //             dokumenKerjasamaRepository.save(dokumenKerjaSama);

    //         }
    //     }

    //     workbook.close();

    // }




    // ******************** End of 2020 *********************************

    /**
     * Check for empty row in excel
     * 
     * @param row
     * @return
     */
    private boolean isRowEmpty(Row row) {
        boolean isEmpty = true;
        DataFormatter dataFormatter = new DataFormatter();

        if (row != null) {
            for (Cell cell : row) {
                if (dataFormatter.formatCellValue(cell).trim().length() > 0) {
                    isEmpty = false;
                    break;
                }
            }
        }

        return isEmpty;
    }

    public boolean isHeaderRow(Row row, String[] expectedHeaders) {
        int cellIndex = 0;
        for (Cell cell : row) {
            if (cell.getCellType() == CellType.STRING) {
                if (!cell.getStringCellValue().trim().equals(expectedHeaders[cellIndex])) {
                    return false;
                }
                cellIndex++;
            } else {
                return false; // Header should be strings, ideally
            }
        }
        return true;
    }

    public boolean isHeaderRow(Row row, String[] expectedHeaders, int limit) {
        int cellIndex = 0;
        for (Cell cell : row) {
            if (cellIndex == limit) {
                break;
            }

            if (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.BLANK) {
                if (!cell.getStringCellValue().trim().equals(expectedHeaders[cellIndex])) {
                    return false;
                }
                cellIndex++;
            } else {
                return false; // Header should be strings, ideally
            }
        }
        return true;
    }

    public int findHeaderRowIndex(Sheet sheet) {
        int rowIndex = 0;
        for (Row row : sheet) {
            if (isHeaderRow(row, new String[] { "Expected", "Header", "Names" })) {
                return rowIndex;
            }
            rowIndex++;
        }
        return -1; // Header not found
    }

    public int findHeaderRowIndex(Sheet sheet, String[] headerNames, int limit) {
        int rowIndex = 0;
        for (Row row : sheet) {
            if (isHeaderRow(row, headerNames, limit)) {
                return rowIndex;
            }
            rowIndex++;
        }
        return -1; // Header not found
    }

    public boolean isRowToSkip(Row row) {
        boolean result = false;
        if (row == null) {
            return true;
        }

        if (isRowEmpty(row)) {
            return true;
        }

        // 2022
        String[] kpt2022Header1 = {
                "", "", "", "", "Bagian Kerja Sama Pihak Ketiga Tahun 2022", "", "", "", "", ""
        };

        String[] kpt2022Header2 = {
                "NO", "Kelompok Kerja Sama", "Instansi Terkait", "Mitra Kerja Sama", "Jenis Naskah Kerja Sama",
                "Judul Naskah Kerja Sama", "Nomor Naskah Kerja Sama", "Jangka Waktu Pelaksanaan", "Tanggal Penetapan",
                "Akhir Kerja Sama"

        };

        String[] kpt2022Header3 = {
                "", "Badan Usaha", "", "", "", "", "", "", "", ""
        };

        String[] kpt2022Header4 = {
                "", "Perguruan Tinggi", "", "", "", "", "", "", "", ""
        };

        String[] kpt2022Header5 = {
                "", "Yayasan", "", "", "", "", "", "", "", ""
        };

        String[] kpt2022Header6 = {
                "", "Lembaga Lain", "", "", "", "", "", "", "", ""
        };
        int kpt2022limit = 10;

        String[] kdn2022Header1 = {
                "", "", "", "", "Bagian Kerja Sama Dalam Negeri Tahun 2022", "", "", "", "", "", ""
        };

        String[] kdn2022Header2 = {
                "NO", "Kelompok Kerja Sama", "Instansi Terkait", "Mitra Kerja Sama", "Jenis Naskah Kerja Sama",
                "Judul Naskah Kerja Sama", "Nomor Naskah Kerja Sama", "Jangka Waktu Pelaksanaan", "Tanggal Penetapan",
                "Akhir Kerja Sama"

        };

        String[] kdn2022Header3 = {
                "", "Sinergisitas", "", "", "", "", "", "", "", ""
        };

        String[] kdn2022Header4 = {
                "", "Kerja Sama dengan Daerah", "", "", "", "", "", "", "", ""
        };

        if (isHeaderRow(row, kpt2022Header1, kpt2022limit)) {
            return true;
        }

        if (isHeaderRow(row, kpt2022Header2, kpt2022limit)) {
            return true;
        }

        if (isHeaderRow(row, kpt2022Header3, kpt2022limit)) {
            return true;
        }

        if (isHeaderRow(row, kpt2022Header4, kpt2022limit)) {
            return true;
        }

        if (isHeaderRow(row, kpt2022Header5, kpt2022limit)) {
            return true;
        }

        if (isHeaderRow(row, kpt2022Header6, kpt2022limit)) {
            return true;
        }

        if (isHeaderRow(row, kdn2022Header1, kpt2022limit)) {
            return true;
        }

        if (isHeaderRow(row, kdn2022Header2, kpt2022limit)) {
            return true;
        }
        if (isHeaderRow(row, kdn2022Header3, kpt2022limit)) {
            return true;
        }
        if (isHeaderRow(row, kdn2022Header4, kpt2022limit)) {
            return true;
        }

        // End of 2022

        // ************************************** Start of Header 2023
        int limit = 10;

        String[] kptHeader1 = {
                "No", "Kelompok Kerja Sama", "Jenis Naskah Kerja Sama", "Instansi Terkait", "Mitra Kerja Sama",
                "Judul Naskah Kerja Sama", "Nomor Naskah Kerja Sama", "Jangka Waktu Pelaksanaan", "Tanggal Penetapan",
                "Akhir Kerja Sama",
                "Urusan Pemerintahan Yang Dikerjasamakan", "Pembiayaan", "Rumpun OPD"
        };

        // String[] kptHeader1 = {
        // "No", "Kelompok Kerja Sama", "Jenis Naskah Kerja Sama", "Instansi Terkait",
        // "Mitra Kerja Sama",
        // "Judul Naskah Kerja Sama", "Jangka Waktu Pelaksanaan", "Nomor Naskah Kerja
        // Sama", "Tanggal Penetapan",
        // "Akhir Kerja Sama",
        // "Urusan Pemerintahan Yang Dikerjasamakan", "Pembiayaan", "Link Dokumen
        // Perjanjian (klik untuk melihat)",
        // "Link Dokumen Perjanjian (klik untuk melihat ) *Asli",
        // "Rumpun OPD", "Keterangan (Tanggal Update)" };

        String[] kptHeader2 = {
                "", "", "Bagian Kerja Sama Pihak Ketiga Tahun 2023", "", "", "", "", "", "", ""
        };

        String[] kptHeader3 = {
                "Badan Usaha", "", "", "", "", "", "", "", "", ""
        };

        String[] kptHeader4 = {
                "Yayasan", "", "", "", "", "", "", "", "", ""
        };

        String[] kptHeader5 = {
                "Perguruan Tinggi", "", "", "", "", "", "", "", "", ""
        };

        int kdn2023limit = 10;
        String[] kdn2023Header1 = {
                "", "", "", "", "", "Bagian Kerja Sama Dalam Negeri Tahun 2023", "", "", "", ""
        };

        String[] kdn2023Header2 = {
                "Nomor", "Kelompok Kerja Sama", "Jenis Naskah Kerja Sama", "Instansi Terkait", "Mitra Kerja Sama",
                "Judul Naskah Kerja Sama", "Nomor Naskah Kerja Sama", "Jangka Waktu Pelaksanaan",
                "Tanggal Penetapan", "Akhir Kerja Sama"
        };

        String[] kdn2023Header3 = {
                "Sinergisitas", "", "", "", "", "", "", "", "", ""
        };

        String[] kdn2023Header4 = {
                "Kerja Sama dengan Daerah", "", "", "", "", "", "", "", "", ""
        };

        String[] kdn2023Header5 = {
                "Kerja Sama Pinjam Pakai dan Bentuk Perjanjian dengan Instansi Lain", "", "", "", "", "", "", "", "", ""
        };

        if (isHeaderRow(row, kptHeader1, limit)) {
            return true;
        }

        if (isHeaderRow(row, kptHeader2, limit)) {
            return true;
        }
        if (isHeaderRow(row, kptHeader3, limit)) {
            return true;
        }
        if (isHeaderRow(row, kptHeader4, limit)) {
            return true;
        }
        if (isHeaderRow(row, kptHeader5, limit)) {
            return true;
        }
        if (isHeaderRow(row, kdn2023Header1, kdn2023limit)) {
            return true;
        }
        if (isHeaderRow(row, kdn2023Header2, kdn2023limit)) {
            return true;
        }
        if (isHeaderRow(row, kdn2023Header3, kdn2023limit)) {
            return true;
        }
        if (isHeaderRow(row, kdn2023Header4, kdn2023limit)) {
            return true;
        }
        if (isHeaderRow(row, kdn2023Header5, kdn2023limit)) {
            return true;
        }

        // ************************************** End of Header 2023

        // ************************************** Start of Header 2021
        int limit2 = 9;

        String[] kpt2021Header1 = {
                "", "", "", "", "Bagian Kerja Sama Pihak Ketiga Tahun 2021", "",
                "", "", "", "", ""
        };

        String[] kpt2021Header2 = {
                "No.", "Kelompok Kerja Sama", "Instansi Terkait", "Mitra Kerja Sama", "Jenis Naskah Kerja Sama",
                "Judul Naskah Kerja", "	Nomor Naskah Kerja Sama",
                "Jangka Waktu Pelaksanaan", "Tanggal Penetapan", "Akhir Kerja Sama"
        };

        String[] kdn2021Header1 = {
                "", "", "", "", "Bagian Kerja Sama Dalam Negeri Tahun 2021", "", "", "", "", ""
        };

        String[] kdn2021Header2 = {
                "No.", "Kelompok Kerja Sama", "Instansi Terkait", "Mitra Kerja Sama", "Jenis Naskah Kerja Sama",
                "Judul Naskah Kerja Sama", "Nomor Naskah Kerja Sama", "Jangka Waktu Pelaksanaan", "Tanggal Penetapan",
                "Akhir Kerja Sama"

        };

        int npbklimit = 6;
        String[] npbk2021Header1 = {
                "", "", "KDN - Naskah Perjanjian Bantuan Keuangan Tahun 2021", "", "", "", "", "", "", ""
        };

        String[] npbk2021Header2 = {
                "No.", "Mitra Kerja Sama", "Judul Naskah Kerja Sama", "Tanggal Penetapan", "Jenis Naskah Kerja Sama",
                "Jangka Waktu Pelaksanaan"
        };

        if (isHeaderRow(row, kpt2021Header1, limit2)) {
            return true;
        }

        if (isHeaderRow(row, kpt2021Header2, limit2)) {
            return true;
        }
        if (isHeaderRow(row, kdn2021Header1, limit2)) {
            return true;
        }
        if (isHeaderRow(row, kdn2021Header2, limit2)) {
            return true;
        }

        if (isHeaderRow(row, npbk2021Header1, npbklimit)) {
            return true;
        }

        if (isHeaderRow(row, npbk2021Header2, npbklimit)) {
            return true;
        }

        // ************************************** End of Header 2021

        // Start of Header 2023
        int limit2023 = 10;

        String[] kpt2023Header1 = {
                "", "", "Bagian Kerja Sama Pihak Ketiga Tahun 2023", "", "", "", "", "", "", ""
        };

        String[] kpt2023Header2 = {
                "No", "Kelompok Kerja Sama", "Jenis Naskah Kerja Sama", "Instansi Terkait", "Mitra Kerja Sama",
                "Judul Naskah Kerja Sama", "Jangka Waktu Pelaksanaan", "Nomor Naskah Kerja Sama", "Tanggal Penetapan",
                "Akhir Kerja Sama"
        };

        if (isHeaderRow(row, kpt2023Header1, limit2023)) {
            return true;
        }

        if (isHeaderRow(row, kpt2023Header2, limit2023)) {
            return true;
        }

        // End of Header 2023


        // Start of header 2020

        int limit2020 = 10;
        String[] kpt2020Header1 = {
            "", "", "", "", "", "Bagian Kerja Sama Pihak Ketiga Tahun 2020", "", "", "", "", ""
        };

        String[] kpt2020Header2 = {
            "No.", "Kelompok Kerja Sama", "Instansi Terkait (Dengan Penandatangan)", "Instansi Terkait",  "Mitra Kerja Sama",
            "Jenis Naskah Kerja Sama", "Judul Naskah Kerja Sama", "Nomor Naskah Kerja Sama", "Jangka Waktu Pelaksanaan",	"Tanggal Penetapan"
        };
        
        String[] kpt2020Header3 = {
            "", "Badan Usaha", "", "", "", "", "", "", "", ""
        };
        String[] kpt2020Header4 = {
            "", "Lembaga Lain", "", "", "", "", "", "", "", ""
        };
        String[] kpt2020Header5 = {
            "", "Perguruan Tinggi", "", "", "", "", "", "", "", ""
        };
        String[] kpt2020Header6 = {
            "", "Perseorangan", "", "", "", "", "", "", "", ""
        };
        String[] kpt2020Header7 = {
            "", "Yayasan", "", "", "", "", "", "", "", ""
        };


        String[] kdn2020Header1 = {
            "", "Sinergisitas", "", "", "", "", "", "", "", ""
        };
        String[] kdn2020Header2 = {
            "", "Kerja Sama Dengan Daerah Lain", "", "", "", "", "", "", "", ""
        };



        if (isHeaderRow(row, kpt2020Header1, limit2020) || 
            isHeaderRow(row, kpt2020Header2, limit2020) ||
            isHeaderRow(row, kpt2020Header3, limit2020) ||
            isHeaderRow(row, kpt2020Header4, limit2020) ||
            isHeaderRow(row, kpt2020Header5, limit2020) ||
            isHeaderRow(row, kpt2020Header6, limit2020) ||
            isHeaderRow(row, kpt2020Header7, limit2020) ||

            isHeaderRow(row, kdn2020Header1, limit2020) ||
            isHeaderRow(row, kdn2020Header2, limit2020)
            ) {
            return true;
        }


        // End of header 2020


        return result;
    }

    public void testCheckRekap2023(String filename, String reportFilename) throws IOException, InvalidFormatException {

        int tahun = 2023;
        File file = new File(filename);

        File report = new File(reportFilename);
        if (!report.exists()) {
            report.createNewFile();
        }

        FileWriter fw = new FileWriter(report.getAbsoluteFile(), false); // override existing file
        BufferedWriter bw = new BufferedWriter(fw);

        XSSFWorkbook workbook = new XSSFWorkbook(file);

        // Testing KPT
        XSSFSheet sheet = workbook.getSheetAt(2); // KPT at sheet 2
        testCheckKPT2023(sheet, bw);

        // Testing KDN
        sheet = workbook.getSheetAt(3);
        testCheckKDN2023(sheet, bw);

        workbook.close();
        bw.close();

    }

    public void testCheckKPT2023(Sheet sheet, BufferedWriter bw) throws IOException {
        bw.write("Testing KPT 2023");
        bw.newLine();
        bw.write("================");
        bw.newLine();

        CheckSheet(sheet, bw, 6);
    }

    public void testCheckKDN2023(Sheet sheet, BufferedWriter bw) throws IOException {
        bw.write("Testing KDN 2023");
        bw.newLine();
        bw.write("================");
        bw.newLine();

        CheckSheet(sheet, bw, 6);
    }

    /**
     * 
     * @param filename
     * @param reportFilename
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void testCheckRekap2021(String filename, String reportFilename) throws IOException, InvalidFormatException {

        int tahun = 2021;
        File file = new File(filename);

        File report = new File(reportFilename);
        if (!report.exists()) {
            report.createNewFile();
        }

        FileWriter fw = new FileWriter(report.getAbsoluteFile(), false); // override existing file
        BufferedWriter bw = new BufferedWriter(fw);

        XSSFWorkbook workbook = new XSSFWorkbook(file);

        // Testing KPT
        XSSFSheet sheet = workbook.getSheetAt(2); // KPT at sheet 1
        testCheckKPT2021(sheet, bw);

        // Testing KDN
        sheet = workbook.getSheetAt(3);
        testCheckKDN2021(sheet, bw);

        workbook.close();
        bw.close();

    }

    public void testCheckKPT2021(Sheet sheet, BufferedWriter bw) throws IOException {
        bw.write("Testing KPT 2021");
        bw.newLine();
        bw.write("================");
        bw.newLine();

        CheckSheet(sheet, bw, 6);
    }

    public void testCheckKDN2021(Sheet sheet, BufferedWriter bw) throws IOException {
        bw.write("Testing KDN 2021");
        bw.newLine();
        bw.write("================");
        bw.newLine();

        CheckSheet(sheet, bw, 6);
    }

    /**
     * 
     * @param filename
     * @param reportFilename
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void testCheckRekap2022(String filename, String reportFilename) throws IOException, InvalidFormatException {

        int tahun = 2022;
        File file = new File(filename);

        File report = new File(reportFilename);
        if (!report.exists()) {
            report.createNewFile();
        }

        FileWriter fw = new FileWriter(report.getAbsoluteFile(), false); // override existing file
        BufferedWriter bw = new BufferedWriter(fw);

        XSSFWorkbook workbook = new XSSFWorkbook(file);

        // Testing KPT
        XSSFSheet sheet = workbook.getSheetAt(1); // KPT at sheet 1
        testCheckKPT2022(sheet, bw);

        // Testing KDN
        sheet = workbook.getSheetAt(2);
        testCheckKDN2022(sheet, bw);

        workbook.close();
        bw.close();

    }

    public void testCheckKPT2022(Sheet sheet, BufferedWriter bw) throws IOException {
        bw.write("Testing KPT 2022");
        bw.newLine();
        bw.write("================");
        bw.newLine();

        CheckSheet(sheet, bw, 6);
    }

    public void testCheckKDN2022(Sheet sheet, BufferedWriter bw) throws IOException {
        bw.write("Testing KDN 2022");
        bw.newLine();
        bw.write("================");
        bw.newLine();

        CheckSheet(sheet, bw, 6);
    }

    public void CheckSheet(Sheet sheet, BufferedWriter bw, int noDokumenColumn) throws IOException {

        int found = 0;
        int not_found = 0;
        String msg;

        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            logger.info("Reading row number : [{}]", rowNum);
            Row row = sheet.getRow(rowNum);
            if (!(isRowToSkip(row))) {

                String nomor = row.getCell(noDokumenColumn).getStringCellValue();
                String trimmedSearchTerm = nomor.trim().replaceAll("\\s", "");

                List<DokumenKerjaSama> dokumenKerjaSama = dokumenKerjasamaRepository
                        .findByNomorDokumen(trimmedSearchTerm);

                if ((dokumenKerjaSama != null) && (dokumenKerjaSama.size() > 0)) {
                    msg = "Row :" + rowNum + " found " + "[" + trimmedSearchTerm + "]";

                    System.out.println(msg);
                    bw.write(msg);
                    bw.newLine();

                    for (DokumenKerjaSama dok : dokumenKerjaSama) {
                        System.out.println(dok.getLinkDokumenKerjaSama());

                    }
                    found = found + 1;
                } else {
                    msg = "Row:" + rowNum + " missing " + "[" + trimmedSearchTerm + "]";
                    System.out.println(msg);
                    bw.write(msg);
                    bw.newLine();

                    not_found = not_found + 1;
                }
            }
        }

        msg = "Found: " + String.valueOf(found);
        System.out.println(msg);
        bw.write(msg);
        bw.newLine();

        msg = "Not Found: " + String.valueOf(not_found);
        System.out.println(msg);
        bw.write(msg);
        bw.newLine();

    }

    public void updateRekap2021(String filename, String reportFilename) throws IOException, InvalidFormatException {

        int tahun = 2021;
        File file = new File(filename);

        File report = new File(reportFilename);
        if (!report.exists()) {
            report.createNewFile();
        }

        FileWriter fw = new FileWriter(report.getAbsoluteFile(), false); // override existing file
        BufferedWriter bw = new BufferedWriter(fw);

        // KPT
        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();

        int jenisMitraIdx = 1;
        int opdIdx = 2;
        int mitraKerjaSamaIdx = 3;
        int judulIdx = 5;
        int noDokumenIdx = 6;
        int tglPenetapanIdx = 8;
        int waktuPelaksanaanBerakhirIdx = -1;
        int urusanIdx = 10;
        int jenisNaskahIdx = 4;
        int jangkaWaktuIdx = 7;
        int pembiayaanIdx = 11;
        int docLinkIdx = 14;
        int rumpunOpdIdx = 12;
        int keteranganIdx = 15;

        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(2); // KPT at sheet 2

        int found = 0;
        int not_found = 0;
        String msg;

        bw.newLine();
        bw.newLine();
        bw.write("Updating KPT");
        bw.newLine();

        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {

            XSSFRow row = sheet.getRow(rowNum);
            if (!(isRowToSkip(row))) {

                String nomor = row.getCell(6).getStringCellValue();
                String trimmedSearchTerm = nomor.trim().replaceAll("\\s", "");
                bw.write("[" + trimmedSearchTerm + "]");
                bw.newLine();

                if (trimmedSearchTerm != null && trimmedSearchTerm.length() > 0) { // skipping empty nomor dokumen

                    List<DokumenKerjaSama> dokumenKerjaSamaList = dokumenKerjasamaRepository
                            .findByNomorDokumen(trimmedSearchTerm);

                    if ((dokumenKerjaSamaList != null) && (dokumenKerjaSamaList.size() > 0)) {
                        msg = "Row :" + rowNum + " found " + "[" + trimmedSearchTerm + "]";

                        bw.write(msg);
                        bw.newLine();

                        DokumenKerjaSama dokumenKerjaSama = dokumenKerjaSamaList.get(0);

                        // Updating dokumen
                        dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagianKPT, dokumenKerjaSama,
                                row,
                                jenisMitraIdx, opdIdx, mitraKerjaSamaIdx, judulIdx, noDokumenIdx,
                                tglPenetapanIdx,
                                waktuPelaksanaanBerakhirIdx, urusanIdx,
                                jenisNaskahIdx, jangkaWaktuIdx, pembiayaanIdx, docLinkIdx, rumpunOpdIdx,
                                keteranganIdx);

                        dokumenKerjasamaRepository.save(dokumenKerjaSama);

                        found = found + 1;
                    } else {
                        msg = "Row:" + rowNum + " missing " + "[" + trimmedSearchTerm + "]";
                        bw.write(msg);
                        bw.newLine();

                        not_found = not_found + 1;
                    }
                }
            }
        }

        // KDN
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        jenisMitraIdx = 1;
        opdIdx = 2;
        mitraKerjaSamaIdx = 3;
        judulIdx = 5;
        noDokumenIdx = 6;
        tglPenetapanIdx = 8;
        waktuPelaksanaanBerakhirIdx = -1;
        urusanIdx = 10;
        jenisNaskahIdx = 4;
        jangkaWaktuIdx = 7;
        pembiayaanIdx = 11;
        docLinkIdx = 14;
        rumpunOpdIdx = 12;
        keteranganIdx = 15;

        // sheet = workbook.getSheetAt(3); // KDN at sheet 3

        // found = 0;
        // not_found = 0;
        // msg = "";

        // bw.newLine();
        // bw.newLine();
        // bw.write("Updating KDN");
        // bw.newLine();

        // for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {

        // XSSFRow row = sheet.getRow(rowNum);
        // if (!(isRowToSkip(row))) {

        // String nomor = row.getCell(6).getStringCellValue();
        // String trimmedSearchTerm = nomor.trim().replaceAll("\\s", "");
        // bw.write("[" + trimmedSearchTerm + "]");
        // bw.newLine();

        // if (trimmedSearchTerm != null && trimmedSearchTerm.length() > 0) { //
        // skipping empty nomor dokumen

        // List<DokumenKerjaSama> dokumenKerjaSamaList = dokumenKerjasamaRepository
        // .findByNomorDokumen(trimmedSearchTerm);

        // if ((dokumenKerjaSamaList != null) && (dokumenKerjaSamaList.size() > 0)) {
        // msg = "Row :" + rowNum + " found " + "[" + trimmedSearchTerm + "]";

        // bw.write(msg);
        // bw.newLine();

        // DokumenKerjaSama dokumenKerjaSama = dokumenKerjaSamaList.get(0);

        // // Updating dokumen
        // dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagianKDN, dokumenKerjaSama,
        // row,
        // jenisMitraIdx, opdIdx, mitraKerjaSamaIdx, judulIdx, noDokumenIdx,
        // tglPenetapanIdx,
        // waktuPelaksanaanBerakhirIdx, urusanIdx,
        // jenisNaskahIdx, jangkaWaktuIdx, pembiayaanIdx, docLinkIdx, rumpunOpdIdx,
        // keteranganIdx);

        // dokumenKerjasamaRepository.save(dokumenKerjaSama);

        // found = found + 1;
        // } else {
        // msg = "Row:" + rowNum + " missing " + "[" + trimmedSearchTerm + "]";
        // bw.write(msg);
        // bw.newLine();

        // not_found = not_found + 1;
        // }
        // }
        // }
        // }

        workbook.close();

        msg = "Found: " + String.valueOf(found);
        System.out.println(msg);
        bw.write(msg);
        bw.newLine();

        msg = "Not Found: " + String.valueOf(not_found);
        System.out.println(msg);
        bw.write(msg);
        bw.newLine();

        bw.close();
    }

    public void updateRekap2022(String filename, String reportFilename) throws IOException, InvalidFormatException {

        int tahun = 2022;
        File file = new File(filename);

        File report = new File(reportFilename);
        if (!report.exists()) {
            report.createNewFile();
        }

        FileWriter fw = new FileWriter(report.getAbsoluteFile(), false); // override existing file
        BufferedWriter bw = new BufferedWriter(fw);

        // KPT
        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();

        RekapExcelDefinition KPTDef2022 = RekapExcelDefinition.getRekapDef(2022, "KPT");

        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(KPTDef2022.getSheetNo());

        int found = 0;
        int not_found = 0;
        String msg;

        bw.newLine();
        bw.newLine();
        bw.write("Updating KPT");
        bw.newLine();

        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {

            XSSFRow row = sheet.getRow(rowNum);
            if (!(isRowToSkip(row))) {

                String nomor = row.getCell(KPTDef2022.getNoDokumenIdx()).getStringCellValue();
                String trimmedSearchTerm = nomor.trim().replaceAll("\\s", "");
                bw.write("[" + trimmedSearchTerm + "]");
                bw.newLine();

                if (trimmedSearchTerm != null && trimmedSearchTerm.length() > 0) { // skipping empty nomor dokumen

                    List<DokumenKerjaSama> dokumenKerjaSamaList = dokumenKerjasamaRepository
                            .findByNomorDokumen(trimmedSearchTerm);

                    if ((dokumenKerjaSamaList != null) && (dokumenKerjaSamaList.size() > 0)) {
                        msg = "Row :" + rowNum + " found " + "[" + trimmedSearchTerm + "]";

                        bw.write(msg);
                        bw.newLine();

                        DokumenKerjaSama dokumenKerjaSama = dokumenKerjaSamaList.get(0);

                        // Updating dokumen
                        dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagianKPT, dokumenKerjaSama, row,
                                KPTDef2022.getJenisMitraIdx(),
                                KPTDef2022.getOpdIdx(),
                                KPTDef2022.getMitraKerjaSamaIdx(),
                                KPTDef2022.getJudulIdx(),
                                KPTDef2022.getNoDokumenIdx(),
                                KPTDef2022.getTglPenetapanIdx(),
                                KPTDef2022.getWaktuPelaksanaanBerakhirIdx(),
                                KPTDef2022.getUrusanIdx(),
                                KPTDef2022.getJenisNaskahIdx(),
                                KPTDef2022.getJangkaWaktuIdx(),
                                KPTDef2022.getPembiayaanIdx(),
                                KPTDef2022.getDocLinkIdx(),
                                KPTDef2022.getRumpunOpdIdx(),
                                KPTDef2022.getKeteranganIdx());

                        dokumenKerjasamaRepository.save(dokumenKerjaSama);

                        found = found + 1;
                    } else {
                        msg = "Row:" + rowNum + " missing " + "[" + trimmedSearchTerm + "]";
                        bw.write(msg);
                        bw.newLine();

                        not_found = not_found + 1;
                    }
                }
            }
        }

        // KDN
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        RekapExcelDefinition KDNDef2022 = RekapExcelDefinition.getRekapDef(2022, "KDN");

        sheet = workbook.getSheetAt(KDNDef2022.getSheetNo()); // KDN at sheet 2

        found = 0;
        not_found = 0;
        msg = "";

        bw.newLine();
        bw.newLine();
        bw.write("Updating KDN");
        bw.newLine();

        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {

            XSSFRow row = sheet.getRow(rowNum);
            if (!(isRowToSkip(row))) {

                String nomor = row.getCell(KDNDef2022.getNoDokumenIdx()).getStringCellValue();
                String trimmedSearchTerm = nomor.trim().replaceAll("\\s", "");
                bw.write("[" + trimmedSearchTerm + "]");
                bw.newLine();

                if (trimmedSearchTerm != null && trimmedSearchTerm.length() > 0) { // skipping empty nomor dokumen

                    List<DokumenKerjaSama> dokumenKerjaSamaList = dokumenKerjasamaRepository
                            .findByNomorDokumen(trimmedSearchTerm);

                    if ((dokumenKerjaSamaList != null) && (dokumenKerjaSamaList.size() > 0)) {
                        msg = "Row :" + rowNum + " found " + "[" + trimmedSearchTerm + "]";

                        bw.write(msg);
                        bw.newLine();

                        DokumenKerjaSama dokumenKerjaSama = dokumenKerjaSamaList.get(0);

                        // Updating dokumen
                        dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagianKDN, dokumenKerjaSama, row,
                                KDNDef2022.getJenisMitraIdx(),
                                KDNDef2022.getOpdIdx(),
                                KDNDef2022.getMitraKerjaSamaIdx(),
                                KDNDef2022.getJudulIdx(),
                                KDNDef2022.getNoDokumenIdx(),
                                KDNDef2022.getTglPenetapanIdx(),
                                KDNDef2022.getWaktuPelaksanaanBerakhirIdx(),
                                KDNDef2022.getUrusanIdx(),
                                KDNDef2022.getJenisNaskahIdx(),
                                KDNDef2022.getJangkaWaktuIdx(),
                                KPTDef2022.getPembiayaanIdx(),
                                KDNDef2022.getDocLinkIdx(),
                                KDNDef2022.getRumpunOpdIdx(),
                                KDNDef2022.getKeteranganIdx());

                        dokumenKerjasamaRepository.save(dokumenKerjaSama);

                        found = found + 1;
                    } else {
                        msg = "Row:" + rowNum + " missing " + "[" + trimmedSearchTerm + "]";
                        bw.write(msg);
                        bw.newLine();

                        not_found = not_found + 1;
                    }
                }
            }
        }

        workbook.close();

        msg = "Found: " + String.valueOf(found);
        System.out.println(msg);
        bw.write(msg);
        bw.newLine();

        msg = "Not Found: " + String.valueOf(not_found);
        System.out.println(msg);
        bw.write(msg);
        bw.newLine();

        bw.close();
    }


    /**
     *  Consolidate updateRekapXXXX into one 
     * 
     *  Status : Work in progress, to be tested.
     * 
     * @param tahun
     * @param reportFilename
     */
    public void updateRekap(int tahun, String folder, String reportFilename) throws IOException, InvalidFormatException {

        File file = new File(folder + "/" + RekapExcelDefinition.REKAP_KSD_2020);

        File report = new File(folder + "/" + reportFilename);
        if (!report.exists()) {
            report.createNewFile();
        }

        FileWriter fw = new FileWriter(report.getAbsoluteFile(), false); // override existing file
        BufferedWriter bw = new BufferedWriter(fw);

        // KPT
        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();

        // KDN
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        RekapExcelDefinition KPTDef = RekapExcelDefinition.getRekapDef(tahun, "KPT");
        RekapExcelDefinition KDNDef = RekapExcelDefinition.getRekapDef(tahun, "KDN");

        XSSFWorkbook    workbook = new XSSFWorkbook(file);
        XSSFSheet       sheetKPT = workbook.getSheetAt(KPTDef.getSheetNo());
        XSSFSheet       sheetKDN = workbook.getSheetAt(KDNDef.getSheetNo());

        //updateWorkSheet(tahun, sheetKPT, bagianKPT, KPTDef, bw);
        //updateWorkSheet(tahun, sheetKDN, bagianKDN, KDNDef, bw);

        workbook.close();

        bw.close();

    }

    private void updateWorkSheet(int tahun, XSSFSheet sheet, Bagian bagian, RekapExcelDefinition rekapDef, BufferedWriter bw) throws IOException, InvalidFormatException {

        int found = 0;
        int not_found = 0;
        String msg;

        bw.newLine();
        bw.newLine();
        bw.write("Updating " + bagian.getSingkatan());
        bw.newLine();

        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {

            XSSFRow row = sheet.getRow(rowNum);
            if (!(isRowToSkip(row))) {

                String nomor = row.getCell(rekapDef.getNoDokumenIdx()).getStringCellValue();
                String trimmedSearchTerm = nomor.trim().replaceAll("\\s", "");
                bw.write("[" + trimmedSearchTerm + "]");
                bw.newLine();

                if (trimmedSearchTerm != null && trimmedSearchTerm.length() > 0) { // skipping empty nomor dokumen

                    List<DokumenKerjaSama> dokumenKerjaSamaList = dokumenKerjasamaRepository
                            .findByNomorDokumen(trimmedSearchTerm);

                    if ((dokumenKerjaSamaList != null) && (dokumenKerjaSamaList.size() > 0)) {
                        msg = "Row :" + rowNum + " found " + "[" + trimmedSearchTerm + "]";

                        bw.write(msg);
                        bw.newLine();

                        DokumenKerjaSama dokumenKerjaSama = dokumenKerjaSamaList.get(0);

                        // Updating dokumen
                        dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagian, dokumenKerjaSama, row,
                                                rekapDef.getJenisMitraIdx(),
                                                rekapDef.getOpdIdx(),
                                                rekapDef.getMitraKerjaSamaIdx(),
                                                rekapDef.getJudulIdx(),
                                                rekapDef.getNoDokumenIdx(),
                                                rekapDef.getTglPenetapanIdx(),
                                                rekapDef.getWaktuPelaksanaanBerakhirIdx(),
                                                rekapDef.getUrusanIdx(),
                                                rekapDef.getJenisNaskahIdx(),
                                                rekapDef.getJangkaWaktuIdx(),
                                                rekapDef.getPembiayaanIdx(),
                                                rekapDef.getDocLinkIdx(),
                                                rekapDef.getRumpunOpdIdx(),
                                                rekapDef.getKeteranganIdx());

                        dokumenKerjasamaRepository.save(dokumenKerjaSama);

                        found = found + 1;
                    } else {
                        msg = "Row:" + rowNum + " missing " + "[" + trimmedSearchTerm + "]";
                        bw.write(msg);
                        bw.newLine();

                        not_found = not_found + 1;
                    }
                }
            }
        }


        msg = "Found: " + String.valueOf(found);
        System.out.println(msg);
        bw.write(msg);
        bw.newLine();

        msg = "Not Found: " + String.valueOf(not_found);
        System.out.println(msg);
        bw.write(msg);
        bw.newLine();


        //bw.close();

    }

    

    public void updateRekap2023(String filename, String reportFilename) throws IOException, InvalidFormatException {

        int tahun = 2023;
        File file = new File(filename);

        File report = new File(reportFilename);
        if (!report.exists()) {
            report.createNewFile();
        }

        FileWriter fw = new FileWriter(report.getAbsoluteFile(), false); // override existing file
        BufferedWriter bw = new BufferedWriter(fw);

        // KPT
        Bagian bagianKPT = bagianService.findBySingkatan("KPT").get();

        RekapExcelDefinition KPTDef2023 = RekapExcelDefinition.getRekapDef(2023, "KPT");

        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(KPTDef2023.getSheetNo());

        int found = 0;
        int not_found = 0;
        String msg;

        bw.newLine();
        bw.newLine();
        bw.write("Updating KPT");
        bw.newLine();

        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {

            XSSFRow row = sheet.getRow(rowNum);
            if (!(isRowToSkip(row))) {

                String nomor = row.getCell(6).getStringCellValue();
                String trimmedSearchTerm = nomor.trim().replaceAll("\\s", "");
                bw.write("[" + trimmedSearchTerm + "]");
                bw.newLine();

                if (trimmedSearchTerm != null && trimmedSearchTerm.length() > 0) { // skipping empty nomor dokumen

                    List<DokumenKerjaSama> dokumenKerjaSamaList = dokumenKerjasamaRepository
                            .findByNomorDokumen(trimmedSearchTerm);

                    if ((dokumenKerjaSamaList != null) && (dokumenKerjaSamaList.size() > 0)) {
                        msg = "Row :" + rowNum + " found " + "[" + trimmedSearchTerm + "]";

                        bw.write(msg);
                        bw.newLine();

                        DokumenKerjaSama dokumenKerjaSama = dokumenKerjaSamaList.get(0);

                        // Updating dokumen
                        dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagianKPT, dokumenKerjaSama, row,
                                KPTDef2023.getJenisMitraIdx(),
                                KPTDef2023.getOpdIdx(),
                                KPTDef2023.getMitraKerjaSamaIdx(),
                                KPTDef2023.getJudulIdx(),
                                KPTDef2023.getNoDokumenIdx(),
                                KPTDef2023.getTglPenetapanIdx(),
                                KPTDef2023.getWaktuPelaksanaanBerakhirIdx(),
                                KPTDef2023.getUrusanIdx(),
                                KPTDef2023.getJenisNaskahIdx(),
                                KPTDef2023.getJangkaWaktuIdx(),
                                KPTDef2023.getPembiayaanIdx(),
                                KPTDef2023.getDocLinkIdx(),
                                KPTDef2023.getRumpunOpdIdx(),
                                KPTDef2023.getKeteranganIdx());

                        dokumenKerjasamaRepository.save(dokumenKerjaSama);

                        found = found + 1;
                    } else {
                        msg = "Row:" + rowNum + " missing " + "[" + trimmedSearchTerm + "]";
                        bw.write(msg);
                        bw.newLine();

                        not_found = not_found + 1;
                    }
                }
            }
        }

        // KDN
        Bagian bagianKDN = bagianService.findBySingkatan("KDN").get();

        RekapExcelDefinition KDNDef2023 = RekapExcelDefinition.getRekapDef(2023, "KDN");

        sheet = workbook.getSheetAt(KDNDef2023.getSheetNo());

        found = 0;
        not_found = 0;
        msg = "";

        bw.newLine();
        bw.newLine();
        bw.write("Updating KDN");
        bw.newLine();

        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {

            XSSFRow row = sheet.getRow(rowNum);
            if (!(isRowToSkip(row))) {

                String nomor = row.getCell(6).getStringCellValue();
                String trimmedSearchTerm = nomor.trim().replaceAll("\\s", "");
                bw.write("[" + trimmedSearchTerm + "]");
                bw.newLine();

                if (trimmedSearchTerm != null && trimmedSearchTerm.length() > 0) { // skipping empty nomor dokumen

                    List<DokumenKerjaSama> dokumenKerjaSamaList = dokumenKerjasamaRepository
                            .findByNomorDokumen(trimmedSearchTerm);

                    if ((dokumenKerjaSamaList != null) && (dokumenKerjaSamaList.size() > 0)) {
                        msg = "Row :" + rowNum + " found " + "[" + trimmedSearchTerm + "]";

                        bw.write(msg);
                        bw.newLine();

                        DokumenKerjaSama dokumenKerjaSama = dokumenKerjaSamaList.get(0);

                        // Updating dokumen
                        dokumenKerjaSama = updateDokumenKerjaSama(tahun, bagianKDN, dokumenKerjaSama, row,
                                KDNDef2023.getJenisMitraIdx(),
                                KDNDef2023.getOpdIdx(),
                                KDNDef2023.getMitraKerjaSamaIdx(),
                                KDNDef2023.getJudulIdx(),
                                KDNDef2023.getNoDokumenIdx(),
                                KDNDef2023.getTglPenetapanIdx(),
                                KDNDef2023.getWaktuPelaksanaanBerakhirIdx(),
                                KDNDef2023.getUrusanIdx(),
                                KDNDef2023.getJenisNaskahIdx(),
                                KDNDef2023.getJangkaWaktuIdx(),
                                KDNDef2023.getPembiayaanIdx(),
                                KDNDef2023.getDocLinkIdx(),
                                KDNDef2023.getRumpunOpdIdx(),
                                KDNDef2023.getKeteranganIdx());

                        dokumenKerjasamaRepository.save(dokumenKerjaSama);

                        found = found + 1;
                    } else {
                        msg = "Row:" + rowNum + " missing " + "[" + trimmedSearchTerm + "]";
                        bw.write(msg);
                        bw.newLine();

                        not_found = not_found + 1;
                    }
                }
            }
        }

        workbook.close();

        msg = "Found: " + String.valueOf(found);
        System.out.println(msg);
        bw.write(msg);
        bw.newLine();

        msg = "Not Found: " + String.valueOf(not_found);
        System.out.println(msg);
        bw.write(msg);
        bw.newLine();

        bw.close();
    }

    /**
     * Tries to convert a string containing several bagian into list of bagian.
     * 
     * @param bagianString
     * @return
     */
    public List<Bagian> parseBagian(String bagianString) {
        List<Bagian> bagianList = bagianService.findAll();

        List<Bagian> result = new ArrayList<Bagian>();

        for (Bagian bagian : bagianList) {
            String bagianSingakatan = bagian.getSingkatan();
            if (bagianSingakatan.equals("KDN")) {
                bagianSingakatan = bagianSingakatan + " ";
            }

            if ("KDN".equals(bagianString)) {
                bagianString = bagianString + " ";
            }

            if (bagianString.contains(bagianSingakatan)) {
                result.add(bagian);
            }
        }

        return result;

    }

    private List<OPD> parseOPD(String opdString) {
        opdString = opdString.replace("Pemukiman", "Permukiman");
        opdString = opdString.replace("Koperasi Usaha", "Koperasi, Usaha");
        opdString = opdString.replace("Statik", "Statistik");
        opdString = opdString.replace("KSD", "Kerja Sama Daerah");
        opdString = opdString.replace("Dinas Pemberdayaan, Perlindungan Anak dan Pengendalian Penduduk",
                "Dinas Pemberdayaan, Perlindungan Anak, dan Pengendalian Penduduk");
        opdString = opdString.replace("Dinas Ketahanan Pangan Kelautan dan Pertanian",
                "Dinas Ketahanan Pangan, Kelautan dan Pertanian");

        List<OPD> opdList = opdService.findAll();
        List<OPD> result = new ArrayList<>();

        for (OPD opd : opdList) {
            String opdNama = opd.getNama();

            if (opdString.contains(opdNama)) {
                result.add(opd);
            }
        }

        return result;
    }

    private List<AsistenSekretarisDaerah> parseRumpunOPD(String rumpunString) {
        rumpunString = rumpunString.replace("LH", "Lingkungan Hidup");
        rumpunString = rumpunString.replace("Kesra", "Kesejahteraan Rakyat");

        List<AsistenSekretarisDaerah> asistenList = asistenSekretarisDaerahService.findAll();
        List<AsistenSekretarisDaerah> result = new ArrayList<>();

        for (AsistenSekretarisDaerah asisten : asistenList) {
            String asistenNama = asisten.getNama();

            if (rumpunString.contains(asistenNama)) {
                result.add(asisten);
            }
        }

        return result;

    }

    public void uploadKSD2024() throws IOException, InvalidFormatException {
        String filename = "[2024] Rekapitulasi Data Informasi Kerja Sama Biro KSD Tahun 2024 (Update 5 Juni 2024).xlsx";

        // uploadKPT2024(filename, 3, 22);
        // uploadKPT2024(filename, 24, 24);
        // uploadKPT2024(filename, 26, 37);
        // uploadKPT2024(filename, 39, 40);
        // uploadKolaborasi2024(filename, 6, 114);
        // uploadKDN2024(filename, 9, 13);

        // uploadKLN2024(filename);
        // updateNPBK2024(filename)
    }

    public void uploadKDN2024(String fileName, int start, int end) throws IOException, InvalidFormatException {
        int tahun = 2024;
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(2);

        for (int rowNum = start; rowNum <= end; rowNum++) {

            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                dokumenKerjaSama.setTahun(tahun);

                Bagian bagian = bagianService.findBySingkatan("KDN").get();

                dokumenKerjaSama.setBagian(bagian);

                String jenisMitraStr = row.getCell(1).getStringCellValue();
                JenisMitra jenisMitra = jenisMitraService.findByNama(jenisMitraStr).get();
                dokumenKerjaSama.setJenisMitra(jenisMitra);

                dokumenKerjaSama.setMitraKerjaSama(row.getCell(3).getStringCellValue());
                dokumenKerjaSama.setJudulDokumenPerjanjian(row.getCell(4).getStringCellValue());
                dokumenKerjaSama.setNoDokumenPerjanjian(row.getCell(5).getStringCellValue());
                dokumenKerjaSama.setTglPenetapan(row.getCell(6).getDateCellValue());
                dokumenKerjaSama.setUrusanPemerintahanYgDikerjakan(row.getCell(8).getStringCellValue());

                String jenisNaskahStr = row.getCell(9).getStringCellValue();
                if (jenisNaskahStr.equals("Adendum PKS")) {
                    jenisNaskahStr = "Adendum Perjanjian Kerja Sama";
                }

                if (jenisNaskahStr.equals("Adendum KB")) {
                    jenisNaskahStr = "Adendum Kesepakatan Bersama";
                }

                JenisNaskah jenisNaskah = jenisNaskahService.findByNama(jenisNaskahStr).get();

                logger.info("Jenis Naskah **** {}, [{}]", jenisNaskah, jenisNaskahStr);
                dokumenKerjaSama.setJenisDokumen(jenisNaskah);

                DataFormatter dataFormatter = new DataFormatter();

                dokumenKerjaSama.setJangkaWaktuPelaksanaan(dataFormatter.formatCellValue(row.getCell(10)));
                dokumenKerjaSama.setPembiayaan(row.getCell(13).getStringCellValue());

                // TODO: Deal with dokumen (multiple maybe)
                // dokumenKerjaSama.getDokumen().add(null)
                XSSFHyperlink doclink = row.getCell(11).getHyperlink();
                if (doclink != null) {
                    dokumenKerjaSama.setLinkDokumenKerjaSama(row.getCell(12).getHyperlink().getAddress());
                }

                XSSFHyperlink doclinkAsli = row.getCell(12).getHyperlink();
                if (doclinkAsli != null) {
                    dokumenKerjaSama.setKeterangan(row.getCell(12).getHyperlink().getAddress());

                }

                dokumenKerjaSama.setKeterangan3(row.getCell(14).getRawValue());

                try {
                    dokumenKerjaSama.setKeterangan3(dataFormatter.formatCellValue(row.getCell(14)));

                } catch (Exception e) {
                    // TODO: handle exception
                }

                dokumenKerjasamaRepository.save(dokumenKerjaSama);
            }
        }

        workbook.close();

    }

    public void uploadKolaborasi2024(String fileName, int start, int end) throws IOException, InvalidFormatException {
        int tahun = 2024;
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(5);

        DataFormatter dataFormatter = new DataFormatter();

        for (int rowNum = start; rowNum <= end; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                Kolaborasi kolaborasi = new Kolaborasi();

                kolaborasi.setTahun(tahun);
                kolaborasi.setMitraKerjaSama(row.getCell(1).getStringCellValue());
                kolaborasi.setNamaDanTempatKejadian(row.getCell(2).getStringCellValue());

                kolaborasi.setWaktu(dataFormatter.formatCellValue(row.getCell(3)));

                String bagianStr = row.getCell(4).getStringCellValue();
                kolaborasi.setBagian(parseBagian(bagianStr));

                kolaborasi.setKeterangan(row.getCell(5).getStringCellValue());

                kolaborasiService.save(kolaborasi);
            }
        }

        workbook.close();
    }

    public void uploadKPT2024(String fileName, int from, int to) throws IOException, InvalidFormatException {
        int tahun = 2024;
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(1);

        for (int rowNum = from; rowNum <= to; rowNum++) {

            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                DataFormatter dataFormatter = new DataFormatter();

                DokumenKerjaSama dokumenKerjaSama = new DokumenKerjaSama();

                dokumenKerjaSama.setTahun(tahun);

                Bagian bagian = bagianService.findBySingkatan("KPT").get();

                dokumenKerjaSama.setBagian(bagian);

                String jenisMitraStr = row.getCell(1).getStringCellValue();
                JenisMitra jenisMitra = jenisMitraService.findByNama(jenisMitraStr).get();
                dokumenKerjaSama.setJenisMitra(jenisMitra);

                dokumenKerjaSama.setOpd(parseOPD(row.getCell(3).getStringCellValue()));

                dokumenKerjaSama.setMitraKerjaSama(row.getCell(4).getStringCellValue());
                dokumenKerjaSama.setJudulDokumenPerjanjian(row.getCell(5).getStringCellValue());
                dokumenKerjaSama.setNoDokumenPerjanjian(row.getCell(7).getStringCellValue());

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                try {
                    dokumenKerjaSama.setTglPenetapan(sdf.parse(dataFormatter.formatCellValue(row.getCell(8))));
                    dokumenKerjaSama
                            .setWaktuPelaksanaanBerakhir(sdf.parse(dataFormatter.formatCellValue(row.getCell(9))));

                } catch (ParseException pe) {
                    pe.printStackTrace();
                }

                dokumenKerjaSama.setUrusanPemerintahanYgDikerjakan(row.getCell(10).getStringCellValue());

                String jenisNaskahStr = row.getCell(2).getStringCellValue();
                if (jenisNaskahStr.equals("Adendum PKS")) {
                    jenisNaskahStr = "Adendum Perjanjian Kerja Sama";
                }
                JenisNaskah jenisNaskah = jenisNaskahService.findByNama(jenisNaskahStr).get();

                logger.info("Jenis Naskah **** {}, [{}]", jenisNaskah, jenisNaskahStr);
                dokumenKerjaSama.setJenisDokumen(jenisNaskah);

                dokumenKerjaSama.setJangkaWaktuPelaksanaan(dataFormatter.formatCellValue(row.getCell(6)));
                dokumenKerjaSama.setPembiayaan(row.getCell(11).getStringCellValue());

                // TODO: Deal with dokumen (multiple maybe)
                // dokumenKerjaSama.getDokumen().add(null)
                XSSFHyperlink doclink = row.getCell(12).getHyperlink();
                if (doclink != null) {
                    dokumenKerjaSama.setLinkDokumenKerjaSama(row.getCell(12).getHyperlink().getAddress());
                }

                // XSSFHyperlink doclinkAsli = row.getCell(13).getHyperlink();
                // if (doclinkAsli != null) {
                // dokumenKerjaSama.setKeterangan(row.getCell(13).getHyperlink().getAddress());

                // }

                dokumenKerjaSama.setRumpun_opd(parseRumpunOPD(row.getCell(14).getStringCellValue()));

                dokumenKerjaSama.setKeterangan3(dataFormatter.formatCellValue(row.getCell(15)));

                dokumenKerjasamaRepository.save(dokumenKerjaSama);
            }
        }

        workbook.close();

    }

    /**
     * Update DokumenKerjaSama, overriding existing values.
     * Soure data taken from Exel's Row data from Rekap exel file
     * 
     * Params : integer values (0-based position in Excel worksheet. -1 to skip)
     * 
     * @param tahun
     * @param bagian
     * @param dokumenKerjaSama
     * @param row
     * @param jenisMitraIdx
     * @param opdIdx
     * @param mitraKerjaSamaIdx
     * @param judulIdx
     * @param noDokumenIdx
     * @param tglPenetapanIdx
     * @param waktuPelaksanaanBerakhirIdx
     * @param urusanIdx
     * @param jenisNaskahIdx
     * @param jangkaWaktuIdx
     * @param pembiayaanIdx
     * @param docLinkIdx
     * @param rumpunOpdIdx
     * @param keteranganIdx
     * 
     * @return updated dokumenKerjaSama
     * 
     */
    private DokumenKerjaSama updateDokumenKerjaSama(int tahun,
            Bagian bagian,
            DokumenKerjaSama dokumenKerjaSama,
            Row row,
            int jenisMitraIdx,
            int opdIdx,
            int mitraKerjaSamaIdx,
            int judulIdx,
            int noDokumenIdx,
            int tglPenetapanIdx,
            int waktuPelaksanaanBerakhirIdx,
            int urusanIdx,
            int jenisNaskahIdx,
            int jangkaWaktuIdx,
            int pembiayaanIdx,
            int docLinkIdx,
            int rumpunOpdIdx,
            int keteranganIdx) {
        DataFormatter dataFormatter = new DataFormatter();

        dokumenKerjaSama.setTahun(tahun);
        dokumenKerjaSama.setBagian(bagian);

        if (jenisMitraIdx > -1) {
            String jenisMitraStr = row.getCell(jenisMitraIdx).getStringCellValue();
            logger.info("JENIS MITRA :::[{}][{}]", jenisMitraIdx, jenisMitraStr);
            JenisMitra jenisMitra = jenisMitraService.findByNama(jenisMitraStr).get();
            dokumenKerjaSama.setJenisMitra(jenisMitra);
        }

        if (opdIdx > -1) {
            dokumenKerjaSama.setOpd(parseOPD(row.getCell(opdIdx).getStringCellValue()));
        }

        if (mitraKerjaSamaIdx > -1) {
            dokumenKerjaSama.setMitraKerjaSama(row.getCell(mitraKerjaSamaIdx).getStringCellValue());
        }

        if (judulIdx > -1) {
            dokumenKerjaSama.setJudulDokumenPerjanjian(row.getCell(judulIdx).getStringCellValue());
        }

        if (noDokumenIdx > -1) {
            dokumenKerjaSama.setNoDokumenPerjanjian(row.getCell(noDokumenIdx).getStringCellValue());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

        try {
            if (tglPenetapanIdx > -1) {
                dokumenKerjaSama
                        .setTglPenetapan(sdf.parse(dataFormatter.formatCellValue(row.getCell(tglPenetapanIdx))));
            }
        } catch (ParseException pe) {
            try {
                dokumenKerjaSama
                        .setTglPenetapan(sdf2.parse(dataFormatter.formatCellValue(row.getCell(tglPenetapanIdx))));
            } catch (ParseException pe2) {
                pe2.printStackTrace();
            }

        }

        try {
            if (waktuPelaksanaanBerakhirIdx > -1) {
                dokumenKerjaSama
                        .setWaktuPelaksanaanBerakhir(
                                sdf.parse(dataFormatter.formatCellValue(row.getCell(waktuPelaksanaanBerakhirIdx))));
            }

        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        if (urusanIdx > -1) {

            dokumenKerjaSama.setUrusanPemerintahanYgDikerjakan(row.getCell(urusanIdx).getStringCellValue());
        }

        if (jenisNaskahIdx > -1) {
            String jenisNaskahStr = row.getCell(jenisNaskahIdx).getStringCellValue();
            jenisNaskahStr = jenisNaskahStr.replace("KB", "Kesepakatan Bersama");
            jenisNaskahStr = jenisNaskahStr.replace("Addendum", "Adendum");
            logger.info("Jenis Naskah Str **** [{}]", jenisNaskahStr);
            if (jenisNaskahStr.equals("Adendum PKS")) {
                jenisNaskahStr = "Adendum Perjanjian Kerja Sama";
            }

            jenisNaskahStr = jenisNaskahStr.replace("Non - Perjanjian Kerja Sama", "Non-PKS");

            JenisNaskah jenisNaskah = jenisNaskahService.findByNama(jenisNaskahStr).get();

            logger.info("Jenis Naskah **** {}, [{}]", jenisNaskah, jenisNaskahStr);
            dokumenKerjaSama.setJenisDokumen(jenisNaskah);
        }

        if (jangkaWaktuIdx > -1) {
            dokumenKerjaSama.setJangkaWaktuPelaksanaan(dataFormatter.formatCellValue(row.getCell(jangkaWaktuIdx)));
        }

        if (pembiayaanIdx > -1) {
            dokumenKerjaSama.setPembiayaan(row.getCell(pembiayaanIdx).getStringCellValue());
        }

        // TODO: Deal with dokumen (multiple maybe)
        // dokumenKerjaSama.getDokumen().add(null)
        if (docLinkIdx > -1) {
            Hyperlink doclink = row.getCell(docLinkIdx).getHyperlink();
            if (doclink != null) {
                dokumenKerjaSama.setLinkDokumenKerjaSama(row.getCell(docLinkIdx).getHyperlink().getAddress());
            }
        }

        // XSSFHyperlink doclinkAsli = row.getCell(13).getHyperlink();
        // if (doclinkAsli != null) {
        // dokumenKerjaSama.setKeterangan(row.getCell(13).getHyperlink().getAddress());

        // }

        if (rumpunOpdIdx > -1) {
            dokumenKerjaSama.setRumpun_opd(parseRumpunOPD(row.getCell(rumpunOpdIdx).getStringCellValue()));
        }

        if (keteranganIdx > -1) {
            dokumenKerjaSama.setKeterangan3(dataFormatter.formatCellValue(row.getCell(keteranganIdx)));
        }

        return dokumenKerjaSama;

    }

    public String expandUrl2(String shortenedUrl) {
        try {

            URL url = new URL(shortenedUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setInstanceFollowRedirects(false);

            String redirectUrl;
            while (true) {
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode >= 300 && responseCode < 400) {
                    redirectUrl = httpURLConnection.getHeaderField("Location");
                    System.out.println("Redirect:[" + redirectUrl + "]");
                    url = new URL(redirectUrl);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setInstanceFollowRedirects(false);
                } else {
                    break;
                }
            }

            return url.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unable to expand URL...";
    }

}
