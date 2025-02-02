package vn.khanhduc.bookstorebackend.service;

import org.springframework.web.multipart.MultipartFile;
import vn.khanhduc.bookstorebackend.dto.request.UpdateUserRequest;
import vn.khanhduc.bookstorebackend.dto.request.UserCreationRequest;
import vn.khanhduc.bookstorebackend.dto.response.UpdateUserResponse;
import vn.khanhduc.bookstorebackend.dto.response.UserCreationResponse;
import vn.khanhduc.bookstorebackend.dto.response.UserDetailResponse;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserCreationResponse createUser(UserCreationRequest request);
    List<UserDetailResponse> getAllUser();
    Optional<String> getAvatarUserLogin();
    UserDetailResponse getUserDetailByUserLogin(Long id);
    UpdateUserResponse updateUserProfile(UpdateUserRequest request, MultipartFile avatar);
}
