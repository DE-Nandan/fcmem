package com.denandan.membershipfc.domain.entity;


import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "benefits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Benefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefit_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "benefit_code", nullable = false, unique = true)
    private String benefitCode;

    @Column(name = "description", nullable = false ,unique = true)
    private String description;



}