package com.myplan.server.controller;

import com.myplan.server.config.response.ApiResponse;
import com.myplan.server.dto.PlanDTO;
import com.myplan.server.jwt.JwtUtil;
import com.myplan.server.model.Plan;
import com.myplan.server.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}")
public class PlanController {
    private final PlanService planService;
    private final JwtUtil jwtUtil;


    // 플랜 전체 조회
    public void findAll(){ }

    // 주차별 조회
    public void findByWeekday(){}

    // 일자별 조회
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<Plan>> findByDate(@PathVariable("userId") Long id, @RequestParam("searchDate") LocalDate startDate){
        Plan plan = planService.findAllBySearchDateAndId(id, startDate);
        return ApiResponse.success(plan, "성공적으로 조회하였습니다.", HttpStatus.OK);
    }

    // 플랜 추가
    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<Plan>> addPlan(@RequestBody @Valid PlanDTO planDTO, @PathVariable("userId") Long userId ){
        Plan newPlan = planService.addPlan(planDTO, userId);
        return ApiResponse.success(newPlan, "성공적으로 추가되었습니다.", HttpStatus.OK);
    }

    // 특정 플랜 삭제
    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<ApiResponse<Void>> deletePlanById(@PathVariable Long planId){
        planService.deletePlan(planId);

        return ApiResponse.success("성공적으로 삭제되었습니다.", HttpStatus.NO_CONTENT);
    }

    // 특정 플랜 수정
    public ResponseEntity<ApiResponse<Plan>> updatePlanDateById(@RequestBody PlanDTO planDTO, @PathVariable Long planId){
        Plan plan = planService.updatePlan(planId, planDTO);

        return ApiResponse.success(plan, "성공적으로 수정되었습니다.", HttpStatus.OK );
    }

}
