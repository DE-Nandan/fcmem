package com.denandan.membershipfc.web.controller;

import com.denandan.membershipfc.service.MembershipService;
import com.denandan.membershipfc.web.controller.dto.response.MembershipPlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membership-plans")
@RequiredArgsConstructor
public class MembershipCatalogController {

    private final MembershipService membershipService;

    @GetMapping
    public ResponseEntity<List<MembershipPlanResponse>> getMembershipPlans() {

        List<MembershipPlanResponse> plans = membershipService.getAvailablePlans();
        return ResponseEntity.ok(plans);

    }
}