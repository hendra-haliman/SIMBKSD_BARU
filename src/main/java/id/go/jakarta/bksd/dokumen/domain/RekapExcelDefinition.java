package id.go.jakarta.bksd.dokumen.domain;

import java.lang.reflect.Method;

import lombok.Data;

@Data
public class RekapExcelDefinition {
    public static final String REKAP_KSD_2020 = "[2020] Rekapitulasi Data Informasi Kerja Sama Biro KSD Tahun 2020 (Update 21 November 2023).xlsx";
    public static final String REKAP_KSD_2021 = "[2021] Rekapitulasi Data Informasi Kerja Sama Biro KSD Tahun 2021 (Update 21 November 2023).xlsx";
    public static final String REKAP_KSD_2023 = "[2023] Rekapitulasi Data Informasi Kerja Sama Biro KSD Tahun 2023 (Update 7 Mei 2024).xlsx";

    private int tahun;
    private int sheetNo;

    private int jenisMitraIdx;
    private int opdIdx;
    private int mitraKerjaSamaIdx;
    private int judulIdx;
    private int noDokumenIdx;
    private int tglPenetapanIdx;
    private int waktuPelaksanaanBerakhirIdx;
    private int urusanIdx;
    private int jenisNaskahIdx;
    private int jangkaWaktuIdx;
    private int pembiayaanIdx;
    private int docLinkIdx;
    private int rumpunOpdIdx;
    private int keteranganIdx;

    public static RekapExcelDefinition getRekapDef(int tahun, String bagian) {
        bagian = bagian.toUpperCase();
        RekapExcelDefinition returnObj = null;
        try {
            RekapExcelDefinition def = new RekapExcelDefinition();
            Method method = RekapExcelDefinition.class.getMethod("get" + bagian + "RekapDef" + tahun);
            returnObj = (RekapExcelDefinition) method.invoke(def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnObj;
    }

    public RekapExcelDefinition getKPTRekapDef2022() {
        RekapExcelDefinition def = new RekapExcelDefinition();
        def.tahun = 2022;
        def.sheetNo = 1;

        def.jenisMitraIdx = 1;
        def.opdIdx = 2;
        def.mitraKerjaSamaIdx = 3;
        def.judulIdx = 5;
        def.noDokumenIdx = 6;
        def.tglPenetapanIdx = 8;
        def.waktuPelaksanaanBerakhirIdx = -1;
        def.urusanIdx = 10;
        def.jenisNaskahIdx = 4;
        def.jangkaWaktuIdx = 7;
        def.pembiayaanIdx = 11;
        def.docLinkIdx = 13;
        def.rumpunOpdIdx = 12;
        def.keteranganIdx = 15;

        return def;

    }

    public RekapExcelDefinition getKDNRekapDef2022() {
        RekapExcelDefinition def = new RekapExcelDefinition();
        def.tahun = 2022;
        def.sheetNo = 2;

        def.jenisMitraIdx = 1;
        def.opdIdx = 2;
        def.mitraKerjaSamaIdx = 3;
        def.judulIdx = 5;
        def.noDokumenIdx = 6;
        def.tglPenetapanIdx = 8;
        def.waktuPelaksanaanBerakhirIdx = -1;
        def.urusanIdx = 10;
        def.jenisNaskahIdx = 4;
        def.jangkaWaktuIdx = 7;
        def.pembiayaanIdx = 11;
        def.docLinkIdx = 13;
        def.rumpunOpdIdx = 12;
        def.keteranganIdx = 15;

        return def;

    }

    public RekapExcelDefinition getKPTRekapDef2023() {
        RekapExcelDefinition def = new RekapExcelDefinition();
        def.tahun = 2023;
        def.sheetNo = 2;

        def.jenisMitraIdx = 1;
        def.opdIdx = 3;
        def.mitraKerjaSamaIdx = 4;
        def.judulIdx = 5;
        def.noDokumenIdx = 6;
        def.tglPenetapanIdx = 8;
        def.waktuPelaksanaanBerakhirIdx = 9;
        def.urusanIdx = 10;
        def.jenisNaskahIdx = 2;
        def.jangkaWaktuIdx = 7;
        def.pembiayaanIdx = 11;
        def.docLinkIdx = 13;
        def.rumpunOpdIdx = 12;
        def.keteranganIdx = 15;

        return def;

    }

    public RekapExcelDefinition getKDNRekapDef2023() {
        RekapExcelDefinition def = new RekapExcelDefinition();
        def.tahun = 2023;
        def.sheetNo = 3;

        def.jenisMitraIdx = 1;
        def.opdIdx = 3;
        def.mitraKerjaSamaIdx = 4;
        def.judulIdx = 5;
        def.noDokumenIdx = 6;
        def.tglPenetapanIdx = 8;
        def.waktuPelaksanaanBerakhirIdx = -1;
        def.urusanIdx = 10;
        def.jenisNaskahIdx = 2;
        def.jangkaWaktuIdx = 7;
        def.pembiayaanIdx = 11;
        def.docLinkIdx = 13;
        def.rumpunOpdIdx = 12;
        def.keteranganIdx = 14;

        return def;

    }

    public RekapExcelDefinition getNPBKRekapDef2021() {
        RekapExcelDefinition def = new RekapExcelDefinition();
        def.tahun = 2021;
        def.sheetNo = 4;

        def.jenisMitraIdx = -1;
        def.opdIdx = -1;
        def.mitraKerjaSamaIdx = 1;
        def.judulIdx = 2;
        def.noDokumenIdx = -1;
        def.tglPenetapanIdx = 3; // date not captured from excel row (blank).
        def.waktuPelaksanaanBerakhirIdx = -1;
        def.urusanIdx = -1;
        def.jenisNaskahIdx = -1;
        def.jangkaWaktuIdx = 5;
        def.pembiayaanIdx = -1;
        def.docLinkIdx = 6;
        def.rumpunOpdIdx = -1;
        def.keteranganIdx = -1;

        return def;

    }

    // Start of def 2020 **************************************************************
    public RekapExcelDefinition getKPTRekapDef2020() {
        RekapExcelDefinition def = new RekapExcelDefinition();
        def.tahun = 2020;
        def.sheetNo = 1;

        def.jenisMitraIdx = 1;
        def.opdIdx = 3;
        def.mitraKerjaSamaIdx = 4;
        def.judulIdx = 6;
        def.noDokumenIdx = 7;
        def.tglPenetapanIdx = 9;
        def.waktuPelaksanaanBerakhirIdx = -1;
        def.urusanIdx = 11;
        def.jenisNaskahIdx = 5;
        def.jangkaWaktuIdx = 8;
        def.pembiayaanIdx = 12;
        def.docLinkIdx = 14;
        def.rumpunOpdIdx = -1;
        def.keteranganIdx = -1;

        return def;

    }

    /**
     *  
     * @return
     */
    public RekapExcelDefinition getKDNRekapDef2020() {
        RekapExcelDefinition def = new RekapExcelDefinition();
        def.tahun = 2020;
        def.sheetNo = 2;

        def.jenisMitraIdx = 1;
        def.opdIdx = 2;
        def.mitraKerjaSamaIdx = 3;
        def.judulIdx = 5;
        def.noDokumenIdx = 6;
        def.tglPenetapanIdx = 8;
        def.waktuPelaksanaanBerakhirIdx = -1;
        def.urusanIdx = 10;
        def.jenisNaskahIdx = 4;
        def.jangkaWaktuIdx = 7;
        def.pembiayaanIdx = 11;
        def.docLinkIdx = 13;
        def.rumpunOpdIdx = -1;
        def.keteranganIdx = 14;

        return def;

    }


    // End of def 2020 ****************************************************************

}
