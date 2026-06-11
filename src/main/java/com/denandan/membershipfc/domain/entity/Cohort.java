package com.denandan.membershipfc.domain.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cohorts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cohort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cohort_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "cohort_name", nullable = false, unique = true)
    private String cohortName;

}