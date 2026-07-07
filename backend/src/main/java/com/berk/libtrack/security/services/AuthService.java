package com.berk.libtrack.security.services;

import com.berk.libtrack.domain.dto.LoginRequest;
import com.berk.libtrack.domain.dto.LoginResponse;
import com.berk.libtrack.domain.dto.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void promoteToAdmin(Long userId);

    Long getMemberIdForUsername(String username);
}