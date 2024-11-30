package com.myplan.server.service;

import com.myplan.server.dto.task.ResponseTaskDTO;
import com.myplan.server.dto.task.RequestTaskDTO;
import com.myplan.server.exception.AlreadyExistsException;
import com.myplan.server.exception.NotFoundException;
import com.myplan.server.model.Plan;
import com.myplan.server.model.Task;
import com.myplan.server.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final PlanService planService;

    /* MEMO: endTime 은 경계값을 처리하기 위해 작업 추가 시 1분 1초를 빼고, 작업 수정과 조회 시 1분 1초를 더한다.*/

    // 작업 조회
    public List<ResponseTaskDTO> getTask(Long planId) {

        if (planId == null) {
            throw new IllegalArgumentException("planId 가 null 입니다.");
        }

        List<Task> tasks = taskRepository.findAllByPlanId(planId);
        List<ResponseTaskDTO> responseTaskDTO = new ArrayList<>();

        log.info("조회된 작업 목록: {}", tasks);
        for (Task task : tasks) {

            responseTaskDTO.add(ResponseTaskDTO.builder()
                    .id(task.getId())
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .colorCode(task.getColorCode())
                    .startTime(task.getStartTime())
                    .endTime(LocalTime.of(task.getEndTime().getHour(), task.getEndTime().getMinute(), 0).plusMinutes(1)) // 1분 더함(ex. 14:19:59 -> 14:20:00)
                    .build()
            );
        }
        return responseTaskDTO;
    }

    // 작업 추가
    @Transactional
    public ResponseTaskDTO addTask(Long planId, RequestTaskDTO requestTaskDTO) {


        // 작업 겹침 유효성
        if (isExistsTask(planId, requestTaskDTO.getStartTime(), requestTaskDTO.getEndTime())) {
            throw new AlreadyExistsException(requestTaskDTO.getStartTime() + " ~ " + requestTaskDTO.getEndTime() + " 까지의 작업은 이미 등록되었습니다.");
        }

        // 날짜 간격 유효성
        if (timeValidation(requestTaskDTO.getStartTime(), requestTaskDTO.getEndTime())) {
            throw new IllegalArgumentException("Start Time 과 End Time 은 최소 1분 이상 차이가 있어야 합니다.");
        }


        if (!planService.existsById(planId)) {
            throw new NotFoundException(planId + "로 등록된 Plan 이 존재하지 않습니다.");
        }
        Plan plan = planService.findById(planId);
        Task task = Task.builder()
                .title(requestTaskDTO.getTitle())
                .description(requestTaskDTO.getDescription())
                .colorCode(requestTaskDTO.getColorCode())
                .startTime(requestTaskDTO.getStartTime())
                .plan(plan)
                .endTime(LocalTime.of(requestTaskDTO.getEndTime().getHour(), requestTaskDTO.getEndTime().getMinute(), 59).minusMinutes(1)) // 1분 뺌(ex. 14:20:00 -> 14:19:59)
                .build();

        Task newTask = taskRepository.save(task);
        requestTaskDTO.setPlanId(newTask.getPlan().getId());
        return ResponseTaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .startTime(task.getStartTime())
                .endTime(LocalTime.of(task.getEndTime().getHour(), task.getEndTime().getMinute(), 0).plusMinutes(1))
                .colorCode(task.getColorCode())
                .build();
    }

    // 작업 수정(시간 미포함)
    @Transactional
    public ResponseTaskDTO updateTask(Long taskId, RequestTaskDTO requestTaskDTO) {
        if (taskId == null) {
            throw new IllegalArgumentException("Task Id 가 비어 있습니다.");
        }
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle(requestTaskDTO.getTitle());
        updatedTask.setDescription(requestTaskDTO.getDescription());
        updatedTask.setColorCode(requestTaskDTO.getColorCode());

        int updatedRowCnt = taskRepository.updateByPlanId(taskId, requestTaskDTO.getTitle(), requestTaskDTO.getDescription(), requestTaskDTO.getColorCode());
        if (updatedRowCnt == 0) {
            throw new IllegalArgumentException("해당 Task ID 로 변경된 데이터가 " + updatedRowCnt + "건 입니다.");
        }
        Task newTask = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("해당 ID의 Task를 찾을 수 없습니다."));
        return ResponseTaskDTO.builder()
                .id(newTask.getId())
                .title(newTask.getTitle())
                .description(newTask.getDescription())
                .colorCode(newTask.getColorCode())
                .startTime(newTask.getStartTime())
                .endTime(LocalTime.of(newTask.getEndTime().getHour(), newTask.getEndTime().getMinute(), 0).plusMinutes(1))
                .build();
    }

    // 작업 수정(시간 포함)
    @Transactional
    public ResponseTaskDTO updateTask(Long planId, Long taskId, RequestTaskDTO requestTaskDTO) {
        if (taskId == null) {
            throw new IllegalArgumentException("Task Id 가 비어 있습니다.");
        }

        // 작업 겹침 유효성
        if (isExistsTask(planId, requestTaskDTO.getStartTime(), requestTaskDTO.getEndTime())) {
            throw new AlreadyExistsException(requestTaskDTO.getStartTime() + " ~ " + requestTaskDTO.getEndTime() + " 까지의 작업은 이미 등록되었습니다.");
        }


        // 날짜 간격 유효성
        if (timeValidation(requestTaskDTO.getStartTime(), requestTaskDTO.getEndTime())) {
            throw new IllegalArgumentException("Start Time 과 End Time 은 최소 1분 이상 차이가 있어야 합니다.");
        }

        Plan plan = planService.findById(planId);
        Task updatedTask = Task.builder()
                .id(taskId)
                .title(requestTaskDTO.getTitle())
                .description(requestTaskDTO.getDescription())
                .colorCode(requestTaskDTO.getColorCode())
                .plan(plan)
                .startTime(requestTaskDTO.getStartTime())
                .endTime(LocalTime.of(requestTaskDTO.getEndTime().getHour(), requestTaskDTO.getEndTime().getMinute(), 59).minusMinutes(1)) // 1분 뺌(ex. 14:20:00 -> 14:19:59)
                .build();

        taskRepository.save(updatedTask);

        ResponseTaskDTO responseTaskDTO = taskRepository.findResponseTaskById(taskId);
        responseTaskDTO.setEndTime(LocalTime.of(responseTaskDTO.getEndTime().getHour(), responseTaskDTO.getEndTime().getMinute(), 0).plusMinutes(1)); // 1분 더함(ex. 14:19:59 -> 14:20:00

        return responseTaskDTO;
    }

    // 작업 삭제
    @Transactional
    public Long deleteTask(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("Task Id 가 비어 있습니다.");
        }
        taskRepository.deleteById(taskId);
        return taskId;
    }

    // == 유틸
    // 작업 존재 유무 확인
    private boolean isExistsTask(Long planId, LocalTime startTime, LocalTime endTime) {
        List<Task> hasTask = taskRepository.findByTimeBetweenAndId(planId, startTime, endTime);
        log.info("현재 존재하는 작업은 {} 입니다.", hasTask);
        return !hasTask.isEmpty();
    }

    // 날짜 간격 유효성 검증(end - start > 60L)
    private boolean timeValidation(LocalTime start, LocalTime end) {
        Duration duration = Duration.between(start, end);
        log.info("시간간격(초): {}, 시간간격(분): {}", duration.getSeconds(), duration.toMinutes());
        return duration.getSeconds() <= 60L;
    }
}
