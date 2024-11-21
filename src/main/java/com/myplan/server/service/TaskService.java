package com.myplan.server.service;

import com.myplan.server.dto.TaskDTO;
import com.myplan.server.model.Plan;
import com.myplan.server.model.Task;
import com.myplan.server.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private  final TaskRepository taskRepository;
    private final PlanService planService;

    // 작업 추가
    @Transactional
    public Task addTask(TaskDTO taskDTO, Long planId){

        boolean hasPlan =planService.existsById(planId);

        if(hasPlan){
            Plan plan =planService.findById(planId);
            Task task = Task.builder()
                    .title(taskDTO.getTitle())
                    .description(taskDTO.getDescription())
                    .colorCode(taskDTO.getColorCode())
                    .startTime(taskDTO.getStartTime())
                    .plan(plan)
                    .endTime(taskDTO.getEndTime())
                    .build();

            return taskRepository.save(task);
        }
        return null;
    }
}
