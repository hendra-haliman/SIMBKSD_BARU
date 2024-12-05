package id.go.jakarta.bksd.dokumen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import id.go.jakarta.bksd.dokumen.domain.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("from Role")
    public List<Role> getAllRoles();

    @Query("from Role where name = :name")
    public Optional<Role> getByName(String name);
}
