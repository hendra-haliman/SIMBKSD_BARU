package id.go.jakarta.bksd.dokumen.domain.datatables;

import java.util.Date;
import java.util.List;

import id.go.jakarta.bksd.dokumen.domain.Search;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class PagingRequest {

    private int start;
    private int length;
    private int draw;
    private List<Order> order;
    private List<Column> columns;
    private Search search;

    private Date filterStartDate;
    private Date filterEndDate;
    private int filterTahun;
    private List<Long> filterBagian;
    private List<Long> filterOPD;
    private List<Long> filterJenisMitra;

    private Date filterTglBerakhirStartDate;
    private Date filterTglBerakhirEndDate;
    private boolean includeNullTglBerakhir;
    private List<Long> filterAsisten;
    private List<Long> filterJenisNaskah;
    private boolean filterEmptyFile;
    private boolean filterEmptyRumpunOPD;

    private String url;
}