package com.denandan.membershipfc.repository;

import com.denandan.membershipfc.domain.entity.Benefit;
import com.denandan.membershipfc.domain.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, String> {
}