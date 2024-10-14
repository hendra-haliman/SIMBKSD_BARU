package id.go.jakarta.bksd.dokumen.controller;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.TableDefinition;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.pangan.Kelompok;
import id.go.jakarta.bksd.dokumen.domain.pangan.KerjaSamaPenyediaanPangan;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.TableDefinitionService;
import id.go.jakarta.bksd.dokumen.service.pangan.KelompokService;
import id.go.jakarta.bksd.dokumen.service.pangan.KerjaSamaPenyediaanPanganService;

@Controller
@RequestMapping("/pangan")
public class PanganController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private KerjaSamaPenyediaanPanganService kerjaSamaPenyediaanPanganService;
    private TableDefinitionService definitionService;
    private PemakaiService pemakaiService;
    private KelompokService kelompokService;


    public PanganController(KerjaSamaPenyediaanPanganService kerjaSamaPenyediaanPanganService,
                            TableDefinitionService definitionService,
                            PemakaiService pemakaiService,
                            KelompokService kelompokService) {
        this.kerjaSamaPenyediaanPanganService = kerjaSamaPenyediaanPanganService;
        this.definitionService = definitionService;
        this.pemakaiService = pemakaiService;
        this.kelompokService = kelompokService;
    }

    @GetMapping("")
    public String panganHome(Model model, Principal principal) throws JsonProcessingException {
        // List<KerjaSamaPenyediaanPangan> kerjasamaList = kerjaSamaPenyediaanPanganService.findAll();
        List<Kelompok> kelompokList = kelompokService.findAll();
        Set<TableDefinition> tableDef = definitionService.findByNamaTable("pangan");

        String name = principal.getName();
        Pemakai pemakai = pemakaiService.findByUserName(name);

        List<TableDefinition> defs = pemakai.getTableDefinition().stream()
                .filter(def -> def.getNamaTable().equals("pangan")).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String jsonDefs = mapper.writeValueAsString(defs);

        logger.info("JSONFEFS:::[{}]", jsonDefs);

        // model.addAttribute("kerjasamaList", kerjasamaList);
        model.addAttribute("kelompokList", kelompokList);
        model.addAttribute("tableDef", tableDef);
        model.addAttribute("pemakai", pemakai);
        model.addAttribute("jsonDefs", jsonDefs);

        return "pangan/pangan";
    }

    @PostMapping("/list")
    @ResponseBody
    public Page<KerjaSamaPenyediaanPangan> list(@RequestBody PagingRequest pagingRequest) {
        Page<KerjaSamaPenyediaanPangan> kerjasamaPage = kerjaSamaPenyediaanPanganService.getKerjasama(pagingRequest);
        logger.info("kerjasama pangan List:::{}", kerjasamaPage);
        return kerjasamaPage;
    }
}
