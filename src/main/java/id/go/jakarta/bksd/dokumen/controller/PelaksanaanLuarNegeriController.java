package id.go.jakarta.bksd.dokumen.controller;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.TableDefinition;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.TableDefinitionService;

@Controller
@RequestMapping("/pelaksanaan_luar_negeri")
public class PelaksanaanLuarNegeriController {
    private TableDefinitionService tableDefinitionService;
    private PemakaiService pemakaiService;

    public PelaksanaanLuarNegeriController(TableDefinitionService tableDefinitionService,
            PemakaiService pemakaiService) {
        this.pemakaiService = pemakaiService;
        this.tableDefinitionService = tableDefinitionService;
    }

    @GetMapping("")
    public String pelaksanaanLuarNegeriHome(Model model,
            @RequestParam(value = "mode", required = false) String mode,
            @RequestParam(value = "year", required = false) String year,
            Principal principal) throws JsonProcessingException {
        Set<TableDefinition> tableDef = tableDefinitionService.findByNamaTable("kerjasamaLN");

        String name = principal.getName();
        Pemakai pemakai = pemakaiService.findByUserName(name);

        List<TableDefinition> defs = pemakai.getTableDefinition().stream()
                .filter(def -> def.getNamaTable().equals("kerjasamaLN")).collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        String jsonDefs = mapper.writeValueAsString(defs);

        // logger.info("JSONFEFS:::[{}]", jsonDefs);

        model.addAttribute("tableDef", tableDef);
        model.addAttribute("pemakai", pemakai);

        model.addAttribute("mode", mode);
        model.addAttribute("jsonDefs", jsonDefs);
        model.addAttribute("year", year);
        model.addAttribute("currentPage", "pelaksanaanLN");

        return "pelaksanaanLN/pelaksanaanLN";
    }
}
