package com.dws.ActualRetro.jwt;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 9)
    private String phone;

    private List<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
