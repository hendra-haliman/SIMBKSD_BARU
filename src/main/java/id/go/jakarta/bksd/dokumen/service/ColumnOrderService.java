package id.go.jakarta.bksd.dokumen.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.ColumnOrder;
import id.go.jakarta.bksd.dokumen.repository.ColumnOrderRepository;

@Service
public class ColumnOrderService {
    private ColumnOrderRepository columnOrderRepository;

    public ColumnOrderService(ColumnOrderRepository columnOrderRepository) {
        this.columnOrderRepository = columnOrderRepository;
    }

    public List<ColumnOrder> findByNamaTable(String namaTable) {
        return columnOrderRepository.findByNamaTable(namaTable);
    }

    public ColumnOrder save(ColumnOrder columnOrder) {
        return columnOrderRepository.save(columnOrder);
    }

    public List<ColumnOrder> findByNamaTableAndColumnNumber(String tableName, List<Integer> columnNumbers) {
        List<ColumnOrder> data = columnOrderRepository.findByNamaTableAndColumnNumber(tableName, columnNumbers);
        return columnNumbers.stream()
                .flatMap(order -> data.stream().filter(e -> e.getColumnNumber().equals(order)))
                .collect(Collectors.toList());
    }
}
