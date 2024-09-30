package id.go.jakarta.bksd.dokumen.controller;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.Privilege;
import id.go.jakarta.bksd.dokumen.domain.Role;
import id.go.jakarta.bksd.dokumen.service.PemakaiService;
import id.go.jakarta.bksd.dokumen.service.PrivilegeService;
import id.go.jakarta.bksd.dokumen.service.RoleService;

@Controller
@RequestMapping("/user")
public class UserController {
    private PemakaiService pemakaiService;
    private RoleService roleService;
    private PrivilegeService privilegeService;

    public UserController(PemakaiService pemakaiService, RoleService roleService, PrivilegeService privilegeService) {
        this.pemakaiService = pemakaiService;
        this.roleService = roleService;
        this.privilegeService = privilegeService;
    }

    @GetMapping("")
    public String userHome(Model model) {
        return "user/user";
    }

    @GetMapping("/view/{id}")
    public String lihatUser(@PathVariable Long id, Model model) {
        Pemakai pemakai = pemakaiService.findById(id);
        UserDetails userDetails = pemakaiService.loadUserByUsername(pemakai.getEmail());
        model.addAttribute("pemakai", pemakai);
        model.addAttribute("userDetails", userDetails);
        return "user/user-lihat";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Pemakai pemakai = pemakaiService.findById(id);
        UserDetails userDetails = pemakaiService.loadUserByUsername(pemakai.getEmail());
        List<Role> roles = roleService.getAllRoles();
        List<Privilege> privs = privilegeService.findAll();

        model.addAttribute("pemakai", pemakai);
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("roles", roles);
        model.addAttribute("privs", privs);
        return "user/user-edit";
    }

    @GetMapping("/add")
    public String showAddUser(Model model) {
        model.addAttribute("pemakai", new Pemakai());
        List<Role> roles = roleService.getAllRoles();

        model.addAttribute("roles", roles);

        return "user/user-add";
    }

    @PostMapping("saveNew")
    public String saveNewUser(Model model, @ModelAttribute("pemakai") Pemakai pemakai) {

        pemakaiService.save(pemakai);
        model.addAttribute("success_msg", "User berhasil disimpan.");
        return "user/user";
    }

    @PostMapping("saveEdit")
    public String saveEditUser(Model model,
            @ModelAttribute("pemakai") Pemakai pemakai) {

        pemakaiService.save(pemakai);
        model.addAttribute("success_msg", "User berhasil disimpan.");

        return "user/user";
    }

}
