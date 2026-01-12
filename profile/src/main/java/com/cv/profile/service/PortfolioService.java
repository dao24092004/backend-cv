package com.cv.profile.service;

import java.util.List;

import com.cv.profile.dto.response.PortfolioDTO;

public interface PortfolioService {
    PortfolioDTO getPublicPortfolio();

    PortfolioDTO getPortfolioById(Long id);

    List<PortfolioDTO> getAllProfiles();

    public PortfolioDTO getPortfolioWithFullValidation(Long rid, Long lid, Long did, Long pid);

    PortfolioDTO getPortfolioByHierarchyCodes(String rCode, String lCode, String dCode, Long pid);

    // New flexible methods for different hierarchy levels
    PortfolioDTO getPortfolioByRegion(String regionCode, Long pid);
    
    PortfolioDTO getPortfolioByLocalOrg(String regionCode, String localCode, Long pid);
    
    PortfolioDTO getPortfolioByDepartment(String regionCode, String localCode, String deptCode, Long pid);
}
