package vn.khanhduc.bookstorebackend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class UserCreationRequest implements Serializable {

    @NotBlank(message = "FirstName cannot be blank")
    private String firstName;

    @NotBlank(message = "LastName cannot be blank")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotNull(message = "Birthday cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
