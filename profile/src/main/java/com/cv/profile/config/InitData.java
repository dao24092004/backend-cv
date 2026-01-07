package com.cv.profile.config;

import com.cv.profile.model.*;
import com.cv.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitData implements CommandLineRunner {

        private final ProfileRepository profileRepository;

        @Override
        @Transactional
        public void run(String... args) {

                // 1. Tránh duplicate dữ liệu
                if (profileRepository.count() > 0) {
                        System.out.println("Dữ liệu đã tồn tại. Bỏ qua init data.");
                        return;
                }

                System.out.println("Đang khởi tạo dữ liệu CV Song Ngữ cho Phạm Minh Đạo...");

                // =========================
                // 2. PROFILE
                // =========================
                Profile profile = new Profile();
                profile.setFullName("Phạm Minh Đạo");
                profile.setEmail("phamminhdao26@gmail.com");
                profile.setPhone("0374179528");
                profile.setAvatarUrl("https://i.imgur.com/avatar-dao.png");
                profile.setGithub("https://github.com/dao24092004");
                profile.setLinkedin("https://linkedin.com/in/phamminhdao24");

                // --- Song Ngữ ---
                profile.setJobTitleVi("Fresher Java Developer");
                profile.setJobTitleEn("Fresher Java Developer");

                profile.setAddressVi("Hồ Chí Minh, Việt Nam");
                profile.setAddressEn("Ho Chi Minh City, Vietnam");

                profile.setBioVi(
                                "Sinh viên năm 4 ngành Công nghệ Thông tin, định hướng Fresher Java Developer. " +
                                                "Có kinh nghiệm thực tế phát triển Backend với Java Spring Boot, RESTful API, JWT, "
                                                +
                                                "làm việc với MySQL, MongoDB, Oracle và tham gia các dự án thực tế, nghiên cứu DevSecOps.");
                profile.setBioEn(
                                "4th-year IT student aspiring to be a Fresher Java Developer. " +
                                                "Practical experience in Backend development with Java Spring Boot, RESTful API, JWT, "
                                                +
                                                "working with MySQL, MongoDB, Oracle, participating in real-world projects, and researching DevSecOps.");

                // =========================
                // 3. EXPERIENCE
                // =========================
                Experience techbyte = new Experience();
                techbyte.setCompanyName("Công ty TNHH Đầu tư Công nghệ Techbyte");

                techbyte.setPositionVi("Backend Developer");
                techbyte.setPositionEn("Backend Developer");

                techbyte.setDescriptionVi(
                                "Phát triển và bảo trì hệ thống Backend với Java Spring Boot. " +
                                                "Thiết kế RESTful API cho quản lý người dùng, sản phẩm, công việc. " +
                                                "Áp dụng JWT cho xác thực và phân quyền. Làm việc với MySQL, MongoDB, Git/GitHub.");
                techbyte.setDescriptionEn(
                                "Developed and maintained Backend systems using Java Spring Boot. " +
                                                "Designed RESTful APIs for user, product, and task management. " +
                                                "Implemented JWT for authentication and authorization. Worked with MySQL, MongoDB, Git/GitHub.");

                techbyte.setStartDate(LocalDate.of(2025, 2, 1));
                techbyte.setEndDate(LocalDate.of(2025, 10, 1));
                techbyte.setIsCurrent(false);
                techbyte.setProfile(profile);

                profile.getExperiences().add(techbyte);

                // =========================
                // 4. EDUCATION
                // =========================
                Education university = new Education();
                university.setSchoolName("Đại học Điện Lực"); // Tên trường thường giữ nguyên hoặc dịch tuỳ ý

                university.setDegreeVi("Cử nhân Công nghệ Thông tin");
                university.setDegreeEn("Bachelor of Information Technology");

                university.setDescriptionVi("GPA: 3.49/4.0. Hoàn thành các môn Java, OOP, Cấu trúc dữ liệu (điểm A).");
                university.setDescriptionEn("GPA: 3.49/4.0. Completed Java, OOP, Data Structures courses (Grade A).");

                university.setStartDate(LocalDate.of(2022, 9, 1));
                university.setEndDate(LocalDate.of(2027, 6, 1));
                university.setProfile(profile);

                Education aptech = new Education();
                aptech.setSchoolName("Bachkhoa Education Aptech");

                aptech.setDegreeVi("Full Stack Java Web Application Plus");
                aptech.setDegreeEn("Full Stack Java Web Application Plus");

                aptech.setDescriptionVi("Học chuyên sâu Java Web: Servlet, JSP, Spring Framework, Spring Boot.");
                aptech.setDescriptionEn("Deep dive into Java Web: Servlet, JSP, Spring Framework, Spring Boot.");

                aptech.setStartDate(LocalDate.of(2023, 6, 1));
                aptech.setEndDate(LocalDate.of(2024, 12, 1));
                aptech.setProfile(profile);

                // LƯU Ý: Đã đổi getEducations() -> getEducation() (số ít)
                profile.getEducation().addAll(List.of(university, aptech));

                // =========================
                // 5. SKILLS (Giữ nguyên vì Skill không chia cột Vi/En)
                // =========================
                profile.getSkills().addAll(List.of(
                                new Skill(null, "Java Core", "Backend", 90, profile),
                                new Skill(null, "Spring Boot / Spring MVC", "Backend", 90, profile),
                                new Skill(null, "Spring Security / Hibernate", "Backend", 85, profile),
                                new Skill(null, "Kafka / Redis", "Backend", 75, profile),
                                new Skill(null, "Oracle SQL", "Database", 90, profile),
                                new Skill(null, "MySQL / MongoDB", "Database", 80, profile),
                                new Skill(null, "ReactJS", "Frontend", 75, profile),
                                new Skill(null, "Microservices", "Architecture", 65, profile),
                                new Skill(null, "Design Patterns", "Software Design", 65, profile)));

                // =========================
                // 6. PROJECTS
                // =========================
                Project ai2 = new Project();
                ai2.setCustomer("Techbyte");
                ai2.setTechStack("Java Spring Boot, React Native, MySQL, JWT");
                ai2.setSourceCodeUrl("https://github.com/dao24092004/ai2");

                ai2.setNameVi("AI2.vn – Nền tảng bán biển quảng cáo");
                ai2.setNameEn("AI2.vn – Billboard Advertising Platform");

                ai2.setRoleVi("Full-stack Developer");
                ai2.setRoleEn("Full-stack Developer");

                ai2.setDescriptionVi(
                                "Xây dựng hệ thống quản lý người dùng, sản phẩm biển quảng cáo. Phát triển RESTful API, JWT Authentication.");
                ai2.setDescriptionEn(
                                "Built user and billboard product management system. Developed RESTful APIs, JWT Authentication.");

                ai2.setProfile(profile);

                Project studentApp = new Project();
                studentApp.setCustomer("Đề tài nghiên cứu khoa học");
                studentApp.setTechStack("ReactJS, Spring Boot, PostgreSQL, Redis, Docker, Kubernetes");
                studentApp.setSourceCodeUrl("https://github.com/dao24092004/student-app");

                studentApp.setNameVi("StudentApp – Ứng dụng Quản lý Sinh viên");
                studentApp.setNameEn("StudentApp – Student Management Application");

                studentApp.setRoleVi("Full-stack Developer");
                studentApp.setRoleEn("Full-stack Developer");

                studentApp.setDescriptionVi(
                                "Ứng dụng quản lý sinh viên, đăng ký môn học, học phí. Tích hợp CI/CD, DevSecOps.");
                studentApp.setDescriptionEn(
                                "Student management app for course registration and tuition. Integrated CI/CD, DevSecOps.");

                studentApp.setProfile(profile);

                Project ai7 = new Project();
                ai7.setCustomer("Techbyte");
                ai7.setTechStack("Java Spring Boot, MongoDB");
                ai7.setSourceCodeUrl("https://github.com/dao24092004/ai7");

                ai7.setNameVi("AI7.vn – Nền tảng chống thất nghiệp");
                ai7.setNameEn("AI7.vn – Anti-Unemployment Platform");

                ai7.setRoleVi("Backend Developer");
                ai7.setRoleEn("Backend Developer");

                ai7.setDescriptionVi(
                                "Phát triển API quản lý công việc: tạo, phân công, theo dõi tiến độ. Xử lý nghiệp vụ với MongoDB.");
                ai7.setDescriptionEn(
                                "Developed APIs for task management: creation, assignment, progress tracking. Business logic with MongoDB.");

                ai7.setProfile(profile);

                profile.getProjects().addAll(List.of(ai2, studentApp, ai7));

                // =========================
                // 7. EVENT / RESEARCH
                // =========================
                Event research = new Event();
                research.setDate(LocalDate.of(2025, 3, 1));

                research.setNameVi("Nghiên cứu khoa học DevSecOps");
                research.setNameEn("DevSecOps Scientific Research");

                research.setRole("Thành viên nghiên cứu"); // Role ngắn có thể giữ chung hoặc chia cột nếu muốn

                research.setDescriptionVi(
                                "Đề tài: Ứng dụng DevSecOps trong hệ thống quản lý sinh viên. Đạt 99.8% uptime.");
                research.setDescriptionEn(
                                "Topic: Applying DevSecOps in cloud-based student management systems. Achieved 99.8% uptime.");

                research.setProfile(profile);

                profile.getEvents().add(research);

                // =========================
                // 8. SAVE
                // =========================
                profileRepository.save(profile);

                System.out.println("Khởi tạo dữ liệu CV thành công cho Phạm Minh Đạo!");
        }
}