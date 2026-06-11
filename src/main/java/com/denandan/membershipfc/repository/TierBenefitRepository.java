package com.denandan.membershipfc.repository;

import com.denandan.membershipfc.domain.entity.Tier;
import com.denandan.membershipfc.domain.entity.TierBenefit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierBenefitRepository extends JpaRepository<TierBenefit, Long> {
    @EntityGraph(attributePaths = {"benefit"})
    List<TierBenefit> findByTier(Tier tier);
}