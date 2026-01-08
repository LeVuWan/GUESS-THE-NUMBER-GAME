package com.inmobi.services;

import com.inmobi.dtos.req.LoginDto;
import com.inmobi.dtos.req.RegisterDto;
import com.inmobi.dtos.res.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginDto request);

    void register(RegisterDto request);
}
