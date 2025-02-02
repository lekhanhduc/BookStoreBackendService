package vn.khanhduc.bookstorebackend.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;
import vn.khanhduc.bookstorebackend.dto.request.LogoutRequest;
import vn.khanhduc.bookstorebackend.dto.request.SignInRequest;
import vn.khanhduc.bookstorebackend.dto.response.RefreshTokenResponse;
import vn.khanhduc.bookstorebackend.dto.response.SignInResponse;

public interface AuthenticationService {
    SignInResponse signIn(SignInRequest request,  HttpServletResponse response);
    RefreshTokenResponse refreshToken(@CookieValue(name = "refreshToken") String refreshToken);
    void signOut(LogoutRequest request, HttpServletResponse response);
}
