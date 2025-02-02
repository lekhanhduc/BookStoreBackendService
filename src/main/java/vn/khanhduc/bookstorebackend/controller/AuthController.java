package vn.khanhduc.bookstorebackend.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.khanhduc.bookstorebackend.dto.request.LogoutRequest;
import vn.khanhduc.bookstorebackend.dto.request.SignInRequest;
import vn.khanhduc.bookstorebackend.dto.response.RefreshTokenResponse;
import vn.khanhduc.bookstorebackend.dto.response.ResponseData;
import vn.khanhduc.bookstorebackend.dto.response.SignInResponse;
import vn.khanhduc.bookstorebackend.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/sign-in")
    ResponseData<SignInResponse> signIn(@RequestBody @Valid SignInRequest request,
                                        HttpServletResponse response) {
        var result = authenticationService.signIn(request, response);
        return ResponseData.<SignInResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Sign in success")
                .data(result)
                .build();
    }

    @PostMapping("/refresh-token")
    ResponseData<RefreshTokenResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken) {
        var result = authenticationService.refreshToken(refreshToken);
        return ResponseData.<RefreshTokenResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Refreshed token success")
                .data(result)
                .build();
    }

    @PostMapping("/logout")
    ResponseData<Void> logout(@RequestBody @Valid LogoutRequest request, HttpServletResponse response) {
        authenticationService.signOut(request, response);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Sign out success")
                .build();
    }

}
