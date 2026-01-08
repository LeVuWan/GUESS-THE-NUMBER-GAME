package com.inmobi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inmobi.Exception.ResourceNotFoundException;
import com.inmobi.Exception.TurnOverException;
import com.inmobi.dtos.req.UserTurnDto;
import com.inmobi.dtos.res.ResponseData;
import com.inmobi.dtos.res.ResponseError;
import com.inmobi.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/turn")
    public ResponseData<?> userTurn(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserTurnDto dto) {
        try {
            Long userId = Long.parseLong(jwt.getClaims().get("userId").toString());
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User turn processed",
                    userService.userTurn(userId, dto.getUserGuess()));
        } catch (ResourceNotFoundException e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (TurnOverException e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "User turn failed: " + e.getMessage());
        }
    }

}
