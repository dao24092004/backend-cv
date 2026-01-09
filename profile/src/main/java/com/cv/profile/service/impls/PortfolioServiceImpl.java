package com.cv.profile.service.impls;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cv.profile.dto.response.ContactDTO;
import com.cv.profile.dto.response.EducationDTO;
import com.cv.profile.dto.response.EventDTO;
import com.cv.profile.dto.response.ExperienceDTO;
import com.cv.profile.dto.response.PortfolioDTO;
import com.cv.profile.dto.response.ProjectDTO;
import com.cv.profile.dto.response.PublicationDTO;
import com.cv.profile.dto.response.SkillDTO;
import com.cv.profile.mapper.PortfolioMapper;
import com.cv.profile.model.Education;
import com.cv.profile.model.Event;
import com.cv.profile.model.Experience;
import com.cv.profile.model.Profile;
import com.cv.profile.model.Project;
import com.cv.profile.model.Publication;
import com.cv.profile.model.Skill;
import com.cv.profile.repository.ProfileRepository;
import com.cv.profile.service.PortfolioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

        private final ProfileRepository profileRepository;
        private final PortfolioMapper mapper;

        @Override
        @Transactional(readOnly = true)
        public PortfolioDTO getPublicPortfolio() {
                // 1. Tìm hồ sơ đang ACTIVE (được Admin chọn hiển thị)
                Optional<Profile> activeProfile = profileRepository.findByIsActiveTrue();

                // 2. Nếu có người Active -> Trả về người đó
                if (activeProfile.isPresent()) {
                        return mapper.toPortfolioDTO(activeProfile.get());
                }

                // 3. Nếu KHÔNG có ai Active -> Lấy người đầu tiên trong DB (Fallback)
                Profile fallback = profileRepository.findAll().stream().findFirst()
                                .orElseThrow(() -> new RuntimeException("Hệ thống chưa có dữ liệu CV nào!"));

                return mapper.toPortfolioDTO(fallback);
        }

        @Override
        @Transactional(readOnly = true)
        public PortfolioDTO getPortfolioById(Long id) {
                Profile entity = profileRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy Profile ID: " + id));

                return mapper.toPortfolioDTO(entity); // <--- Tái sử dụng
        }

        @Override
        @Transactional(readOnly = true)
        public List<PortfolioDTO> getAllProfiles() {
                return profileRepository.findAll().stream()
                                // Với list tổng quan, ta cũng dùng mapper (hoặc viết hàm mapper riêng cho list
                                // nếu muốn nhẹ hơn)
                                .map(mapper::toPortfolioDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public PortfolioDTO getPortfolioWithFullValidation(Long rid, Long lid, Long did, Long pid) {
                // 1. Tìm Portfolio (Đã được tối ưu JOIN nhờ @EntityGraph)
                Profile entity = profileRepository.findById(pid)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên ID: " + pid));

                // 2. Validate dữ liệu 4 cấp
                if (entity.getDepartment() == null || !entity.getDepartment().getId().equals(did)) {
                        throw new RuntimeException("Nhân viên không thuộc Phòng ban ID: " + did);
                }
                if (entity.getDepartment().getLocalOrg() == null
                                || !entity.getDepartment().getLocalOrg().getId().equals(lid)) {
                        throw new RuntimeException("Phòng ban không thuộc Chi nhánh ID: " + lid);
                }
                if (entity.getDepartment().getLocalOrg().getRegion() == null
                                || !entity.getDepartment().getLocalOrg().getRegion().getId().equals(rid)) {
                        throw new RuntimeException("Chi nhánh không thuộc Vùng ID: " + rid);
                }

                return mapper.toPortfolioDTO(entity);
        }

        @Override
        public PortfolioDTO getPortfolioByHierarchyCodes(String rCode, String lCode, String dCode, Long pid) {
                // 1. Tìm nhân viên bằng ID (Đây là cái duy nhất chính xác tuyệt đối)
                // Dùng EntityGraph để load luôn các bảng cha
                Profile entity = profileRepository.findById(pid)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ ID: " + pid));

                // 2. Validate ngược từ dưới lên trên (Entity -> Dept -> Local -> Region)

                // Kiểm tra Phòng ban (Department)
                if (entity.getDepartment() == null ||
                                !entity.getDepartment().getCode().equalsIgnoreCase(dCode)) {
                        throw new RuntimeException("Nhân viên này không thuộc phòng ban có mã: " + dCode);
                }

                // Kiểm tra Chi nhánh (Local Org)
                if (entity.getDepartment().getLocalOrg() == null ||
                                !entity.getDepartment().getLocalOrg().getCode().equalsIgnoreCase(lCode)) {
                        throw new RuntimeException("Phòng ban này không thuộc chi nhánh có mã: " + lCode);
                }

                // Kiểm tra Vùng (Region)
                if (entity.getDepartment().getLocalOrg().getRegion() == null ||
                                !entity.getDepartment().getLocalOrg().getRegion().getCode().equalsIgnoreCase(rCode)) {
                        throw new RuntimeException("Chi nhánh này không thuộc vùng có mã: " + rCode);
                }

                // 3. Nếu mọi thứ khớp Code -> Trả về DTO
                return mapper.toPortfolioDTO(entity);
        }
}
