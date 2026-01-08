package com.inmobi.services.impl;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.inmobi.Exception.DuplicateUsernameException;
import com.inmobi.Exception.ResourceNotFoundException;
import com.inmobi.Exception.UnauthenticationException;
import com.inmobi.dtos.req.LoginDto;
import com.inmobi.dtos.req.RegisterDto;
import com.inmobi.dtos.res.LoginResponse;
import com.inmobi.models.User;
import com.inmobi.repositories.UserRepository;
import com.inmobi.services.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final byte[] secretKey;

    public AuthServiceImpl(
            UserRepository userRepository,
            @Value("${jwt.secret}") String secret) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(10);
        this.secretKey = secret.getBytes(StandardCharsets.UTF_8);
    }

    public LoginResponse login(LoginDto request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Username or password incorrect"));

        Boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new UnauthenticationException("Username or password incorrect");
        }

        String accessToken = generateTokenJwt(user, Duration.ofMinutes(15), "access");
        String refreshToken = generateTokenJwt(user, Duration.ofDays(7), "refresh");

        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        return new LoginResponse(accessToken, refreshToken, authenticated);
    }

    private String generateTokenJwt(User user, Duration ttl, String keyType) {
        try {
            Instant now = Instant.now();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .claim("userId", user.getId())
                    .claim("username", user.getUsername())
                    .claim("token_type", keyType)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plus(ttl)))
                    .build();

            JWSObject jwsObject = new JWSObject(
                    new JWSHeader.Builder(JWSAlgorithm.HS256)
                            .type(JOSEObjectType.JWT)
                            .build(),
                    new Payload(claims.toJSONObject()));

            jwsObject.sign(new MACSigner(secretKey));
            return jwsObject.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }

    @Override
    public void register(RegisterDto request) {
        Optional<User> userExist = userRepository.findByUsername(request.getUsername());

        if (userExist.isPresent()) {
            throw new DuplicateUsernameException("Username already exists");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setScore(0);
        newUser.setTurn(5);

        userRepository.save(newUser);
    }

    public String refreshToken(String refreshToken) throws ParseException, JOSEException {

        JWTClaimsSet claims = verifyRefreshToken(refreshToken, "refresh");

        String username = claims.getSubject();

        User existUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (existUser.getRefreshToken() == null ||
                !refreshToken.equals(existUser.getRefreshToken())) {
            throw new JOSEException("Refresh token revoked or invalid");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return generateTokenJwt(user, Duration.ofMinutes(15), "access");
    }

    public JWTClaimsSet verifyRefreshToken(String refreshToken, String expectedTokenType)
            throws ParseException, JOSEException {

        SignedJWT signedJWT = SignedJWT.parse(refreshToken);

        JWSVerifier verifier = new MACVerifier(secretKey);

        if (!signedJWT.verify(verifier)) {
            throw new RuntimeException("Invalid refresh token signature");
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        Date expirationTime = claims.getExpirationTime();

        if (expirationTime == null || expirationTime.before(new Date())) {
            throw new RuntimeException("Refresh token expired");
        }

        String tokenType = claims.getStringClaim("token_type");
        if (!expectedTokenType.equals(tokenType)) {
            throw new RuntimeException("Invalid token type");
        }

        return claims;

    }

    @Override
    public void logout(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRefreshToken(null);
        userRepository.save(user);
    }

}
