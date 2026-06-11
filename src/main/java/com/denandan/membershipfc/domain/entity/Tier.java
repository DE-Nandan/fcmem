package com.denandan.membershipfc.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tier_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "tier_name", nullable = false, unique = true)
    private String tierName;

    @Column(name = "priority_level", nullable = false ,unique = true)
    private Integer priorityLevel;

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TierBenefit> tierBenefits = new ArrayList<>();

}