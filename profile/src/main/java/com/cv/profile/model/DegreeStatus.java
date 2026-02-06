package com.cv.profile.model;

public enum DegreeStatus {
    PENDING, // Đang chờ xử lý
    ISSUED, // Đã cấp thành công
    REVOKED, // Đã bị thu hồi (do sai sót hoặc kỷ luật)
    ERROR // Lỗi trong quá trình cấp
}