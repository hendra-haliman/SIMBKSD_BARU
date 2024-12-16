package id.go.jakarta.bksd.dokumen.domain;

import lombok.Data;

@Data
public class Rekapitulasi {
    private int year;

    private long kptBadanUsahaPKSCount;
    private long kptBadanUsahaKBCount;
    private long kptBadanUsahaNKCount;
    private long kptBadanUsahaCount;

    private long kptPTPKSCount;
    private long kptPTKBCount;
    private long kptPTNKCount;
    private long kptPTCount;

    private long kptYayasanPKSCount;
    private long kptYayasanKBCount;
    private long kptYayasanNKCount;
    private long kptYayasanCount;

    private long kptLembagaLainPKSCount;
    private long kptLembagaLainKBCount;
    private long kptLembagaLainNKCount;
    private long kptLembagaLainCount;

    private long kptPerseoranganPKSCount;
    private long kptPerseoranganKBCount;
    private long kptPerseoranganNKCount;
    private long kptPerseoranganCount;

    private long kdnSinergiPKSCount;
    private long kdnSinergiKBCount;
    private long kdnSinergiNKCount;
    private long kdnSinergiNPCount;
    private long kdnSinergiCount;

    private long kdnDaerahPKSCount;
    private long kdnDaerahKBCount;
    private long kdnDaerahNKCount;
    private long kdnDaerahNPBKCount;
    private long kdnDaerahCount;

    private long kdnPinjamPakaiInstansiLainPKSCount;
    private long kdnPinjamPakaiInstansiLainKBCount;
    private long kdnPinjamPakaiInstansiLainNKCount;
    private long kdnPinjamPakaiInstansiLainNonPKSCount;
    private long kdnPinjamPakaiInstansiLainCount;

    private long klnNonPerikatanCount;

    private long kptTotal;
    private long kdnTotal;
    private long klnTotal;

    private long pksTotal;
    private long kbTotal;
    private long nkTotal;
    private long nonPksTotal;
    private long npbkTotal;

    private long total;

}
