package com.denandan.membershipfc.service;

import com.denandan.membershipfc.domain.entity.Subscription;
import com.denandan.membershipfc.domain.entity.Tier;
import com.denandan.membershipfc.domain.entity.User;
import com.denandan.membershipfc.domain.entity.UserSubscription;
import com.denandan.membershipfc.domain.enums.SubscriptionStatus;
import com.denandan.membershipfc.repository.SubscriptionRepository;
import com.denandan.membershipfc.repository.TierRepository;
import com.denandan.membershipfc.repository.UserSubscriptionRepository;
import com.denandan.membershipfc.strategy.TierEvaluationStrategy;
import com.denandan.membershipfc.web.controller.dto.request.UserMetricsSummary;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class TierEvaluationService {
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final TierRepository tierRepository;
    private final SubscriptionRepository subscriptionRepository;

    private final List<TierEvaluationStrategy> evaluationStrategies;

    public TierEvaluationService(UserSubscriptionRepository userSubscriptionRepository,
                                 TierRepository tierRepository,
                                 List<TierEvaluationStrategy> tierEvaluationStrategies,
                                 SubscriptionRepository subscriptionRepository)
    {
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.tierRepository = tierRepository;

        this.evaluationStrategies = tierEvaluationStrategies;
        this.subscriptionRepository = subscriptionRepository;

        evaluationStrategies.sort((o1,o2) -> {
            return Integer.compare(o2.getEvaluationOrder(),o1.getEvaluationOrder());
        } );
    }

    @Transactional
    public void evaluateAndUpgradeTier(UserMetricsSummary userMetricsSummary)
    {
        UserSubscription userSubscription = userSubscriptionRepository.
                findByUserIdAndSubscriptionStatus(userMetricsSummary.getUserId(), SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (userSubscription == null) {
            log.info("User {} has no active subscription to upgrade.", userMetricsSummary.getUserId());
            return;
        }

        Tier currentTier = userSubscription.getSubscription().getBaseTier();

        String targetTierName = calculate(userMetricsSummary);


        Tier targetTier = tierRepository.findByTierName(targetTierName)
                .orElseThrow(() -> new RuntimeException("Tier configuration missing: " + targetTierName));


        if (targetTier.getPriorityLevel() > currentTier.getPriorityLevel()) {

            log.info("Upgrading User {} from {} to {}!",
                    userMetricsSummary.getUserId(), currentTier.getTierName(), targetTier.getTierName());

            Subscription newPlan = subscriptionRepository.findByBaseTier(targetTier)
                    .stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No catalog plan exists for tier: " + targetTierName));


            userSubscription.setSubscriptionStatus(SubscriptionStatus.UPGRADED);
            userSubscription.setExpiryDate(LocalDateTime.now());
            userSubscriptionRepository.save(userSubscription);

            UserSubscription newContract = UserSubscription.builder()
                    .user(userSubscription.getUser())
                    .subscription(newPlan)
                    .subscriptionStatus(SubscriptionStatus.ACTIVE)
                    .startDate(LocalDateTime.now())
                    .expiryDate(LocalDateTime.now().plusMonths(1))
                    .lockedInPrice(newPlan.getCurrentPrice())
                    .build();
            userSubscriptionRepository.save(newContract);

        } else {
            log.info("User {} remains on Tier {}.", userMetricsSummary.getUserId(), currentTier.getTierName());
        }

    }

    private String calculate(UserMetricsSummary metricsSummary)
    {
        for(var strategy : evaluationStrategies)
        {
            if(strategy.isEligible(metricsSummary))
            {
                return strategy.grantedTier();
            }
        }

        return "SILVER";
    }
}
