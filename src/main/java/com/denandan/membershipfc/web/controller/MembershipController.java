package com.denandan.membershipfc.web.controller;

import com.denandan.membershipfc.service.MembershipService;
import com.denandan.membershipfc.web.controller.dto.request.SubscriptionRequest;
import com.denandan.membershipfc.web.controller.dto.response.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/subscribe")
    public ResponseEntity<SubscriptionResponse> subscribe(@RequestHeader(value = "Idempotency-Key") String idempotencyKey,
                                                          @RequestBody SubscriptionRequest request) {


        SubscriptionResponse response = membershipService.subscribeUserWithIdempotency(idempotencyKey,request);


        return ResponseEntity.ok(response);
    }

}