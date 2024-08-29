package id.go.jakarta.bksd.dokumen.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class Pemakai extends AuditMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String jabatan;

    private String namaDepan;

    private String namaKeluarga;

    private String email;

    private String password;

    private boolean enabled;

    @Column(name = "reset_str")
    private String resetStr;

    // @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    // // @JoinColumn(name = "alamatId", referencedColumnName = "id")
    // private Alamat alamat;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @Column(name = "profile_image")
    private byte[] profileImage;

    @ManyToMany(targetEntity = TableDefinition.class)
    private List<TableDefinition> tableDefinition = new ArrayList<>();

    @ManyToMany(targetEntity = ColumnOrder.class)
    private List<ColumnOrder> columnOrder = new ArrayList<>();

    public String getFullName() {
        return namaDepan + " " + namaKeluarga;
    }

    public String getName() {
        return namaDepan + " " + namaKeluarga;
    }

    public Pemakai(String title, String jabatan, String namaDepan, String namaKeluarga, String email, String password,
            boolean enabled) {
        this.title = title;
        this.jabatan = jabatan;
        this.namaDepan = namaDepan;
        this.namaKeluarga = namaKeluarga;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }
}
