package vn.khanhduc.bookstorebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.khanhduc.bookstorebackend.dto.request.UpdateUserRequest;
import vn.khanhduc.bookstorebackend.dto.request.UserCreationRequest;
import vn.khanhduc.bookstorebackend.dto.response.ResponseData;
import vn.khanhduc.bookstorebackend.dto.response.UpdateUserResponse;
import vn.khanhduc.bookstorebackend.dto.response.UserCreationResponse;
import vn.khanhduc.bookstorebackend.dto.response.UserDetailResponse;
import vn.khanhduc.bookstorebackend.service.UserService;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j(topic = "USER-CONTROLLER")
public class UserController {

    private final UserService userService;

    @PostMapping("/users-creation")
    ResponseData<UserCreationResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create user controller layer");
        var result = userService.createUser(request);

        return ResponseData.<UserCreationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("User created")
                .data(result)
                .build();
    }

    @GetMapping("/users")
    ResponseData<List<UserDetailResponse>> getAll() {
        log.info("Get all user controller layer");
        var result = userService.getAllUser();
        return ResponseData.<List<UserDetailResponse>>builder()
                .code(HttpStatus.CREATED.value())
                .message("Get All User")
                .data(result)
                .build();
    }

    @GetMapping("/users/avatar")
    ResponseData<Optional<String>> getAvatar() {
        var result = userService.getAvatarUserLogin();
        return ResponseData.<Optional<String>>builder()
                .code(HttpStatus.CREATED.value())
                .message("Get Avatar User")
                .data(result)
                .build();
    }

    @PutMapping("/users")
    ResponseData<UpdateUserResponse> updateUserProfile(
            @RequestPart(required = false) @Valid UpdateUserRequest request,
            @RequestPart(name = "avatar", required = false) MultipartFile file
            ) {
        log.info("update profile controller layer");
        var result = userService.updateUserProfile(request, file);
        return ResponseData.<UpdateUserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Update user profile")
                .data(result)
                .build();
    }

}
