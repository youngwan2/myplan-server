package com.myplan.server.repository;

import com.myplan.server.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Plan findByPlanDate(LocalDate planDate);
    Plan findByPlanDateAndMemberId(LocalDate planDate, Long member);
}
