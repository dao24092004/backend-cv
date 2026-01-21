package com.cv.profile.service.impls;

import com.cv.profile.dto.request.AuthRequest;
import com.cv.profile.dto.request.ChangePasswordRequest;
import com.cv.profile.dto.request.RegisterRequest;
import com.cv.profile.dto.response.AuthResponse;
import com.cv.profile.model.Profile;
import com.cv.profile.model.Role;
import com.cv.profile.model.User;
import com.cv.profile.repository.ProfileRepository;
import com.cv.profile.repository.UserRepository;
import com.cv.profile.security.JwtUtils;
import com.cv.profile.service.AuthService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository; // Repository của Profile entity
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập '" + request.getUsername() + "' đã tồn tại!");
        }

        Profile profile;

        if (request.getProfileId() != null) {
            profile = profileRepository.findById(request.getProfileId())
                    .orElseThrow(
                            () -> new RuntimeException("Không tìm thấy Profile với ID: " + request.getProfileId()));

            if (request.getFullName() != null && !request.getFullName().isEmpty()) {
                profile.setFullName(request.getFullName());
            }

        } else {
            profile = new Profile();
            profile.setFullName(request.getFullName());
            profile.setIsActive(true);
        }
        var user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER); // Mặc định là USER
        user.setProfile(profile); // Cascade ALL sẽ tự động lưu Profile

        userRepository.save(user);

        var jwtToken = jwtUtils.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .profileId(user.getProfile().getId())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtUtils.generateToken(user);

        // Lấy ID profile nếu user có profile (Admin có thể không có profile)
        Long profileId = (user.getProfile() != null) ? user.getProfile().getId() : null;

        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .profileId(profileId)
                .build();
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Kiểm tra mật khẩu cũ có đúng không
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
        }

        // Kiểm tra mật khẩu mới và xác nhận (nếu cần)
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp!");
        }

        // Mã hóa và lưu mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}