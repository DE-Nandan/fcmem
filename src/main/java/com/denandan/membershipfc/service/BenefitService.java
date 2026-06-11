package com.denandan.membershipfc.service;

import com.denandan.membershipfc.domain.entity.TierBenefit;
import com.denandan.membershipfc.domain.entity.UserSubscription;
import com.denandan.membershipfc.domain.enums.SubscriptionStatus;
import com.denandan.membershipfc.repository.TierBenefitRepository;
import com.denandan.membershipfc.repository.UserSubscriptionRepository;
import com.denandan.membershipfc.web.controller.dto.response.ActiveBenefitsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BenefitService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final TierBenefitRepository tierBenefitRepository;

    @Transactional(readOnly = true)
    public ActiveBenefitsResponse getUserBenefits(Long userId) {

        UserSubscription activeSub = userSubscriptionRepository
                .findByUserIdAndSubscriptionStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active membership found for user: " + userId));


        List<TierBenefit> benefits = tierBenefitRepository.findByTier(activeSub.getSubscription().getBaseTier());

        Map<String, String> perksMap = new HashMap<>();

        for (TierBenefit tb : benefits) {
            String code = tb.getBenefit().getBenefitCode();
            String configValue = tb.getConfigValue();
            perksMap.put(code, configValue);
        }

        return ActiveBenefitsResponse.builder()
                .userId(userId)
                .tierName(activeSub.getSubscription().getBaseTier().getTierName())
                .availablePerks(perksMap)
                .build();
    }
}