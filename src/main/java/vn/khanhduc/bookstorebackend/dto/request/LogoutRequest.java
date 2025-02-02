package vn.khanhduc.bookstorebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class LogoutRequest implements Serializable {

    @NotBlank(message = "Token cannot be null")
    private String accessToken;
}
