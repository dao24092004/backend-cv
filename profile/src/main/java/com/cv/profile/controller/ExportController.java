package com.cv.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cv.profile.dto.response.PortfolioDTO;
import com.cv.profile.service.PortfolioService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
public class ExportController {

    private final PortfolioService portfolioService;

    /**
     * API hỗ trợ Export PDF.
     * Lưu ý: Trong kiến trúc React hiện đại, việc render PDF (giao diện đẹp)
     * thường do thư viện Frontend (react-to-print) đảm nhận.
     * API này đóng vai trò cung cấp Dữ liệu chuẩn (Clean JSON) để Frontend in,
     * hoặc Log lại hành động download của user.
     */
    @GetMapping("/cv-data")
    public ResponseEntity<PortfolioDTO> getCvDataForExport() {
        // Log hành động export
        System.out.println(">>> User requesting CV Export at: " + LocalDateTime.now());

        // Trả về dữ liệu đầy đủ nhất để in
        return ResponseEntity.ok(portfolioService.getPublicPortfolio());
    }

    /**
     * (Optional) Nếu Backend thực sự phải render PDF (dùng iText/JasperReport)
     * Đây là code mẫu giả lập trả về file byte[]
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPdfMock() {
        // Code tạo file PDF thực tế rất dài và phức tạp về căn chỉnh UI.
        // Theo đề bài "Web CV", in từ Frontend là chuẩn nhất.
        // API này chỉ demo headers.
        byte[] mockPdfContent = new byte[0]; // File rỗng

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cv_profile.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(mockPdfContent);
    }
}
