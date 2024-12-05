package id.go.jakarta.bksd.dokumen.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.Role;
import id.go.jakarta.bksd.dokumen.domain.kolaborasi.KolaboratorRequest;
import id.go.jakarta.bksd.dokumen.domain.kolaborasi.StatusDaftar;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.RoleService;

@Controller
@RequestMapping("/kolaborator")
public class KolaboratorController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String ROLE_KOLABORATOR = "ROLES_KOLABORATOR";

    private PemakaiService pemakaiService;
    private Pemakai kolaborator;
    private PasswordEncoder passwordEncoder;
    private RoleService roleService;


    public KolaboratorController(PemakaiService pemakaiService, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.pemakaiService = pemakaiService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @PostMapping("/daftar")
    @ResponseBody
    public StatusDaftar daftarKolaborator(@RequestBody KolaboratorRequest kolaboratorRequet) {
        String fullName = kolaboratorRequet.getNama();
        String[] bagian = fullName.split("\\s+", 2);

        String namaDepan = bagian[0];
        String namaKeluarga = bagian.length > 1 ? bagian[1] : "";

        Pemakai pemakai = pemakaiService.findByEmail(kolaboratorRequet.getEmail());

        StatusDaftar statusDaftar = new StatusDaftar();

        if (pemakai != null) { // sudah ada user dengan email tersebut.
            statusDaftar.setStatus("Error");
            statusDaftar.setMessage("email address sudah ada di sistem.");

        } else {
            kolaborator = new Pemakai();
            kolaborator.setNamaDepan(namaDepan);
            kolaborator.setNamaKeluarga(namaKeluarga);
            kolaborator.setPassword(passwordEncoder.encode(kolaboratorRequet.getPassword()));
            kolaborator.setEmail(kolaboratorRequet.getEmail());
            kolaborator.setEnabled(true); // auto enable kolaborator

            Role kolabRole = roleService.getByNama(ROLE_KOLABORATOR).get(); 

            kolaborator.getRoles().add(kolabRole);
            pemakaiService.save(kolaborator);

            statusDaftar.setStatus("Sukses");
            statusDaftar.setMessage("Akun Kolaborator berhasil dibuat");
        
        }

        
        return statusDaftar;
    }

    @GetMapping("/dashboardKolab")
    public String dashboardKolaborator() {
        return "kolaborasi/dashboard-kolab";
    }
}
