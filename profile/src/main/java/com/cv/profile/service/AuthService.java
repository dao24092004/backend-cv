package com.cv.profile.service;

import com.cv.profile.dto.request.AuthRequest;
import com.cv.profile.dto.request.ChangePasswordRequest;
import com.cv.profile.dto.request.RegisterRequest;
import com.cv.profile.dto.response.AuthResponse;

public interface AuthService {
    public AuthResponse register(RegisterRequest request);

    public AuthResponse login(AuthRequest request);

    public void changePassword(ChangePasswordRequest request);

}
