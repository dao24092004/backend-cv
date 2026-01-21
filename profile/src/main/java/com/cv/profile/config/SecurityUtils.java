package com.cv.profile.config;

import com.cv.profile.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null; // Hoặc ném Exception nếu bắt buộc phải đăng nhập
    }

    // Hàm lấy Profile ID nhanh
    public static Long getCurrentProfileId() {
        User user = getCurrentUser();
        if (user != null && user.getProfile() != null) {
            return user.getProfile().getId();
        }
        throw new RuntimeException("User không tồn tại hoặc chưa liên kết Profile!");
    }
}
