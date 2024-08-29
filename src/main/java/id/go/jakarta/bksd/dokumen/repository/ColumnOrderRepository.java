package id.go.jakarta.bksd.dokumen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.go.jakarta.bksd.dokumen.domain.ColumnOrder;

import java.util.List;

@Repository
public interface ColumnOrderRepository extends JpaRepository<ColumnOrder, Long> {
    @Query(value = """
            from ColumnOrder c where c.namaTable = :namaTable
            """)
    public List<ColumnOrder> findByNamaTable(String namaTable);

    @Query(value = """
            from ColumnOrder c where c.namaTable = :tablename and c.columnNumber in :columnNumber
            """)
    public List<ColumnOrder> findByNamaTableAndColumnNumber(String tablename, List<Integer> columnNumber);
}
