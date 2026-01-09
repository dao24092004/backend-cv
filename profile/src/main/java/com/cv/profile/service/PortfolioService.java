package com.cv.profile.service;

import java.util.List;

import com.cv.profile.dto.ai.CVExtractionResult;
import com.cv.profile.dto.response.PortfolioDTO;

public interface PortfolioService {
    PortfolioDTO getPublicPortfolio();

    PortfolioDTO getPortfolioById(Long id);

    List<PortfolioDTO> getAllProfiles();

    public PortfolioDTO getPortfolioWithFullValidation(Long rid, Long lid, Long did, Long pid);

    PortfolioDTO getPortfolioByHierarchyCodes(String rCode, String lCode, String dCode, Long pid);
}
