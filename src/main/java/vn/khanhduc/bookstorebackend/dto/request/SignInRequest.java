package vn.khanhduc.bookstorebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {

    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
