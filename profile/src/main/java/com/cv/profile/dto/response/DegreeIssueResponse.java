package com.cv.profile.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DegreeIssueResponse {
    private boolean success;
    private boolean isValid;
    private String pdfUrl; // Link Cloudinary (Java điền vào)
    private String transactionHash; // Mã giao dịch Blockchain
    private String documentHash; // Mã hash SHA256
    private String pdfBase64; // [TẠM] Nhận từ Node.js, không cần trả về FE
    private String error;
    private String qrCodeUrl;
}