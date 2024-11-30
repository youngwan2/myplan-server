package com.myplan.server.service;

import com.myplan.server.dto.task.RequestTaskDTO;
import com.myplan.server.exception.AlreadyExistsException;
import com.myplan.server.model.Plan;
import com.myplan.server.model.Task;
import com.myplan.server.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;


    @Mock
    private PlanService planService;
    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTask_ShouldThrowAlreadyExistsException_WhenTaskAlreadyExist() {

        // Given
        Long planId = 4L;
        RequestTaskDTO requestTaskDTO = new RequestTaskDTO();
        requestTaskDTO.setColorCode("#333");
        requestTaskDTO.setTitle("점심식사 시간");
        requestTaskDTO.setDescription("돈까스와 김치를 같이 먹습니다.");
        requestTaskDTO.setStartTime(LocalTime.of(15, 10));
        requestTaskDTO.setEndTime(LocalTime.of(16, 10));

        // == 기존에 존재하는 Task Mock (Plan 과 ManyToOne 관계)
        Task existingTask = new Task();
        existingTask.setId(4L);
        existingTask.setTitle("돈 먹고 꿩 먹기");
        existingTask.setDescription("꿩이 돈을 좋아하게 노래해줍니다.");
        existingTask.setStartTime(LocalTime.of(15, 10));
        existingTask.setEndTime(LocalTime.of(16, 10));
        existingTask.setColorCode("#333");

        // == Plan Mock
        Plan existingPlan = new Plan();
        existingPlan.setId(planId);
        existingPlan.setPlanDate(LocalDate.of(24,11,23));

        // == Task Mock return 값
        List<Task> mockTask = Collections.singletonList(existingTask);


        // findByTimeBetweenAndId가 existingTask 를 반환하도록 설정
        when(taskRepository.findByTimeBetweenAndId(planId, requestTaskDTO.getStartTime(), requestTaskDTO.getEndTime())).thenReturn(mockTask);
        when(planService.findById(planId)).thenReturn(existingPlan);

        // When & Then: Task가 이미 존재하면 예외를 던져야 한다.
        assertThrows(AlreadyExistsException.class, () -> taskService.addTask( planId, requestTaskDTO));

        // == Verify: save 메서드는 호출되지 않아야 한다.
        verify(taskRepository, never()).save(any(Task.class));
    }
}
