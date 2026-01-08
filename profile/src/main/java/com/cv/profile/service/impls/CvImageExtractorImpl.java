package com.cv.profile.service.impls;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cv.profile.service.CvImageExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CvImageExtractorImpl implements CvImageExtractor {

    private final Cloudinary cloudinary;

    // Inject Cloudinary Bean (đã cấu hình ở bước trước)
    public CvImageExtractorImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String extractAvatar(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (fileName == null)
            return null;

        try {
            if (fileName.toLowerCase().endsWith(".pdf")) {
                return extractFromPdf(file);
            } else if (fileName.toLowerCase().endsWith(".docx")) {
                return extractFromDocx(file);
            }
        } catch (Exception e) {
            log.error("Lỗi khi trích xuất ảnh từ file: {}", e.getMessage());
        }
        return null; // Trả về null nếu không tìm thấy hoặc lỗi
    }

    private String extractFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            if (document.getNumberOfPages() > 0) {
                PDPage page = document.getPage(0);
                PDResources resources = page.getResources();
                int maxScore = 0;
                BufferedImage bestImage = null;

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
                    return saveImageToCloud(bestImage); // Đổi tên hàm cho rõ nghĩa
                }
                log.warn("Không tìm thấy ảnh nào phù hợp trong PDF");
            }
        }
        return null;
    }

    private String extractFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            List<XWPFPictureData> pictures = doc.getAllPictures();

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
                return saveImageToCloud(bestImage);
            }
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
            return 0;

        return width * height;
    }

    /**
     * Thay đổi logic: Không lưu vào ổ cứng mà upload thẳng lên Cloudinary
     */
    private String saveImageToCloud(BufferedImage bufferedImage) throws IOException {
        if (bufferedImage == null) {
            return null;
        }

        // 1. Chuyển BufferedImage thành mảng byte (byte[])
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Ghi ảnh vào stream dưới dạng PNG để giữ chất lượng (Cloudinary sẽ tự tối ưu
        // sau)
        boolean writeResult = ImageIO.write(bufferedImage, "png", baos);

        if (!writeResult) {
            log.error("❌ Không thể chuyển đổi ảnh sang định dạng PNG.");
            return null;
        }

        byte[] imageBytes = baos.toByteArray();
        baos.close(); // Đóng stream (tuy không bắt buộc với ByteArrayOutputStream nhưng là thói quen
                      // tốt)

        // 2. Tạo tên file ngẫu nhiên cho Cloudinary
        String fileName = "extracted_avatar_" + UUID.randomUUID().toString();

        // 3. Upload lên Cloudinary
        Map uploadResult = cloudinary.uploader().upload(imageBytes,
                ObjectUtils.asMap(
                        "public_id", fileName,
                        "folder", "cv_avatars" // (Tùy chọn) Gom vào thư mục riêng trên Cloud cho gọn
                ));

        // 4. Lấy URL secure (https)
        String secureUrl = (String) uploadResult.get("secure_url");

        return secureUrl;
    }
}