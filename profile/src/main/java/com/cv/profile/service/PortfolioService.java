package com.cv.profile.service;

import java.util.List;

import com.cv.profile.dto.ai.CVExtractionResult;
import com.cv.profile.dto.response.PortfolioDTO;

public interface PortfolioService {
    PortfolioDTO getPublicPortfolio();

    PortfolioDTO getPortfolioById(Long id);

    List<PortfolioDTO> getAllProfiles();
}
