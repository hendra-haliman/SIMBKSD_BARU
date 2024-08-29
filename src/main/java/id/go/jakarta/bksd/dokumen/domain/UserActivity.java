package id.go.jakarta.bksd.dokumen.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class UserActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;

  @ManyToOne(targetEntity = Pemakai.class)
  private Pemakai pemakai;

  private String methodName;

  @Column(columnDefinition = "varchar(8092)")
  private String arguments;

  private String targetClass;

  private LocalDateTime timestamp;

  public UserActivity(String username, Pemakai pemakai, String methodName, String arguments, String targetClass) {
    this.username = username;
    this.pemakai = pemakai;
    this.methodName = methodName;
    this.arguments = arguments;
    this.targetClass = targetClass;
    this.timestamp = LocalDateTime.now();
  }

}