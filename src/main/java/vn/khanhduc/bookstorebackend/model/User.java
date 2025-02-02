package vn.khanhduc.bookstorebackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.khanhduc.bookstorebackend.common.Gender;
import vn.khanhduc.bookstorebackend.common.UserStatus;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "User")
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User extends AbstractEntity<Long> implements UserDetails {

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phoneNumber;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "url_avatar")
    private String avatarUrl;

    @Column(name = "age")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;

    @Column(name = "birthday")
    private LocalDate birthday;

    @OneToMany(mappedBy = "author",cascade = CascadeType.ALL)
    private Set<Book> books;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserHasRole> userHasRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userHasRoles.stream().map(UserHasRole::getRole)
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired(); // default true
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked(); // default true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired(); // default true
    }

    @Override
    public boolean isEnabled() {  // default true
        return this.userStatus.equals(UserStatus.ACTIVE);
    }
}

/*
1. isAccountNonExpired() --> Kiểm tra xem tài khoản của người dùng có bị hết hạn hay không.
                         --> Một tài khoản hết hạn thường được sử dụng để vô hiệu hóa người dùng sau một
                             khoảng thời gian nhất định.

2.isAccountNonLocked() --> Kiểm tra xem tài khoản của người dùng có bị khóa hay không.
                       --> Thường được sử dụng để tạm thời vô hiệu hóa tài khoản của người dùng nếu có hành
                           vi đáng ngờ, như nhập sai mật khẩu nhiều lần.

3.isCredentialsNonExpired() --> Kiểm tra xem thông tin xác thực (mật khẩu) của người dùng có bị hết hạn hay không.
                            --> Thường được sử dụng khi bạn muốn yêu cầu người dùng thay đổi mật khẩu sau một
                                khoảng thời gian.

4.isEnabled --> được sử dụng để kiểm tra xem tài khoản của người dùng có được kích hoạt hay không.
            --> nếu Khi phương thức này trả về false, Spring Security sẽ ngăn người dùng đăng nhập,
                ngay cả khi họ nhập đúng tên đăng nhập và mật khẩu.
*/
