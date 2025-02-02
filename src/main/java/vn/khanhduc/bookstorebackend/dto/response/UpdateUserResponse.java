package vn.khanhduc.bookstorebackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import vn.khanhduc.bookstorebackend.common.Gender;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserResponse implements Serializable {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Integer age;
    private Gender gender;
    private String avatarUrl;
}
