package com.cv.profile.service.impls;

import com.cv.profile.dto.ai.CVExtractionResult;
import com.cv.profile.service.CvParserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CvParserServiceImpl implements CvParserService {

    private final Tika tika;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.url}")
    private String apiUrl;

    public CvParserServiceImpl(ObjectMapper objectMapper) {
        // FIX 1: Bắt buộc Java sử dụng IPv4
        System.setProperty("java.net.preferIPv4Stack", "true");

        this.tika = new Tika();
        this.objectMapper = objectMapper;

        // FIX 2: Cấu hình Timeout
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(90000);

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    @Override
    public CVExtractionResult parseResume(MultipartFile file) throws IOException {
        // 1. Trích xuất text
        String extractedText;
        try {
            extractedText = tika.parseToString(file.getInputStream());
        } catch (TikaException e) {
            throw new IOException("Lỗi trích xuất văn bản từ Tika: " + e.getMessage(), e);
        }

        if (extractedText == null || extractedText.isBlank()) {
            throw new IOException("File tải lên không chứa văn bản đọc được.");
        }

        // 2. Chuẩn bị Prompt (CẬP NHẬT PHẦN DỰ ÁN ĐỂ LẤY CHI TIẾT HƠN)
        String promptText = """
                Bạn là Senior Data Scientist chuyên trách trích xuất hồ sơ khoa học.
                NHIỆM VỤ: Trích xuất TOÀN BỘ dữ liệu từ CV.

                ========================
                INPUT TEXT
                ========================
                %s

                ========================
                QUY TẮC BẮT BUỘC
                ========================
                1. Output DUY NHẤT 1 JSON object.
                2. KHÔNG markdown, KHÔNG giải thích.
                3. Giữ nguyên nội dung chi tiết, KHÔNG ĐƯỢC VIẾT TẮT hay TÓM TẮT.
                4. Trích xuất TOÀN BỘ các đầu mục, gạch đầu dòng trong mô tả công việc và dự án. KHÔNG được bỏ sót bất kỳ ý nào.

                ========================
                QUY TẮC ĐỊNH DẠNG NGÀY THÁNG (BẮT BUỘC)
                ========================
                - Mọi trường ngày tháng (startDate, endDate, date, releaseDate) PHẢI theo định dạng chuẩn: "YYYY-MM-DD".
                - Nếu chỉ có tháng/năm (ví dụ: "05/2023"), hãy tự động thêm ngày 01 -> "2023-05-01".
                - Nếu chỉ có năm (ví dụ: "2023"), hãy trả về "2023-01-01".
                - Nếu là hiện tại (Present/Now), hãy trả về chuỗi "Present".

                ========================
                QUY TẮC KIỂU DỮ LIỆU
                ========================
                ▶ CÁC FIELD SAU PHẢI LÀ STRING THUẦN (KHÔNG object, KHÔNG song ngữ):
                - email, phone, linkedin, github, avatarUrl
                - workHistory.companyName
                - education.school
                - publications.publisher
                - skills.name, skills.category
                - events.role

                ▶ CÁC FIELD SAU PHẢI LÀ OBJECT SONG NGỮ { "vi": "...", "en": "..." }:
                - fullName, jobTitle, bio, address
                - workHistory.position, workHistory.description
                - projects.name, projects.role, projects.description
                - education.degree, education.description
                - publications.name
                - events.name, events.description

                ========================
                HƯỚNG DẪN TRÍCH XUẤT CHI TIẾT (QUAN TRỌNG)
                ========================

                [DỰ ÁN & NHIỆM VỤ (AiProject)]
                - description: BẮT BUỘC phải trích xuất đầy đủ, chi tiết nhất có thể.
                  + Nếu CV có nhiều gạch đầu dòng mô tả nhiệm vụ, hãy nối tất cả chúng lại thành một đoạn văn bản đầy đủ, ngăn cách các ý bằng dấu chấm hoặc xuống dòng hợp lý.
                  + Phải bao gồm tất cả các thông tin như: Vấn đề giải quyết, Công nghệ sử dụng, Vai trò cụ thể và Kết quả đạt được.
                  + TUYỆT ĐỐI KHÔNG được chọn lọc hay chỉ lấy 1 ý đầu tiên. Phải lấy HẾT.

                [KINH NGHIỆM LÀM VIỆC]
                - description: Tương tự như dự án, liệt kê đầy đủ trách nhiệm và thành tích. Không tóm tắt, không lược bỏ.

                ========================
                OUTPUT JSON SCHEMA
                ========================
                {
                  "fullName": { "vi": "", "en": "" },
                  "email": "", "phone": "", "linkedin": "", "github": "", "avatarUrl": "",
                  "jobTitle": { "vi": "", "en": "" },
                  "bio": { "vi": "", "en": "" },
                  "address": { "vi": "", "en": "" },
                  "workHistory": [
                    { "companyName": "", "position": { "vi": "", "en": "" }, "startDate": "YYYY-MM-DD", "endDate": "YYYY-MM-DD", "description": { "vi": "", "en": "" } }
                  ],
                  "projects": [
                    { "name": { "vi": "", "en": "" }, "role": { "vi": "", "en": "" }, "description": { "vi": "", "en": "" }, "sourceCodeUrl": "", "techStack": [] }
                  ],
                  "skills": [
                    { "name": "", "category": "", "proficiency": 80 }
                  ],
                  "education": [
                    { "school": "", "degree": { "vi": "", "en": "" }, "startDate": "YYYY-MM-DD", "endDate": "YYYY-MM-DD", "description": { "vi": "", "en": "" } }
                  ],
                  "publications": [
                    { "name": { "vi": "", "en": "" }, "publisher": "", "date": "YYYY-MM-DD", "url": "" }
                  ],
                  "events": [
                    { "name": { "vi": "", "en": "" }, "role": "", "date": "YYYY-MM-DD", "description": { "vi": "", "en": "" } }
                  ]
                }
                """
                .formatted(extractedText);

        var requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(Map.of("role", "user", "content", promptText)),
                "temperature", 0.0,
                "max_tokens", 8192,
                "response_format", Map.of("type", "json_object"));

        int maxRetries = 3;
        int attempt = 0;

        // 3. Gọi API
        while (attempt < maxRetries) {
            attempt++;
            try {
                System.out.println("Đang gửi yêu cầu tới Groq AI (Lần thử " + attempt + ")...");

                String jsonResponse = restClient.post()
                        .uri(apiUrl)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .retrieve()
                        .body(String.class);

                return parseGroqResponse(jsonResponse);

            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 429) {
                    System.err.println("Groq Rate Limit exceeded. Đang chờ 5s...");
                    sleep(5000);
                } else {
                    throw new IOException(
                            "Groq API Client Error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString(), e);
                }
            } catch (ResourceAccessException e) {
                System.err.println("Lỗi kết nối mạng (Timeout/Network): " + e.getMessage());
                if (attempt >= maxRetries) {
                    throw new IOException(
                            "Không thể kết nối tới Groq AI sau " + maxRetries + " lần thử. Vui lòng kiểm tra mạng.", e);
                }
                sleep(2000);
            } catch (Exception e) {
                System.err.println("Lỗi không xác định: " + e.getMessage());
                if (attempt >= maxRetries)
                    throw new IOException("Lỗi phân tích AI: " + e.getMessage(), e);
                sleep(2000);
            }
        }
        throw new IOException("Thất bại sau " + maxRetries + " lần thử kết nối.");
    }

    private CVExtractionResult parseGroqResponse(String jsonResponse) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (!root.has("choices") || root.path("choices").isEmpty()) {
                throw new IOException("Phản hồi từ AI không chứa dữ liệu hợp lệ: " + jsonResponse);
            }

            String rawJson = root.path("choices").get(0).path("message").path("content").asText();

            if (rawJson == null || rawJson.isEmpty()) {
                throw new IOException("AI trả về nội dung rỗng.");
            }

            return objectMapper.readValue(rawJson, CVExtractionResult.class);
        } catch (JsonProcessingException e) {
            throw new IOException("Lỗi ánh xạ JSON từ AI vào Object Java: " + e.getMessage(), e);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}