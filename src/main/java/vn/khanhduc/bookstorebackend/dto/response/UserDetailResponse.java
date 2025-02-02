package vn.khanhduc.bookstorebackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.khanhduc.bookstorebackend.common.Gender;

import java.io.Serializable;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailResponse implements Serializable {
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private Integer age;
    private Gender gender;
    private String avatarUrl;
}
