package id.go.jakarta.bksd.dokumen.domain.datatables;

import lombok.Data;
import java.util.List;

@Data
public class State {
    private Integer number;
    private Integer start;
    private Integer length;
    private Order order;
    private DTSearch search;
    private List<DTColumn> columns;

}
