package com.cv.profile.service.impls;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cv.profile.dto.request.OrganizationRequest.DepartmentRequest;
import com.cv.profile.dto.request.OrganizationRequest.LocalOrgRequest;
import com.cv.profile.dto.request.OrganizationRequest.RegionRequest;
import com.cv.profile.dto.response.OrganizationResponse.DepartmentResponse;
import com.cv.profile.dto.response.OrganizationResponse.LocalOrgResponse;
import com.cv.profile.dto.response.OrganizationResponse.RegionResponse;
import com.cv.profile.mapper.PortfolioMapper;
import com.cv.profile.model.Department;
import com.cv.profile.model.LocalOrg;
import com.cv.profile.model.Region;
import com.cv.profile.repository.DepartmentRepository;
import com.cv.profile.repository.LocalOrgRepository;
import com.cv.profile.repository.RegionRepository;
import com.cv.profile.service.OrganizationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
    private final RegionRepository regionRepository;
    private final LocalOrgRepository localOrgRepository;
    private final DepartmentRepository departmentRepository;
    private final PortfolioMapper mapper;

    // ================= REGION (VÙNG) =================
    // Sửa: Trả về List<RegionResponse> thay vì
    public List<RegionResponse> getAllRegions() {
        return regionRepository.findAll().stream()
                .map(r -> RegionResponse.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .code(r.getCode())
                        .build())
                .collect(Collectors.toList());
    }

    public RegionResponse createRegion(RegionRequest req) {
        Region region = new Region();
        region.setName(req.getName());
        region.setCode(req.getCode());
        region = regionRepository.save(region);
        return toRegionDTO(region);
    }

    // Helper convert
    private RegionResponse toRegionDTO(Region r) {
        return RegionResponse.builder().id(r.getId()).name(r.getName()).code(r.getCode()).build();
    }

    // Các hàm update/delete giữ nguyên logic, chỉ đổi kiểu trả về nếu cần
    public void deleteRegion(Long id) {
        regionRepository.deleteById(id);
    }

    public RegionResponse updateRegion(Long id, RegionRequest req) {
        Region region = regionRepository.findById(id).orElseThrow();
        region.setName(req.getName());
        if (req.getCode() != null)
            region.setCode(req.getCode());
        return toRegionDTO(regionRepository.save(region));
    }

    // ================= LOCAL ORG (CHI NHÁNH) =================
    public List<LocalOrgResponse> getAllLocalOrgs() {
        return localOrgRepository.findAll().stream()
                .map(mapper::toLocalOrgResponse)
                .collect(Collectors.toList());
    }

    public LocalOrgResponse createLocalOrg(LocalOrgRequest req) {
        Region region = regionRepository.findById(req.getRegionId())
                .orElseThrow(() -> new RuntimeException("Region ID not found"));
        LocalOrg local = new LocalOrg();
        local.setName(req.getName());
        local.setCode(req.getCode());
        local.setRegion(region);
        return mapper.toLocalOrgResponse(localOrgRepository.save(local));
    }

    public LocalOrgResponse updateLocalOrg(Long id, LocalOrgRequest req) {
        LocalOrg local = localOrgRepository.findById(id).orElseThrow();
        local.setName(req.getName());
        if (req.getRegionId() != null) {
            Region region = regionRepository.findById(req.getRegionId()).orElseThrow();
            local.setRegion(region);
        }
        if (req.getCode() != null)
            local.setCode(req.getCode());
        return mapper.toLocalOrgResponse(localOrgRepository.save(local));
    }

    public void deleteLocalOrg(Long id) {
        localOrgRepository.deleteById(id);
    }

    // private LocalOrgResponse mapper.toLocalOrgResponse(LocalOrg l) {
    // return LocalOrgResponse.builder()
    // .id(l.getId())
    // .name(l.getName())
    // .code(l.getCode())
    // // Xử lý null safety cho Region
    // .regionId(l.getRegion() != null ? l.getRegion().getId() : null)
    // .regionName(l.getRegion() != null ? l.getRegion().getName() : null)
    // .build();
    // }

    // ================= DEPARTMENT (PHÒNG BAN) =================
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(mapper::toDepartmentResponse)
                .collect(Collectors.toList());
    }

    public DepartmentResponse createDepartment(DepartmentRequest req) {
        LocalOrg local = localOrgRepository.findById(req.getLocalOrgId())
                .orElseThrow(() -> new RuntimeException("Local ID not found"));
        Department dept = new Department();
        dept.setName(req.getName());
        dept.setLocalOrg(local);
        dept.setCode(req.getCode());
        return mapper.toDepartmentResponse(departmentRepository.save(dept));
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest req) {
        Department dept = departmentRepository.findById(id).orElseThrow();
        dept.setName(req.getName());
        if (req.getLocalOrgId() != null) {
            LocalOrg local = localOrgRepository.findById(req.getLocalOrgId()).orElseThrow();
            dept.setLocalOrg(local);
        }
        if (req.getCode() != null)
            dept.setCode(req.getCode());
        return mapper.toDepartmentResponse(departmentRepository.save(dept));
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    // private DepartmentResponse mapper.toDepartmentResponse(Department d) {
    // return DepartmentResponse.builder()
    // .id(d.getId())
    // .name(d.getName())
    // .code(d.getCode())
    // .localOrgId(d.getLocalOrg() != null ? d.getLocalOrg().getId() : null)
    // .localOrgName(d.getLocalOrg() != null ? d.getLocalOrg().getName() : null)
    // .build();
    // }
}
