package com.denandan.membershipfc.web.controller;


import com.denandan.membershipfc.service.BenefitService;
import com.denandan.membershipfc.service.MembershipService;
import com.denandan.membershipfc.web.controller.dto.request.MembershipUpdateRequest;
import com.denandan.membershipfc.web.controller.dto.response.ActiveBenefitsResponse;
import com.denandan.membershipfc.web.controller.dto.response.ActiveMembershipResponse;
import com.denandan.membershipfc.web.controller.dto.response.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/membership")
@RequiredArgsConstructor
public class UserMembershipController {

    private final MembershipService membershipService;
    private final BenefitService benefitService;

    @GetMapping
    public ResponseEntity<ActiveMembershipResponse> getActiveMembership(@PathVariable Long userId) {
        ActiveMembershipResponse response = membershipService.getActiveMembership(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelMembership(@PathVariable Long userId) {
        membershipService.cancelSubscription(userId);

        return ResponseEntity.ok("Membership successfully cancelled.");
    }

    @PostMapping("/update")
    public ResponseEntity<ActiveMembershipResponse> updateMembership(
            @PathVariable Long userId,
            @RequestBody MembershipUpdateRequest request) {

        ActiveMembershipResponse response = membershipService.changeSubscription(userId, request.getNewSubscriptionId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/benefits")
    public ResponseEntity<ActiveBenefitsResponse> getUserBenefits(@PathVariable Long userId) {

        ActiveBenefitsResponse response = benefitService.getUserBenefits(userId);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/history")
    public ResponseEntity<Page<SubscriptionResponse>> getMembershipHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        Page<SubscriptionResponse> response = membershipService.getUserHistory(userId, page, size);
        return ResponseEntity.ok(response);
    }
}