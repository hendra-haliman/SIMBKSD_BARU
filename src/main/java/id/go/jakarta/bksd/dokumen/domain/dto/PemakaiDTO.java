package id.go.jakarta.bksd.dokumen.domain.dto;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class PemakaiDTO {

    private Long id;
    private String title;
    private String jabatan;
    private String namaDepan;
    private String namaKeluarga;
    private String email;
    private Boolean enabled;
    private Set<RoleDTO> roles;
    private List<String> privileges;
}