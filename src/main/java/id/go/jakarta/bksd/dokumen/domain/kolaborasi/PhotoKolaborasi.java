package id.go.jakarta.bksd.dokumen.domain.kolaborasi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pk_photo_kolaborasi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoKolaborasi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String thumbnail;   //nama file picture tumbnail

    private String banner;      // nama file picture banner;
}
