package vn.khanhduc.bookstorebackend.service;

import com.nimbusds.jose.JOSEException;
import vn.khanhduc.bookstorebackend.model.User;
import java.text.ParseException;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractUserName(String accessToken);
    boolean verificationToken(String token, User user) throws ParseException, JOSEException;
    String buildAuthority(User user);
    String buildPermissions(User user);
    long extractTokenExpired(String token);
}
