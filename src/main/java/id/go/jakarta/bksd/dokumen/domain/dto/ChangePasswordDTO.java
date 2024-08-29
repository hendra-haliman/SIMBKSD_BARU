package id.go.jakarta.bksd.dokumen.domain.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String resetStr;
    private String newPassword;
    private String confirmPassword;   
}
