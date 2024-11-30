package com.myplan.server.repository;

import com.myplan.server.dto.plan.ResponsePlanDTO;
import com.myplan.server.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {


    @Query("SELECT new com.myplan.server.dto.plan.ResponsePlanDTO(e.id, e.planDate, e.updatedAt, e.createdAt) " +
            "FROM Plan e WHERE e.member.id = :memberId AND e.planDate BETWEEN :startDate AND :endDate")
    List<ResponsePlanDTO> findResponsePlanByStartDateAndEndDate(@Param("memberId") Long memberId,
                                                                @Param("startDate") LocalDate startDate,
                                                                @Param("endDate") LocalDate endDate);


    @Modifying
    @Query("DELETE FROM Plan e WHERE id = :planId AND e.member.id = :memberId")
    void deleteByPlanIdAndMemberId(@Param("planId") Long planId, @Param("memberId") Long memberId);


    Plan findByPlanDateAndMemberId(LocalDate planDate, Long member);
}
