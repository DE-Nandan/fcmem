package com.denandan.membershipfc.repository;

import com.denandan.membershipfc.domain.entity.Subscription;
import com.denandan.membershipfc.domain.entity.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByBaseTier(Tier baseTier);
}