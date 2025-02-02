package vn.khanhduc.bookstorebackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.khanhduc.bookstorebackend.common.UserType;
import vn.khanhduc.bookstorebackend.dto.request.UserCreationRequest;
import vn.khanhduc.bookstorebackend.dto.response.UserCreationResponse;
import vn.khanhduc.bookstorebackend.model.Role;
import vn.khanhduc.bookstorebackend.model.User;
import vn.khanhduc.bookstorebackend.repository.RoleRepository;
import vn.khanhduc.bookstorebackend.repository.UserRepository;
import vn.khanhduc.bookstorebackend.service.CloudinaryService;
import vn.khanhduc.bookstorebackend.service.impl.UserServiceImpl;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void createUser_success() {
        UserCreationRequest request = new UserCreationRequest();
        request.setFirstName("Le Khanh");
        request.setLastName("Duc");
        request.setEmail("duc@gmail.com");
        request.setPassword("123456");
        request.setBirthday(LocalDate.of(2003, 10, 2));

        Role role = new Role();
        role.setName(UserType.USER.toString());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("password-encoder");
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthday(request.getBirthday());

        when(userRepository.existsByEmail("duc@gmail.com")).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("password-encoder");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserCreationResponse response = userService.createUser(request);
        assertNotNull(response);
        assertEquals("Le Khanh", response.getFirstName());
        assertEquals("Duc", response.getLastName());
        assertEquals("duc@gmail.com", response.getEmail());
        assertEquals(String.format("%s %s", request.getFirstName(), request.getLastName()), response.getFullName());
        assertEquals(request.getBirthday(), response.getBirthday());
    }
}
