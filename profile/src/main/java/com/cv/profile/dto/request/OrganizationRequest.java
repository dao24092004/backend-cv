package com.cv.profile.dto.request;

import lombok.Data;

public class OrganizationRequest {

    @Data
    public static class RegionRequest {
        private String name;
        private String code;
    }

    @Data
    public static class LocalOrgRequest {
        private String name;
        private String code;
        private Long regionId; // Cần ID cha để biết thuộc vùng nào
    }

    @Data
    public static class DepartmentRequest {
        private String name;
        private String code;
        private Long localOrgId; // Cần ID cha để biết thuộc chi nhánh nào
    }
}