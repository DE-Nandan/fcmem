package com.denandan.membershipfc.domain.entity;

import com.denandan.membershipfc.domain.enums.SubscriptionPlanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "subscription_name", nullable = false)
    private String subscriptionName;

    @Column(name = "current_price", nullable = false)
    private BigDecimal currentPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false)
    private SubscriptionPlanType subscriptionPlanType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_tier_id", nullable = false)
    private Tier baseTier;

    @Version
    @Column(name = "version")
    private Long version;

}