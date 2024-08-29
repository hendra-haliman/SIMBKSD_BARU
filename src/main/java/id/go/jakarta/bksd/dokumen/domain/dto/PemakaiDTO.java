package id.go.jakarta.bksd.dokumen.domain.dto;

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
    //private String password;
    private Boolean enabled;
    private AlamatDTO alamat;
    private Set<RoleDTO> roles;

}