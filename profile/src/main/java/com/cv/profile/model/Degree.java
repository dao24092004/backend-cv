package com.cv.profile.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data // Lombok sinh getter/setter/toString...
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "degrees") // Đặt tên bảng là 'degrees'
public class Degree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- NHÓM 1: THÔNG TIN IN TRÊN BẰNG (SNAPSHOT) ---
    // Lý do lưu riêng: Tên trong Profile có thể đổi, nhưng tên trên bằng thì cố
    // định.

    @Column(nullable = false)
    private String studentName; // Tên in trên bằng (Ví dụ: NGUYỄN VĂN A)

    @Column(nullable = false)
    private String studentId; // Mã sinh viên (VD: CT0501)

    @Column(nullable = false)
    private String degreeName; // Tên văn bằng (VD: Bằng Kỹ sư CNTT)

    private String major; // private String dobChuyên ngành (VD: Công nghệ phần mềm)
    private String dob;
    @Column(unique = true)
    private String serialNumber; // Số hiệu văn bằng (Số vào sổ gốc - Bắt buộc duy nhất)

    private String classification; // Xếp loại (Giỏi/Khá...)

    private String modeOfStudy; // Hình thức đào tạo (Chính quy/Vừa làm vừa học...)

    private LocalDate graduationYear; // Năm tốt nghiệp (Lưu ngày tháng để sort dễ hơn)
    @Column(name = "qr_code_url") // <--- Thêm cột này vào Database
    private String qrCodeUrl;
    // --- NHÓM 2: THÔNG TIN BLOCKCHAIN & LƯU TRỮ ---

    @Column(columnDefinition = "TEXT")
    private String pdfUrl; // Link file PDF gốc trên Cloudinary

    private String transactionHash; // Mã giao dịch trên Blockchain (TxHash)

    private String documentHash; // Mã băm (SHA256) của nội dung file PDF

    @Enumerated(EnumType.STRING)
    private DegreeStatus status; // Trạng thái: PENDING, ISSUED, REVOKED

    // --- NHÓM 3: LIÊN KẾT ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnore // Tránh vòng lặp vô hạn khi convert JSON
    @ToString.Exclude
    private Profile profile; // Bằng này thuộc về ai?
}