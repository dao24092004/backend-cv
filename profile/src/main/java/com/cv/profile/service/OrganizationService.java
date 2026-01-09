package com.cv.profile.service;

import java.util.List;

import com.cv.profile.dto.request.OrganizationRequest.DepartmentRequest;
import com.cv.profile.dto.request.OrganizationRequest.LocalOrgRequest;
import com.cv.profile.dto.request.OrganizationRequest.RegionRequest;
import com.cv.profile.dto.response.OrganizationResponse.DepartmentResponse;
import com.cv.profile.dto.response.OrganizationResponse.LocalOrgResponse;
import com.cv.profile.dto.response.OrganizationResponse.RegionResponse;
import com.cv.profile.model.Department;
import com.cv.profile.model.LocalOrg;
import com.cv.profile.model.Region;

public interface OrganizationService {
    public List<RegionResponse> getAllRegions();

    public RegionResponse createRegion(RegionRequest req);

    public RegionResponse updateRegion(Long id, RegionRequest req);

    public void deleteRegion(Long id);

    public List<LocalOrgResponse> getAllLocalOrgs();

    public LocalOrgResponse createLocalOrg(LocalOrgRequest req);

    public LocalOrgResponse updateLocalOrg(Long id, LocalOrgRequest req);

    public void deleteLocalOrg(Long id);

    public List<DepartmentResponse> getAllDepartments();

    public DepartmentResponse createDepartment(DepartmentRequest req);

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest req);

    public void deleteDepartment(Long id);

}
