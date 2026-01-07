package com.cv.profile.service.impls;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;
import com.cv.profile.service.CvImageExtractor;

import lombok.extern.slf4j.Slf4j; // Sử dụng SLF4J để log chuyên nghiệp hơn

@Slf4j
@Service
public class CvImageExtractorImpl implements CvImageExtractor {

    // Khai báo tên thư mục
    private final String UPLOAD_DIR_NAME = "uploads";

    @Override
    public String extractAvatar(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        log.info("--- Bắt đầu trích xuất ảnh từ file: {} ---", fileName);

        if (fileName == null)
            return null;

        try {
            if (fileName.toLowerCase().endsWith(".pdf")) {
                log.info("Nhận diện định dạng PDF");
                return extractFromPdf(file);
            } else if (fileName.toLowerCase().endsWith(".docx")) {
                log.info("Nhận diện định dạng DOCX");
                return extractFromDocx(file);
            }
        } catch (Exception e) {
            log.error("Lỗi nghiêm trọng khi trích xuất ảnh: ", e);
        }
        return null;
    }

    private String extractFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            if (document.getNumberOfPages() > 0) {
                PDPage page = document.getPage(0);
                PDResources resources = page.getResources();
                int maxScore = 0;
                BufferedImage bestImage = null;

                log.info("Đang quét trang 1 của PDF...");
                for (COSName xObjectName : resources.getXObjectNames()) {
                    if (resources.isImageXObject(xObjectName)) {
                        PDImageXObject image = (PDImageXObject) resources.getXObject(xObjectName);
                        BufferedImage bufferedImage = image.getImage();
                        int score = calculateImageScore(bufferedImage);

                        if (score > maxScore) {
                            maxScore = score;
                            bestImage = bufferedImage;
                        }
                    }
                }

                if (bestImage != null) {
                    log.info("Đã tìm thấy ảnh tốt nhất trong PDF với điểm số: {}", maxScore);
                    return saveImage(bestImage);
                }
                log.warn("Không tìm thấy ảnh nào phù hợp trong PDF");
            }
        }
        return null;
    }

    private String extractFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            List<XWPFPictureData> pictures = doc.getAllPictures();
            log.info("Tìm thấy {} hình ảnh trong file Word", pictures.size());

            int maxScore = 0;
            BufferedImage bestImage = null;

            for (XWPFPictureData pic : pictures) {
                byte[] data = pic.getData();
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(data));

                if (bufferedImage == null)
                    continue;

                int score = calculateImageScore(bufferedImage);
                if (score > maxScore) {
                    maxScore = score;
                    bestImage = bufferedImage;
                }
            }

            if (bestImage != null) {
                log.info("Đã tìm thấy ảnh tốt nhất trong DOCX với điểm số: {}", maxScore);
                return saveImage(bestImage);
            }
            log.warn("Không tìm thấy ảnh nào phù hợp trong DOCX");
        }
        return null;
    }

    private int calculateImageScore(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        float ratio = (float) width / height;

        // Bỏ qua ảnh quá nhỏ hoặc quá méo (banner, icon)
        if (width < 50 || height < 50)
            return 0;
        if (ratio > 2.2 || ratio < 0.4)
            return 0;
        if (width > 1200 || height > 1200)
            return 0; // Bỏ qua ảnh nền quá to

        return width * height;
    }

    private String saveImage(BufferedImage bufferedImage) throws IOException {
        // Tạo tên file
        String fileName = UUID.randomUUID().toString() + ".png"; // Dùng PNG để giữ chất lượng và tránh lỗi nén JPG

        String rootPath = System.getProperty("user.dir");
        Path uploadPath = Paths.get(rootPath, UPLOAD_DIR_NAME);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        File outputFile = new File(uploadPath.toFile(), fileName);

        // Kiểm tra xem bufferedImage có null không trước khi ghi
        if (bufferedImage == null) {
            log.error("❌ BufferedImage bị null, không thể ghi file!");
            return null;
        }

        // Ghi file theo định dạng PNG (linh hoạt hơn JPG trong Java ImageIO)
        boolean result = ImageIO.write(bufferedImage, "png", outputFile);

        if (result) {
            log.info("✅ FILE ĐÃ ĐƯỢC LƯU THÀNH CÔNG TẠI: {}", outputFile.getAbsolutePath());
        } else {
            log.error("❌ ImageIO.write trả về false! Có thể do định dạng ảnh không hỗ trợ.");
            throw new IOException("Failed to write image file to disk.");
        }

        // Trả về link (nhớ đổi đuôi thành .png)
        return "http://localhost:8080/uploads/" + fileName;
    }
}