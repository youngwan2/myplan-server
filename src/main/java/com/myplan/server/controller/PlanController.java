package com.myplan.server.controller;

import com.myplan.server.config.response.ApiResponse;
import com.myplan.server.dto.plan.RequestPlanDTO;
import com.myplan.server.dto.plan.ResponsePlanDTO;
import com.myplan.server.util.AuthenticationFacade;
import com.myplan.server.util.JwtUtil;
import com.myplan.server.model.Plan;
import com.myplan.server.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PlanController {
    private final PlanService planService;


    // 기간별 플랜 조회
    @GetMapping("/plans/{month}")
    public ResponseEntity<ApiResponse<List<ResponsePlanDTO>>> findPlanByDateRange(@PathVariable("month") int month){
        return ApiResponse.success(planService.findPlanByDateRange(month), "전송", HttpStatus.OK);
    }


    // 특정 날짜의 플랜 조회
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<ResponsePlanDTO>> findByDate(@RequestParam("planDate") LocalDate planDate) {

        log.info("PlanDate: {}", planDate);
        ResponsePlanDTO plan = planService.findByDate(planDate);
        return ApiResponse.success(plan, "성공적으로 조회하였습니다.", HttpStatus.OK);
    }

    // 플랜 추가
    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<ResponsePlanDTO>> addPlan(@RequestBody @Valid RequestPlanDTO planDTO) {
        ResponsePlanDTO plan = planService.addPlan(planDTO);
        return ApiResponse.success(plan, "성공적으로 추가되었습니다.", HttpStatus.OK);
    }

    // 특정 플랜 삭제
    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<ApiResponse<Long>> deletePlanById(@PathVariable("planId") Long planId) {

        return ApiResponse.success(planService.deletePlan(planId), "성공적으로 삭제되었습니다.", HttpStatus.NO_CONTENT);
    }

    // 특정 플랜 수정
    public ResponseEntity<ApiResponse<Plan>> updatePlanDateById(@RequestBody RequestPlanDTO planDTO, @PathVariable Long planId) {
        Plan plan = planService.updatePlan(planId, planDTO);

        return ApiResponse.success(plan, "성공적으로 수정되었습니다.", HttpStatus.OK);
    }


}
