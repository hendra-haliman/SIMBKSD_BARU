package id.go.jakarta.bksd.dokumen.domain;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Page<T> {

    public Page(List<T> data) {
        this.data = data;
    }

    public Page(List<T> data, int recordsFiltered, int recordsTotal, int draw) {
        this.data = data;
        this.recordsFiltered = recordsFiltered;
        this.recordsTotal = recordsTotal;
        this.draw = draw;
    }

    private List<T> data;
    private int recordsFiltered;
    private int recordsTotal;
    private int draw;

}