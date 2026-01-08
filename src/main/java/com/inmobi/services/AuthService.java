package com.inmobi.services;

import java.text.ParseException;

import com.inmobi.dtos.req.LoginDto;
import com.inmobi.dtos.req.RegisterDto;
import com.inmobi.dtos.res.LoginResponse;
import com.nimbusds.jose.JOSEException;

public interface AuthService {
    LoginResponse login(LoginDto request);

    void register(RegisterDto request);

    String refreshToken(String refreshToken) throws ParseException, ParseException, JOSEException;
}
