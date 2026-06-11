package com.denandan.membershipfc.web.controller.dto.request;

import lombok.Data;

@Data
public class SubscriptionRequest {
    private Long userId;
    private Long subscriptionId;
}