package com.claimswift.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaVerifyRequest {

    @NotBlank(message="Username required")
    private String username;

    @Pattern(regexp="\\d{6}", message="OTP must be 6 digits")
    private String code;
}