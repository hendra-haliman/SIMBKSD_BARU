package id.go.jakarta.bksd.dokumen.service;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.Privilege;
import id.go.jakarta.bksd.dokumen.repository.PrivilegeRepository;

import java.util.List;

@Service
public class PrivilegeService {
    private PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    public List<Privilege> findAll() {
        return privilegeRepository.findAll();
    }
}
