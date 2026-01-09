package com.cv.profile.dto.response;

import lombok.Builder;
import lombok.Data;

public class OrganizationResponse {

    @Data
    @Builder
    public static class RegionResponse {
        private Long id;
        private String name;
        private String code;
    }

    @Data
    @Builder
    public static class LocalOrgResponse {
        private Long id;
        private String name;
        private String code;
        private Long regionId; // Chỉ trả về ID
        private String regionName; // Trả thêm tên để hiển thị cho tiện
    }

    @Data
    @Builder
    public static class DepartmentResponse {
        private Long id;
        private String name;
        private String code;
        private Long localOrgId;
        private String localOrgName;
    }
}
