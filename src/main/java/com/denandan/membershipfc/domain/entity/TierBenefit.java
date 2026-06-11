package com.denandan.membershipfc.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tier_benefits", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tier_id", "benefit_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private Tier tier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private Benefit benefit;


    @Column(name = "config_value", nullable = false)
    private String configValue;
}