package com.cv.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // Cần thiết để Spring đọc JSON
@AllArgsConstructor
public class DegreeRequest {

    // --- QUAN TRỌNG NHẤT ---
    @NotNull(message = "Profile ID không được để trống")
    private Long profileId; // Để biết cấp bằng cho User nào

    // --- THÔNG TIN IN TRÊN BẰNG ---
    @NotBlank(message = "Tên sinh viên không được để trống")
    private String studentName; // Nguyễn Văn A (Tên in trên bằng có thể khác tên Profile)

    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentId; // CT0501

    @NotBlank(message = "Tên văn bằng không được để trống")
    private String degreeName; // Bằng Kỹ sư CNTT

    @NotBlank(message = "Chuyên ngành không được để trống")
    private String major; // Công nghệ thông tin

    @NotBlank(message = "Ngày sinh không được để trống")
    private String dob; // 01/01/2000

    @NotBlank(message = "Xếp loại không được để trống")
    private String classification; // Giỏi

    @NotBlank(message = "Năm tốt nghiệp không được để trống")
    private String graduationYear; // 2025

    @NotBlank(message = "Số hiệu bằng không được để trống")
    private String serialNumber; // B123456

    private String verificationLink;
}