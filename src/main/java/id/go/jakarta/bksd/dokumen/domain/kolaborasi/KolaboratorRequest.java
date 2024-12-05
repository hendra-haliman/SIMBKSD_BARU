package id.go.jakarta.bksd.dokumen.domain.kolaborasi;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class KolaboratorRequest {
    private String nama;
    private String email;
    private String password;
}
