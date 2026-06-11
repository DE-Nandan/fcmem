package com.denandan.membershipfc.web.controller.dto.request;

import lombok.Data;

@Data
public class SubscriptionUpdateRequest {
    private Long userId;
    private Long subscriptionId;
}
