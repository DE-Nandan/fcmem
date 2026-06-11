package com.denandan.membershipfc;

import com.denandan.membershipfc.domain.entity.*;
import com.denandan.membershipfc.domain.enums.SubscriptionPlanType;
import com.denandan.membershipfc.domain.enums.SubscriptionStatus;
import com.denandan.membershipfc.repository.*;
import com.denandan.membershipfc.service.BenefitService;
import com.denandan.membershipfc.service.TierEvaluationService;
import com.denandan.membershipfc.web.controller.dto.request.UserMetricsSummary;
import com.denandan.membershipfc.web.controller.dto.response.ActiveBenefitsResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class MembershipfcApplication {

	@Bean
	public CommandLineRunner loadTestData(
            UserRepository userRepo,
            TierRepository tierRepo,
            SubscriptionRepository subRepo,
            UserSubscriptionRepository userSubscriptionRepo,
            TierEvaluationService tierEvaluationService,
            BenefitRepository benefitRepo,
            TierBenefitRepository tierBenefitRepo,
            BenefitService benefitService) {

		return args -> {


			Tier silverTier = Tier.builder().tierName("SILVER").priorityLevel(1).build();
			Tier goldTier = Tier.builder().tierName("GOLD").priorityLevel(2).build();
			Tier platinumTier = Tier.builder().tierName("PLATINUM").priorityLevel(3).build();
			tierRepo.saveAll(List.of(silverTier, goldTier, platinumTier));


			Subscription monthlySilver = Subscription.builder()
					.subscriptionName("Monthly Silver Plan")
					.subscriptionPlanType(SubscriptionPlanType.MONTHLY)
					.currentPrice(new BigDecimal("19.99"))
					.baseTier(silverTier).build();

			Subscription monthlyGold = Subscription.builder()
					.subscriptionName("Monthly Gold Plan")
					.subscriptionPlanType(SubscriptionPlanType.MONTHLY)
					.currentPrice(new BigDecimal("29.99"))
					.baseTier(goldTier).build();

			Subscription monthlyPlatinum = Subscription.builder()
					.subscriptionName("Monthly Platinum Plan")
					.subscriptionPlanType(SubscriptionPlanType.MONTHLY)
					.currentPrice(new BigDecimal("49.99"))
					.baseTier(platinumTier).build();

			subRepo.saveAll(List.of(monthlySilver, monthlyGold, monthlyPlatinum));


			Benefit freeShipping = Benefit.builder()
					.benefitCode("FREE_SHIPPING")
					.description("Waives all standard shipping fees at checkout")
					.build();

			Benefit extraDiscount = Benefit.builder()
					.benefitCode("EXTRA_DISCOUNT")
					.description("Additional percentage off eligible cart items")
					.build();

			Benefit prioritySupport = Benefit.builder()
					.benefitCode("PRIORITY_SUPPORT")
					.description("Access to the 24/7 VIP customer service queue")
					.build();

			benefitRepo.saveAll(List.of(freeShipping, extraDiscount, prioritySupport));
			benefitRepo.saveAll(List.of(freeShipping, extraDiscount, prioritySupport));


			tierBenefitRepo.save(TierBenefit.builder().tier(silverTier).benefit(freeShipping).configValue("FALSE").build());
			tierBenefitRepo.save(TierBenefit.builder().tier(silverTier).benefit(extraDiscount).configValue("0").build());


			tierBenefitRepo.save(TierBenefit.builder().tier(goldTier).benefit(freeShipping).configValue("TRUE").build());
			tierBenefitRepo.save(TierBenefit.builder().tier(goldTier).benefit(extraDiscount).configValue("10").build());


			tierBenefitRepo.save(TierBenefit.builder().tier(platinumTier).benefit(freeShipping).configValue("TRUE").build());
			tierBenefitRepo.save(TierBenefit.builder().tier(platinumTier).benefit(extraDiscount).configValue("15").build());
			tierBenefitRepo.save(TierBenefit.builder().tier(platinumTier).benefit(prioritySupport).configValue("TRUE").build());


			User testUser = User.builder().userName("Nandan Kumar").build();
			userRepo.save(testUser);

			UserSubscription startingSubscription = UserSubscription.builder()
					.user(testUser)
					.subscription(monthlySilver)
					.subscriptionStatus(SubscriptionStatus.ACTIVE)
					.startDate(LocalDateTime.now())
					.expiryDate(LocalDateTime.now().plusMonths(1))
					.lockedInPrice(monthlySilver.getCurrentPrice())
					.build();
			userSubscriptionRepo.save(startingSubscription);

			System.out.println("TEST DATA LOADED SUCCESSFULLY!");
			System.out.println("Current Tier: " + startingSubscription.getSubscription().getBaseTier().getTierName());


			System.out.println("SIMULATING HIGH-VALUE CHECKOUT EVENT...");
			UserMetricsSummary simulatedMetrics = UserMetricsSummary.builder()
					.userId(testUser.getId())
					.totalOrders(3)
					.monthlySpend(new BigDecimal("1200.00"))
					.cohortName("REGULAR")
					.build();
			tierEvaluationService.evaluateAndUpgradeTier(simulatedMetrics);

			System.out.println("E-COMMERCE ENGINE CALLING BENEFITS API...");

			ActiveBenefitsResponse response = benefitService.getUserBenefits(testUser.getId());

			System.out.println("FINAL CHECKOUT PERKS APPLIED FOR USER:");
			System.out.println("   -> Tier Recognized: " + response.getTierName());
			response.getAvailablePerks().forEach((code, value) ->
					System.out.println("   -> " + code + ": " + value)
			);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(MembershipfcApplication.class, args);
	}

}
