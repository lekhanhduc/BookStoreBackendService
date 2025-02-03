package vn.khanhduc.bookstorebackend.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.khanhduc.bookstorebackend.exception.ErrorCode;
import vn.khanhduc.bookstorebackend.exception.AppException;
import vn.khanhduc.bookstorebackend.model.User;
import vn.khanhduc.bookstorebackend.model.UserHasRole;
import vn.khanhduc.bookstorebackend.service.JwtService;
import vn.khanhduc.bookstorebackend.service.RedisService;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {

    private final RedisService redisService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public String generateAccessToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet =  new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("identity-service")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(60, ChronoUnit.MINUTES).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("Authority", buildAuthority(user))
                .claim("Permission", buildPermissions(user))
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    public String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        var claimsSet =  new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("identity-service")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(14, ChronoUnit.DAYS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();

        var payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    public String extractUserName(String accessToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public boolean verificationToken(String token, User user) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        var jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if(StringUtils.isNotBlank(redisService.get(jwtId))) {
            throw new AppException(ErrorCode.TOKEN_BLACK_LIST);
        }
        var email = signedJWT.getJWTClaimsSet().getSubject();
        var expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        if( !Objects.equals(email, user.getEmail())) {
            log.error("Email in token not match email system");
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        if(expiration.before(new Date())) {
            log.error("Token expired");
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        return signedJWT.verify(new MACVerifier(secretKey));
    }

    @Override
    public long extractTokenExpired(String token) {
        try {
            long expirationTime = SignedJWT.parse(token)
                    .getJWTClaimsSet().getExpirationTime().getTime();
            long currentTime = System.currentTimeMillis();
            return Math.max(expirationTime - currentTime, 0);
        } catch (ParseException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    private String buildAuthority(User user) {
        return user.getUserHasRoles().stream().map(u -> u.getRole().getName())
                .collect(Collectors.joining(", "));
    }


    private String buildPermissions(User user) {
        StringJoiner joiner = new StringJoiner(", ");
        Optional.ofNullable(user.getUserHasRoles())
                .ifPresent(userHasRoles -> userHasRoles.stream().map(UserHasRole::getRole)
                .flatMap(role -> role.getRoleHasPermissions().stream().map(roleHasPermission -> roleHasPermission.getRole().getName()))
                .distinct()
                .forEach(joiner::add));
        return joiner.toString();
    }

}
