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
}
