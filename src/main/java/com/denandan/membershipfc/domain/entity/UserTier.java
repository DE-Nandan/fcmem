package com.denandan.membershipfc.domain.entity;

import com.denandan.membershipfc.domain.enums.SubscriptionStatus;
import com.denandan.membershipfc.domain.enums.TierStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private Tier tier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TierStatus tierStatus;

    @Column(name = "user_name", nullable = false)
    private LocalDateTime lastEvaluationDate;

}