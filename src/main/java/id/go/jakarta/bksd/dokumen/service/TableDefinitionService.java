package id.go.jakarta.bksd.dokumen.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.TableDefinition;
import id.go.jakarta.bksd.dokumen.repository.TableDefinitionRepository;

@Service
public class TableDefinitionService {

    private TableDefinitionRepository tableDefinitionRepository;

    public TableDefinitionService(TableDefinitionRepository dokumenTableDefinitionRepository) {
        this.tableDefinitionRepository = dokumenTableDefinitionRepository;
    }

    public Set<TableDefinition> findByNamaTable(String namaTable) {
        return tableDefinitionRepository.findByNamaTable(namaTable);
    }

    public List<TableDefinition> findByIds(List<Long> ids) {
        return tableDefinitionRepository.findAllById(ids);
    }
}
