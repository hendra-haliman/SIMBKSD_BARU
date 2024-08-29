package id.go.jakarta.bksd.dokumen.domain.datatables;

import lombok.Data;

@Data
public class DTSearch {
    private String search;
    private boolean regex;
    private boolean smart;
    private boolean caseInsensitive;
}
