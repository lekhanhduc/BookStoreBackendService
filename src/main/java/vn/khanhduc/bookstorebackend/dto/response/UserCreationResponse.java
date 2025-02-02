package vn.khanhduc.bookstorebackend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserCreationResponse implements Serializable {
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
