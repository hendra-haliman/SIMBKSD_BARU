package id.go.jakarta.bksd.dokumen.service;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.pangan.BUMD;
import id.go.jakarta.bksd.dokumen.domain.pangan.Jenis;
import id.go.jakarta.bksd.dokumen.domain.pangan.Kelompok;
import id.go.jakarta.bksd.dokumen.domain.pangan.KerjaSamaPenyediaanPangan;
import id.go.jakarta.bksd.dokumen.domain.pangan.KotaKabupaten;
import id.go.jakarta.bksd.dokumen.domain.pangan.MitraBusiness;
import id.go.jakarta.bksd.dokumen.domain.pangan.Pemda;
import id.go.jakarta.bksd.dokumen.service.pangan.BUMDService;
import id.go.jakarta.bksd.dokumen.service.pangan.JenisService;
import id.go.jakarta.bksd.dokumen.service.pangan.KelompokService;
import id.go.jakarta.bksd.dokumen.service.pangan.KerjaSamaPenyediaanPanganService;
import id.go.jakarta.bksd.dokumen.service.pangan.KotaKabupatenService;
import id.go.jakarta.bksd.dokumen.service.pangan.MitraBusinessService;
import id.go.jakarta.bksd.dokumen.service.pangan.PemdaService;


@Service
public class BUMDPanganDataUploaderService {
    private final String fileName = "Data Kerja Sama Penyediaan Pangan Tahun 2023-2024.xlsx";
    private final String BUMD_PASAR_JAYA = "Perumda Pasar Jaya";
    private final String BUMD_FOOD_STATION = "Food Station";
    private final String BUMD_DHARMA_JAYA ="Dharma Jaya";

    private final String KELOMPOK_KERJASAMA_DGN_DAERAH = "Kerja Sama dengan Pemerintah Daerah";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private PemdaService pemdaService;
    private BUMDService bumdService;
    private MitraBusinessService mitraBusinessService;
    private KotaKabupatenService kabupatenService;
    private JenisService jenisService;
    private KerjaSamaPenyediaanPanganService kerjaSamaPenyediaanPanganService;
    private KelompokService kelompokService;


    public BUMDPanganDataUploaderService(PemdaService pemdaService,
                                        BUMDService bumdService,
                                        MitraBusinessService mitraBusinessService,
                                        KotaKabupatenService kabupatenService,
                                        JenisService jenisService,
                                        KerjaSamaPenyediaanPanganService kerjaSamaPenyediaanPanganService,
                                        KelompokService kelompokService) {
        this.pemdaService = pemdaService;
        this.bumdService = bumdService;
        this.mitraBusinessService = mitraBusinessService;
        this.kabupatenService = kabupatenService;
        this.jenisService = jenisService;
        this.kerjaSamaPenyediaanPanganService = kerjaSamaPenyediaanPanganService;
        this.kelompokService = kelompokService;
    }

    public void uploadDataPangan()  throws IOException, InvalidFormatException{
        File file = new File("D:/Biro KSD/" + fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        uploadDataPasarJaya(workbook);
        uploadDataFoodStation(workbook);

        workbook.close();
    }

    private void uploadDataPasarJaya(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheetAt(1);

        Kelompok kelompok = kelompokService.findByNama(KELOMPOK_KERJASAMA_DGN_DAERAH);
        BUMD pasarJaya = bumdService.findByNama(BUMD_PASAR_JAYA);
        uploadWorksheetData(2023, pasarJaya, kelompok, sheet, 5, 5, true);
        uploadWorksheetData(2024, pasarJaya, kelompok, sheet, 11, 15, true);
    }

    private void uploadDataFoodStation(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheetAt(2);

        Kelompok kelompok = kelompokService.findByNama(KELOMPOK_KERJASAMA_DGN_DAERAH);
        BUMD foodStation = bumdService.findByNama(BUMD_FOOD_STATION);
        uploadWorksheetData(2023, foodStation, kelompok, sheet, 5, 33, true);
        uploadWorksheetData(2024, foodStation, kelompok, sheet, 41, 66, true);
    }


    

    private void uploadWorksheetData(int year, BUMD bumd, Kelompok kelompok, XSSFSheet sheet, int from, int to, boolean includeKeterangan) {
        for (int rowNum = from; rowNum <= to; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            KerjaSamaPenyediaanPangan kerjasama = new KerjaSamaPenyediaanPangan();

            kerjasama.setTahun(year);
            kerjasama.setBumd(bumd);
            kerjasama.setKelompok(kelompok);

            Pemda pemda = pemdaService.findByNama(row.getCell(1).getStringCellValue());
            kerjasama.getPemda().add(pemda);

            MitraBusiness mitra = mitraBusinessService.findByNama(row.getCell(2).getStringCellValue());
            kerjasama.setMitraBusiness(mitra);

            KotaKabupaten kota = kabupatenService.findByNama(row.getCell(3).getStringCellValue());
            kerjasama.getKotaKabupaten().add(kota);

            Jenis jenis = jenisService.findByNama(row.getCell(4).getStringCellValue());
            kerjasama.setJenis(jenis);

            kerjasama.setKuantitas(row.getCell(5).getNumericCellValue());
            kerjasama.setNilaiTransaksi(row.getCell(6).getNumericCellValue());
            kerjasama.setKeterangan(row.getCell(7).getStringCellValue());

            kerjaSamaPenyediaanPanganService.save(kerjasama);

        }
    }
}
