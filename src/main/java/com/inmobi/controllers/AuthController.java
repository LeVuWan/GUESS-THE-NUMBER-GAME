package com.inmobi.controllers;

import java.text.ParseException;
import java.util.DuplicateFormatFlagsException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inmobi.Exception.ResourceNotFoundException;
import com.inmobi.Exception.UnauthenticationException;
import com.inmobi.dtos.req.LoginDto;
import com.inmobi.dtos.req.LogoutDto;
import com.inmobi.dtos.req.RefreshTokenDto;
import com.inmobi.dtos.req.RegisterDto;
import com.inmobi.dtos.res.ResponseData;
import com.inmobi.dtos.res.ResponseError;
import com.inmobi.services.AuthService;
import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseData<?> login(@Valid @RequestBody LoginDto dto) {
        try {
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Login successful",
                    authService.login(dto));
        } catch (ResourceNotFoundException e) {
            return new ResponseError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        } catch (UnauthenticationException e) {
            return new ResponseError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        } catch (Exception e) {
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseData<?> register(@Valid @RequestBody RegisterDto dto) {
        try {
            authService.register(dto);
            return new ResponseData<>(HttpStatus.CREATED.value(), "User registered successfully");
        } catch (DuplicateFormatFlagsException e) {
            return new ResponseError(HttpStatus.CONFLICT.value(), "Username already exists");
        } catch (Exception e) {
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseData<?> refreshToken(@RequestBody RefreshTokenDto dto) {
        try {
            String accessToken = authService.refreshToken(dto.getRefreshToken());
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Get access token success", accessToken);
        } catch (ResourceNotFoundException e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (ParseException e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (JOSEException e) {
            return new ResponseError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        } catch (Exception e) {
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseData<?> logout(@RequestBody LogoutDto dto, @AuthenticationPrincipal Jwt jwt) {
        try {
            Long userId = Long.parseLong(jwt.getClaims().get("userId").toString());
            authService.logout(userId);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Logout success");
        } catch (ResourceNotFoundException e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
}