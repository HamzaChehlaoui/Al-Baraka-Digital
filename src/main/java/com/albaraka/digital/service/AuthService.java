package com.albaraka.digital.service;

import com.albaraka.digital.dto.request.LoginRequest;
import com.albaraka.digital.dto.request.RegisterRequest;
import com.albaraka.digital.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);


}
