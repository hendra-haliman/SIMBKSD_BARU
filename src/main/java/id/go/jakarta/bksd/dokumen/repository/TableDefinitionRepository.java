package id.go.jakarta.bksd.dokumen.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.TableDefinition;

@Repository
public interface TableDefinitionRepository extends JpaRepository<TableDefinition, Long> {

    @Query(value = """
            from TableDefinition t where t.namaTable = :namaTable
            """)
    public Set<TableDefinition> findByNamaTable(String namaTable);

}
