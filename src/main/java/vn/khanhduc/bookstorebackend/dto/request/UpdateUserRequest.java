package vn.khanhduc.bookstorebackend.dto.request;

import lombok.Getter;
import vn.khanhduc.bookstorebackend.common.Gender;
import java.io.Serializable;

@Getter
public class UpdateUserRequest implements Serializable {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Integer age;
    private Gender gender;
}
