package com.denandan.membershipfc.service;

import com.denandan.membershipfc.domain.entity.IdempotencyRecord;
import com.denandan.membershipfc.domain.entity.Subscription;
import com.denandan.membershipfc.domain.entity.User;
import com.denandan.membershipfc.domain.entity.UserSubscription;
import com.denandan.membershipfc.domain.enums.SubscriptionPlanType;
import com.denandan.membershipfc.domain.enums.SubscriptionStatus;
import com.denandan.membershipfc.repository.IdempotencyRepository;
import com.denandan.membershipfc.repository.SubscriptionRepository;
import com.denandan.membershipfc.repository.UserRepository;
import com.denandan.membershipfc.repository.UserSubscriptionRepository;
import com.denandan.membershipfc.web.controller.dto.request.MembershipUpdateRequest;
import com.denandan.membershipfc.web.controller.dto.request.SubscriptionRequest;
import com.denandan.membershipfc.web.controller.dto.response.ActiveMembershipResponse;
import com.denandan.membershipfc.web.controller.dto.response.MembershipPlanResponse;
import com.denandan.membershipfc.web.controller.dto.response.SubscriptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.denandan.membershipfc.domain.enums.SubscriptionPlanType.MONTHLY;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final IdempotencyRepository idempotencyRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public SubscriptionResponse subscribeUserWithIdempotency(String idempotencyKey, SubscriptionRequest request) {


        Optional<IdempotencyRecord> existingRecord = idempotencyRepo.findById(idempotencyKey);

        if (existingRecord.isPresent()) {
            log.info("Duplicate request detected for key {}. Returning cached response.", idempotencyKey);
            try {

                return objectMapper.readValue(existingRecord.get().getResponsePayload(), SubscriptionResponse.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse cached response");
            }
        }

        SubscriptionResponse newResponse = this.subscribeUser(request);

        try {
            String jsonResponse = objectMapper.writeValueAsString(newResponse);

            IdempotencyRecord newRecord = IdempotencyRecord.builder()
                    .idempotencyKey(idempotencyKey)
                    .responsePayload(jsonResponse)
                    .createdAt(LocalDateTime.now())
                    .build();

            idempotencyRepo.save(newRecord);

        } catch (Exception e) {
            log.error("Failed to serialize response for idempotency cache");
        }

        return newResponse;
    }


    public SubscriptionResponse subscribeUser(SubscriptionRequest subscriptionRequest)
    {
        User user = userRepository.findById(subscriptionRequest.getUserId()).
                orElseThrow(() -> new RuntimeException("User not found wit ID "+ subscriptionRequest.getUserId()));

        Subscription subscription = subscriptionRepository.findById(subscriptionRequest.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription plan not found for this id "+subscriptionRequest.getSubscriptionId()));


        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime expiryDate = calculateExpiryDate(startDate, subscription.getSubscriptionPlanType());


        UserSubscription userSubscription = UserSubscription.builder().user(user).subscription(subscription)
                .startDate(startDate).expiryDate(expiryDate).lockedInPrice(subscription.getCurrentPrice())
                .subscriptionStatus(SubscriptionStatus.ACTIVE).build();


        UserSubscription savedRecord =  userSubscriptionRepository.save(userSubscription);

        return SubscriptionResponse.builder()
                .userSubscriptionId(savedRecord.getId())
                .catalogPlanId(subscription.getId())
                .planName(subscription.getSubscriptionPlanType().name())
                .tierName(subscription.getBaseTier().getTierName())
                .validUntil(savedRecord.getExpiryDate())
                .status(savedRecord.getSubscriptionStatus().name())
                .build();

    }

    @Transactional(readOnly = true)
    public ActiveMembershipResponse getActiveMembership(Long userId)
    {
        UserSubscription activeSubscription = userSubscriptionRepository.
                findByUserIdAndSubscriptionStatus(userId,SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active membership found for user: " + userId));

        return ActiveMembershipResponse.builder()
                .planName(activeSubscription.getSubscription().getSubscriptionPlanType().name())
                .tierName(activeSubscription.getSubscription().getBaseTier().getTierName())
                .validUntil(activeSubscription.getExpiryDate())
                .status(activeSubscription.getSubscriptionStatus().name())
                .build();
    }

    @Transactional
    public void cancelSubscription(Long userId) {


        UserSubscription activeSubscription = userSubscriptionRepository
                .findByUserIdAndSubscriptionStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active membership found to cancel for user: " + userId));

        activeSubscription.setSubscriptionStatus(SubscriptionStatus.CANCELLED);

        userSubscriptionRepository.save(activeSubscription);
    }

    @Transactional
    public ActiveMembershipResponse changeSubscription(Long userId,Long subscriptionId) {


        UserSubscription activeSubscription = userSubscriptionRepository
                .findByUserIdAndSubscriptionStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active membership found to cancel for user: " + userId));

        activeSubscription.setSubscription(subscriptionRepository.getReferenceById(subscriptionId));

        userSubscriptionRepository.save(activeSubscription);

        return ActiveMembershipResponse.builder()
                .planName(activeSubscription.getSubscription().getSubscriptionPlanType().name())
                .tierName(activeSubscription.getSubscription().getBaseTier().getTierName())
                .validUntil(activeSubscription.getExpiryDate())
                .status(activeSubscription.getSubscriptionStatus().name())
                .build();
    }



    public List<MembershipPlanResponse> getAvailablePlans()
    {
        List<Subscription> subscriptionList =  subscriptionRepository.findAll();
        List<MembershipPlanResponse> responseList = new ArrayList<>();

        for(var subscription : subscriptionList)
        {
            MembershipPlanResponse response = MembershipPlanResponse.builder()
                    .subscriptionId(subscription.getId())
                    .planName(subscription.getSubscriptionPlanType().name())
                    .price(subscription.getCurrentPrice())
                    .tierName(subscription.getBaseTier().getTierName())
                    .build();

            responseList.add(response);
        }

        return responseList;

    }

    public Page<SubscriptionResponse> getUserHistory(Long userId, int page, int size) {


        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("startDate").descending());


        Page<UserSubscription> historyPage = userSubscriptionRepository.findByUserId(userId, pageRequest);


        return historyPage.map(sub -> SubscriptionResponse.builder()
                .userSubscriptionId(sub.getId())
                .catalogPlanId(sub.getSubscription().getId())
                .tierName(sub.getSubscription().getBaseTier().getTierName())
                .planName(sub.getSubscription().getSubscriptionPlanType().name())
                .status(sub.getSubscriptionStatus().name())
                .validUntil(sub.getExpiryDate())
                .build());
    }



    private LocalDateTime calculateExpiryDate(LocalDateTime startDate, SubscriptionPlanType subscriptionPlanType) {
        return switch (subscriptionPlanType) {
            case MONTHLY -> startDate.plusMonths(1);
            case QUATERLY -> startDate.plusMonths(3);
            case  YEARLY -> startDate.minusYears(1);
        };
    }
}
