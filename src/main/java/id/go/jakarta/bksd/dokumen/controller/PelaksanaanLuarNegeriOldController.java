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
@RequestMapping("/pelaksanaan_luar_negeri_old")
public class PelaksanaanLuarNegeriOldController {
    private PemakaiService pemakaiService;

    public PelaksanaanLuarNegeriOldController(PemakaiService pemakaiService) {
        this.pemakaiService = pemakaiService;
    }

    @GetMapping("")
    public String pelaksanaanLuarNegeriOldHome(Model model,
            @RequestParam(value = "mode", required = false) String mode,
            @RequestParam(value = "year", required = false) String year,
            Principal principal) throws JsonProcessingException {

        String name = principal.getName();
        Pemakai pemakai = pemakaiService.findByUserName(name);
        model.addAttribute("pemakai", pemakai);

        model.addAttribute("mode", mode);
        model.addAttribute("year", year);
        model.addAttribute("currentPage", "pelaksanaanLNOld");

        return "pelaksanaanLNOld/pelaksanaanLNOld";
    }
}
