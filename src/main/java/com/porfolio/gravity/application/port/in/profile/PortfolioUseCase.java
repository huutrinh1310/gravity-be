package com.porfolio.gravity.application.port.in.profile;

import com.porfolio.gravity.domain.model.Portfolio;

import java.util.List;

public interface PortfolioUseCase {
    List<Portfolio> listPortfolios();

    Portfolio getPortfolio(Integer id);

    Portfolio createPortfolio(CreatePortfolioCommand command);

    Portfolio updatePortfolio(Integer id, UpdatePortfolioCommand command);

    void deletePortfolio(Integer id);
}
