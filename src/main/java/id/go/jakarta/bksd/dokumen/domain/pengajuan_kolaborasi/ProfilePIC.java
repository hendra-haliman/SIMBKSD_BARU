package id.go.jakarta.bksd.dokumen.domain.pengajuan_kolaborasi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pk_profile_pic")
@Data
public class ProfilePIC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String namaLengkap;

    private String email;

    private String no_telephone;
}
