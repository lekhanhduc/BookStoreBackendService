package vn.khanhduc.bookstorebackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.khanhduc.bookstorebackend.common.UserStatus;
import vn.khanhduc.bookstorebackend.common.UserType;
import vn.khanhduc.bookstorebackend.dto.request.UpdateUserRequest;
import vn.khanhduc.bookstorebackend.dto.request.UserCreationRequest;
import vn.khanhduc.bookstorebackend.dto.response.UpdateUserResponse;
import vn.khanhduc.bookstorebackend.dto.response.UserCreationResponse;
import vn.khanhduc.bookstorebackend.dto.response.UserDetailResponse;
import vn.khanhduc.bookstorebackend.exception.ErrorCode;
import vn.khanhduc.bookstorebackend.exception.AppException;
import vn.khanhduc.bookstorebackend.model.Role;
import vn.khanhduc.bookstorebackend.model.User;
import vn.khanhduc.bookstorebackend.model.UserHasRole;
import vn.khanhduc.bookstorebackend.repository.RoleRepository;
import vn.khanhduc.bookstorebackend.repository.UserRepository;
import vn.khanhduc.bookstorebackend.service.CloudinaryService;
import vn.khanhduc.bookstorebackend.service.UserService;
import vn.khanhduc.bookstorebackend.utils.SecurityUtils;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCreationResponse createUser(UserCreationRequest request) {
        log.info("User creation");
        if(userRepository.existsByEmail(request.getEmail())) {
            log.error("User already exists {}", request.getEmail());
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = roleRepository.findByName(String.valueOf(UserType.USER))
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        User user = User.builder()
                .email(request.getEmail())
                .fullName(String.format("%s %s", request.getFirstName(), request.getLastName()))
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthday(request.getBirthday())
                .userStatus(UserStatus.ACTIVE)
                .build();
        user.setCreatedBy(user.getEmail());

        UserHasRole userHasRole = UserHasRole.builder()
                .role(role)
                .user(user)
                .build();
        user.setUserHasRoles(Set.of(userHasRole));

        userRepository.save(user);

        log.info("User created");
        return UserCreationResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(String.format("%s %s", user.getFirstName(), user.getLastName()))
                .email(user.getEmail())
                .birthday(user.getBirthday())
                .build();
    }

    @Override
    public List<UserDetailResponse> getAllUser() {
        log.info("Get all user");
        return userRepository.findAll()
                .stream()
                .map(user -> UserDetailResponse.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .fullName(String.format("%s %s", user.getFirstName(), user.getLastName()))
                        .phone(user.getPhoneNumber())
                        .build())
                .toList();
    }

    @Override
    public Optional<String> getAvatarUserLogin() {
        log.info("Get avatar user login");
        String email = SecurityUtils.getCurrentLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return StringUtils.isBlank(user.getAvatarUrl()) ? Optional.empty()
                : Optional.of(user.getAvatarUrl());
    }

    @Override
    public UserDetailResponse getUserDetailByUserLogin(Long id) {
        log.info("Get user detail by login");
        String email = SecurityUtils.getCurrentLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return UserDetailResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhoneNumber())
                .age(user.getAge())
                .gender(user.getGender())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public UpdateUserResponse updateUserProfile(UpdateUserRequest request, MultipartFile avatar) {
        log.info("Update user profile");
        var email = SecurityUtils.getCurrentLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UpdateUserResponse userResponse = new UpdateUserResponse();
        if(avatar != null) {
            log.info("Upload avatar");
            String url = cloudinaryService.uploadImage(avatar);
            userResponse.setAvatarUrl(url);
            user.setAvatarUrl(url);
        }
        if(request != null) {
            log.info("Update user request");
            if(StringUtils.isNotBlank(request.getFirstName())) {
                userResponse.setFirstName(request.getFirstName());
                user.setFirstName(request.getFirstName());
            } else if(StringUtils.isNotBlank(request.getLastName())) {
                userResponse.setLastName(request.getLastName());
                user.setLastName(request.getLastName());
            } else if(StringUtils.isNotBlank(request.getPhoneNumber())) {
                userResponse.setPhoneNumber(request.getPhoneNumber());
                user.setPhoneNumber(request.getPhoneNumber());
            } else if(request.getAge() != null) {
                userResponse.setAge(request.getAge());
                user.setAge(request.getAge());
            } else if(StringUtils.isNotBlank(request.getGender().toString())) {
                userResponse.setGender(request.getGender());
                user.setGender(request.getGender());
            }
        }
        userRepository.save(user);
        log.info("Updated user success");
        return userResponse;
    }

}
