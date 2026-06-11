package com.denandan.membershipfc.repository;

import com.denandan.membershipfc.domain.entity.UserSubscription;
import com.denandan.membershipfc.domain.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    @EntityGraph(attributePaths = {"subscription", "subscription.baseTier"})
    Optional<UserSubscription> findByUserIdAndSubscriptionStatus(Long userId, SubscriptionStatus status);


    Page<UserSubscription> findByUserId(Long userId, Pageable pageable);
}