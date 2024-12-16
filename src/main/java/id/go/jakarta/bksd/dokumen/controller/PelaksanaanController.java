package id.go.jakarta.bksd.dokumen.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import id.go.jakarta.bksd.dokumen.domain.AsistenSekretarisDaerah;
import id.go.jakarta.bksd.dokumen.domain.Bagian;
import id.go.jakarta.bksd.dokumen.domain.JenisMitra;
import id.go.jakarta.bksd.dokumen.domain.JenisNaskah;
import id.go.jakarta.bksd.dokumen.domain.OPD;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.TableDefinition;
import id.go.jakarta.bksd.dokumen.service.AsistenSekretarisDaerahService;
import id.go.jakarta.bksd.dokumen.service.BagianService;
import id.go.jakarta.bksd.dokumen.service.JenisMitraService;
import id.go.jakarta.bksd.dokumen.service.JenisNaskahService;
import id.go.jakarta.bksd.dokumen.service.OPDService;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.TableDefinitionService;

@Controller
@RequestMapping("/pelaksanaan")
public class PelaksanaanController {
    private BagianService bagianService;
    private OPDService opdService;
    private JenisMitraService jenisMitraService;
    private AsistenSekretarisDaerahService asistenSekretarisDaerahService;
    private JenisNaskahService jenisNaskahService;
    private TableDefinitionService tableDefinitionService;
    private PemakaiService pemakaiService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public PelaksanaanController(BagianService bagianService, OPDService opdService,
            JenisMitraService jenisMitraService, AsistenSekretarisDaerahService asistenSekretarisDaerahService,
            JenisNaskahService jenisNaskahService,
            TableDefinitionService tableDefinitionService,
            PemakaiService pemakaiService) {
        this.bagianService = bagianService;
        this.opdService = opdService;
        this.jenisMitraService = jenisMitraService;
        this.asistenSekretarisDaerahService = asistenSekretarisDaerahService;
        this.jenisNaskahService = jenisNaskahService;
        this.tableDefinitionService = tableDefinitionService;
        this.pemakaiService = pemakaiService;
    }

    @GetMapping("")
    public String pelaksanaanHome(Model model,
            @RequestParam(value = "mode", required = false) String mode,
            @RequestParam(value = "bagian", required = false) String bagian,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "jn", required = false) String jenisNaskah,
            @RequestParam(value = "jm", required = false) String jenisMitra,
            Principal principal) throws JsonProcessingException {

        List<Bagian> bagianList = bagianService.findAll();
        List<OPD> opdList = opdService.findAll();
        List<JenisMitra> jenisMitraList = jenisMitraService.findAll();
        List<AsistenSekretarisDaerah> asistenList = asistenSekretarisDaerahService.findAll();
        List<JenisNaskah> jenisNaskahList = jenisNaskahService.findAll();

        JenisNaskah jenisNaskahPKS = jenisNaskahService.findByNama("Perjanjian Kerja Sama").get();
        JenisNaskah jenisNaskahAdendumPKS = jenisNaskahService.findByNama("Adendum Perjanjian Kerja Sama").get();

        JenisNaskah jenisNaskahKesepakatanBersama = jenisNaskahService.findByNama("Kesepakatan Bersama").get();
        JenisNaskah jenisNaskahAdendumKesepakatanBersama = jenisNaskahService.findByNama("Adendum Kesepakatan Bersama")
                .get();
        JenisNaskah jenisNaskahNotaKesepakatan = jenisNaskahService.findByNama("Nota Kesepakatan").get();
        JenisNaskah jenisNaskahPerjanjianPinjamPakai = jenisNaskahService.findByNama("Perjanjian Pinjam Pakai").get();
        JenisNaskah jenisNaskahAdendumPerjanjianPinjamPakai = jenisNaskahService
                .findByNama("Adendum Perjanjian Pinjam Pakai").get();
        JenisNaskah jenisNaskahPerubahanPerjanjianPinjamPakai = jenisNaskahService
                .findByNama("Perubahan Perjanjian Pinjam Pakai").get();

        JenisNaskah jenisNaskahNPBK = jenisNaskahService.findByNama("Naskah Perjanjian Bantuan Keuangan").get();
        JenisNaskah jenisNaskahNonPKS = jenisNaskahService.findByNama("Non-PKS").get();
        JenisNaskah jenisNaskahNP   = jenisNaskahService.findByNama("Perjanjian Kerahasiaan").get();


        List<JenisNaskah> jenisNaskahSelectedList = new ArrayList<>();

        if ("pks".equals(jenisNaskah)) {
            jenisNaskahSelectedList.add(jenisNaskahPKS);
            jenisNaskahSelectedList.add(jenisNaskahAdendumPKS);
            jenisNaskahSelectedList.add(jenisNaskahPerjanjianPinjamPakai);
            jenisNaskahSelectedList.add(jenisNaskahAdendumPerjanjianPinjamPakai);
            jenisNaskahSelectedList.add(jenisNaskahPerubahanPerjanjianPinjamPakai);
        }

        if ("kb".equals(jenisNaskah)) {
            jenisNaskahSelectedList.add(jenisNaskahKesepakatanBersama);
            jenisNaskahSelectedList.add(jenisNaskahAdendumKesepakatanBersama);
        }

        if ("nk".equals(jenisNaskah)) {
            jenisNaskahSelectedList.add(jenisNaskahNotaKesepakatan);
        }

        if ("npbk".equals(jenisNaskah)) {
            jenisNaskahSelectedList.add(jenisNaskahNPBK);
        }

        if ("nonpks".equals(jenisNaskah)) {
            jenisNaskahSelectedList.add(jenisNaskahNonPKS);
        }

        if ("np".equals(jenisNaskah)) {
            jenisNaskahSelectedList.add(jenisNaskahNP);
        }

        JenisMitra jenisMitraBadanUsaha = jenisMitraService.findByNama("Badan Usaha").get();
        JenisMitra jenisMitraYayasan = jenisMitraService.findByNama("Yayasan").get();
        JenisMitra jenisMitraPerguruanTinggi = jenisMitraService.findByNama("Perguruan Tinggi").get();
        JenisMitra jenisMitraSinergisitas = jenisMitraService.findByNama("Sinergisitas").get();
        JenisMitra jenisMitraLembagaLain = jenisMitraService.findByNama("Lembaga Lain").get();
        JenisMitra jenisMitraPerseorangan = jenisMitraService.findByNama("Perseorangan").get();
        JenisMitra jenisMitraKerjaSamaDenganDaerah = jenisMitraService.findByNama("Kerja Sama dengan Daerah").get();
        JenisMitra jenisMitraPinjamPakai = jenisMitraService
                .findByNama("Kerja Sama Pinjam Pakai dan Bentuk Perjanjian dengan Instansi Lain").get();

        List<JenisMitra> jenisMitraSelectedList = new ArrayList<>();

        if ("bu".equals(jenisMitra)) {
            jenisMitraSelectedList.add(jenisMitraBadanUsaha);
        }

        if ("yayasan".equals(jenisMitra)) {
            jenisMitraSelectedList.add(jenisMitraYayasan);
        }

        if ("pt".equals(jenisMitra)) {
            jenisMitraSelectedList.add(jenisMitraPerguruanTinggi);
        }

        if ("sinergi".equals(jenisMitra)) {
            jenisMitraSelectedList.add(jenisMitraSinergisitas);
        }
        if ("ll".equals(jenisMitra)) {
            jenisMitraSelectedList.add(jenisMitraLembagaLain);
        }
        if ("perseorangan".equals(jenisMitra)) {
            jenisMitraSelectedList.add(jenisMitraPerseorangan);
        }
        if ("daerah".equals(jenisMitra)) {
            jenisMitraSelectedList.add(jenisMitraKerjaSamaDenganDaerah);
        }
        if ("pp".equals(jenisMitra)) {
            jenisMitraSelectedList.add(jenisMitraPinjamPakai);
        }

        Set<TableDefinition> tableDef = tableDefinitionService.findByNamaTable("kerjasama");

        String name = principal.getName();
        Pemakai pemakai = pemakaiService.findByUserName(name);

        List<TableDefinition> defs = pemakai.getTableDefinition().stream()
                .filter(def -> def.getNamaTable().equals("kerjasama")).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String jsonDefs = mapper.writeValueAsString(defs);

        logger.info("JSONFEFS:::[{}]", jsonDefs);

        model.addAttribute("bagianList", bagianList);
        model.addAttribute("opdList", opdList);
        model.addAttribute("jenisMitraList", jenisMitraList);
        model.addAttribute("jenisMitraSelectedList", jenisMitraSelectedList);
        model.addAttribute("mode", mode);
        model.addAttribute("bagian", bagian);
        model.addAttribute("asistenList", asistenList);
        model.addAttribute("jenisNaskahList", jenisNaskahList);
        model.addAttribute("jenisNaskahSelectedList", jenisNaskahSelectedList);
        model.addAttribute("tableDef", tableDef);
        model.addAttribute("pemakai", pemakai);
        model.addAttribute("year", year);

        model.addAttribute("jsonDefs", jsonDefs);

        model.addAttribute("currentPage", "pelaksanaan");

        return "pelaksanaan/pelaksanaan";
    }
}
