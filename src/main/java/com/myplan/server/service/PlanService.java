package com.myplan.server.service;

import com.myplan.server.dto.plan.RequestPlanDTO;
import com.myplan.server.dto.plan.ResponsePlanDTO;
import com.myplan.server.exception.AlreadyExistsException;
import com.myplan.server.exception.NotFoundException;
import com.myplan.server.model.Member;
import com.myplan.server.model.Plan;
import com.myplan.server.repository.PlanRepository;
import com.myplan.server.util.AuthenticationFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanService {
    private final PlanRepository planRepository;
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;



    // 기간별 조회
    public List<ResponsePlanDTO> findPlanByDateRange(int month) {

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusMonths(month);

        return planRepository.findResponsePlanByStartDateAndEndDate(getCurrentMemberId(), startDate, endDate);
    }


    // 플랜 추가
    @Transactional
    public ResponsePlanDTO addPlan(RequestPlanDTO planDTO) {

        Long memberId = getCurrentMemberId();
        if (memberId == null) {
            throw new IllegalArgumentException("유저의 ID 가 null 입니다.");
        }

        if (planRepository.findByPlanDateAndMemberId(planDTO.getPlanDate(), memberId) == null) {
            Member member = userService.getUserById(memberId);

            Plan plan = new Plan();
            plan.setPlanDate(planDTO.getPlanDate());
            plan.setMember(member);

            Plan newPlan = planRepository.save(plan);

            return new ResponsePlanDTO(newPlan.getId(), newPlan.getPlanDate(), newPlan.getCreatedAt(), newPlan.getUpdatedAt());

        } else {
            throw new AlreadyExistsException("이미 존재하는 플랜입니다.");
        }
    }

    // 특정 플랜 존재 유무 확인
    public boolean existsById(Long id) {
        return planRepository.existsById(id);
    }

    // 특정 틀린 조회 : id
    public Plan findById(Long planId){
         return planRepository.findById(planId).orElseThrow(()-> new NotFoundException("현재하지 않는 plan 입니다."));
    }

    // 특정 플랜 조회 : 날짜
    public ResponsePlanDTO findByDate(LocalDate planDate) {
        if (planDate == null) {
            throw new IllegalArgumentException("planDate 가 null 입니다.");
        }

        Plan plan = planRepository.findByPlanDateAndMemberId(planDate, getCurrentMemberId());
        if (plan == null) {
            throw new NotFoundException("현재" + planDate + "로 등록된 플랜을 찾을 수 없습니다.");
        }
        return new ResponsePlanDTO(plan.getId(), plan.getPlanDate(), plan.getUpdatedAt(), plan.getCreatedAt());
    }

    // 특정 플랜 삭제 : id
    @Transactional
    public long deletePlan(Long planId) {
        if (planId == null) {
            throw new IllegalArgumentException("Plan Id 가 null 입니다.");
        }
        planRepository.deleteByPlanIdAndMemberId(planId, getCurrentMemberId());
        return planId;
    }

    // 특정 플랜 수정
    @Transactional
    public Plan updatePlan(Long planId, RequestPlanDTO planDTO) {
        if (planId == null) {
            throw new IllegalArgumentException("Plan Id 가 null 입니다.");
        }

        Plan plan = planRepository.findById(planId).orElseThrow(() -> new NotFoundException("해당 Plan 은 존재하지 않습니다."));
        plan.setPlanDate(planDTO.getPlanDate());

        return planRepository.save(plan);
    }


    // 유저 ID 조회
    private Long getCurrentMemberId() {
        return userService.getUserIdByUsername();
    }

}
