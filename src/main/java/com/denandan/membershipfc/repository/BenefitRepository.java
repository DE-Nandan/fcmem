package com.denandan.membershipfc.repository;

import com.denandan.membershipfc.domain.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {
}