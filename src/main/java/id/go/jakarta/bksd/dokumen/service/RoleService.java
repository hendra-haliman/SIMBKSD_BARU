package id.go.jakarta.bksd.dokumen.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.Role;
import id.go.jakarta.bksd.dokumen.repository.RoleRepository;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    private ModelMapper modelMapper;

    public RoleService(RoleRepository roleRepository, ModelMapper mapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = mapper;
    }

    public List<Role> getAllRoles() {
        return roleRepository.getAllRoles();
        // return roles.stream()
        // .map(role -> modelMapper.map(role, RoleDTO.class))
        // .collect(Collectors.toList());

    }

    public Optional<Role> getByNama(String roleName) {
        return roleRepository.getByName(roleName);
    }
}
