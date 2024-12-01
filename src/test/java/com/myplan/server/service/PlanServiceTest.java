package com.myplan.server.service;

import com.myplan.server.dto.plan.RequestPlanDTO;
import com.myplan.server.dto.plan.ResponsePlanDTO;
import com.myplan.server.exception.AlreadyExistsException;
import com.myplan.server.exception.NotFoundException;
import com.myplan.server.model.Member;
import com.myplan.server.model.Plan;
import com.myplan.server.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlanServiceTest {

    private static final Logger log = LoggerFactory.getLogger(PlanServiceTest.class);
    @Mock
    private UserService userService;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addPlan_Success() {
        // Given
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);

        RequestPlanDTO requestPlanDTO = new RequestPlanDTO(LocalDate.now());

        Plan savedPlan = new Plan();
        savedPlan.setId(memberId);
        savedPlan.setPlanDate(requestPlanDTO.getPlanDate());

        System.out.println(userService.getClass());
        // Mock 설정
        when(userService.getUserIdByUsername()).thenReturn(memberId);
        when(userService.getUserById(memberId)).thenReturn(member);
        when(planRepository.findByPlanDateAndMemberId(requestPlanDTO.getPlanDate(), memberId)).thenReturn(null);
        when(planRepository.save(any(Plan.class))).thenReturn(savedPlan);

        // When
        ResponsePlanDTO response = planService.addPlan(requestPlanDTO);

        // Then
        assertNotNull(response);
        assertEquals(savedPlan.getId(), response.getId());
        verify(userService, times(1)).getUserById(memberId); // Mock 호출 검증
    }

    @Test
    void addPlan_AlreadyExists() {
        // Given
        RequestPlanDTO requestPlanDTO = new RequestPlanDTO(LocalDate.now());
        Long memberId = 1L;
        Plan existingPlan = new Plan();

        when(userService.getUserIdByUsername()).thenReturn(memberId);
        when(planRepository.findByPlanDateAndMemberId(requestPlanDTO.getPlanDate(), memberId)).thenReturn(existingPlan);

        // When & Then
        assertThrows(AlreadyExistsException.class, () -> planService.addPlan(requestPlanDTO));
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void findPlanByDateRange() {
        // Given
        int month = 1;
        Long memberId = 1L;

        when(userService.getUserIdByUsername()).thenReturn(memberId);
        when(planRepository.findResponsePlanByStartDateAndEndDate(eq(memberId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        // When
        var result = planService.findPlanByDateRange(month);

        // Then
        assertNotNull(result); // null 인지 체크
        verify(planRepository, times(1)).findResponsePlanByStartDateAndEndDate(eq(memberId), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void deletePlan_Success() {

        // Given
        Long planId = 1L;
        Long memberId = 1L;

        when(userService.getUserIdByUsername()).thenReturn(memberId);

        doNothing().when(planRepository).deleteByPlanIdAndMemberId(planId, memberId);

        // When & Then
        assertDoesNotThrow(() -> planService.deletePlan(planId)); // 예외 안 던지는지 체크
        verify(planRepository, times(1)).deleteByPlanIdAndMemberId(planId, memberId);
    }

    @Test
    void findById_Success() {

        // Given
        Long planId = 1L;
        Plan plan = new Plan();
        plan.setId(planId);

        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));


        // WHen
        Plan result = planService.findById(planId);

        //Then
        assertNotNull(result);
        assertEquals(planId, result.getId());
    }

    @Test
    void findById_NotFound() {
        Long planId = 1L;

        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> planService.findById(planId));
    }

    @Test
    void updatePlan_Success() {

        // Given
        Long planId = 1L;
        RequestPlanDTO planDTO = new RequestPlanDTO(LocalDate.now());
        Plan existingPlan = new Plan();
        existingPlan.setId(planId);

        when(planRepository.findById(planId)).thenReturn(Optional.of(existingPlan));
        when(planRepository.save(any(Plan.class))).thenReturn(existingPlan);


        // When
        Plan updatedPlan = planService.updatePlan(planId, planDTO);


        // Then
        assertNotNull(updatedPlan);
        verify(planRepository, times(1)).save(existingPlan);
    }

    @Test
    void updatePlan_NotFound() {
        Long planId = 1L;
        RequestPlanDTO planDTO = new RequestPlanDTO(LocalDate.now());

        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> planService.updatePlan(planId, planDTO));
        verify(planRepository, never()).save(any(Plan.class));
    }
}
