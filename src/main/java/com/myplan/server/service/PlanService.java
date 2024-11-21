package com.myplan.server.service;

import com.myplan.server.dto.PlanDTO;
import com.myplan.server.exception.NotFound;
import com.myplan.server.model.Plan;
import com.myplan.server.repository.PlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanService {
    private final PlanRepository planRepository;
    private final UserService userService;

    // 플랜 전체 조회
    public void findAll() {
    }

    // 요일 별 조회
    public void findByDay() {
    }

    // 주차별 조회
    public void findByWeekday() {
    }

    // 날짜별 조회
    public Plan findAllBySearchDateAndId(Long id, LocalDate searchDate) {
        if (id == null) {
            throw new IllegalArgumentException("User ID is Null");
        }
        if (searchDate == null) {
            throw new IllegalArgumentException("Search date is Null");
        }
        if (userService.existsUser(id)) {
            Plan plan = planRepository.findByPlanDate(searchDate);
            if (plan == null) {
                throw new NotFound("Plan is Null");
            }
            return plan;
        } else {
            return null;
        }
    }

    // 플랜 추가
    @Transactional
    public Plan addPlan(PlanDTO planDTO, Long id) {

        if (id == null) {
            throw new IllegalArgumentException("User ID is Null");
        }

        Plan plan = new Plan();
        if(planRepository.findByPlanDateAndMemberId(planDTO.getPlanDate(), id)==null){

            plan.setPlanDate(planDTO.getPlanDate());
            planRepository.save(plan);

            return plan;

        }
        return plan;
    }

    // 특정 플랜 존재 유무 확인
    public boolean existsById(Long id){
        return planRepository.existsById(id);
    }

    // 특정 플랜 조회
    public Plan findById(Long id){
        return planRepository.findById(id).orElseThrow(()-> new NotFound("Plan is Null"));
    }

    // 특정 플랜 삭제
    public void removeById() {
    }

    // 특정 플랜 수정
    public void updateById() {
    }
}
