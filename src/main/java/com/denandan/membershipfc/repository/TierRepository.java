package com.denandan.membershipfc.repository;

import com.denandan.membershipfc.domain.entity.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TierRepository extends JpaRepository<Tier, Long> {
    Optional<Tier> findByTierName(String tierName);
}