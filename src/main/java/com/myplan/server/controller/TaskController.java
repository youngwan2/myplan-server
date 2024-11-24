package com.myplan.server.controller;

import com.myplan.server.config.response.ApiResponse;
import com.myplan.server.dto.ResponseTaskDTO;
import com.myplan.server.dto.RequestTaskDTO;
import com.myplan.server.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/{userId}/plans/{planId}")
public class TaskController {
    private final TaskService taskService;

    // 작업 조회(All)
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<ResponseTaskDTO>>> getTask(@PathVariable("planId") Long planId) {
        return ApiResponse.success(taskService.getTask(planId), "성공적으로 조회되었습니다.", HttpStatus.OK);
    }

    // 작업 추가
    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<ResponseTaskDTO>> addTask(@RequestBody @Valid RequestTaskDTO requestTaskDto, @PathVariable("planId") Long planId) {
        log.info("plan: {}\t planId: {}", requestTaskDto.toString(), planId);

        return ApiResponse.success(taskService.addTask(planId, requestTaskDto), "성공적으로 추가되었습니다.", HttpStatus.CREATED);
    }


    // 작업 수정
    @PatchMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<ResponseTaskDTO>> updateTask(
            @RequestBody @Valid RequestTaskDTO requestTaskDTO,
            @PathVariable("taskId") Long taskId,
            @PathVariable("planId") Long planId) {

        boolean hasTime = !(requestTaskDTO.getStartTime() == null && requestTaskDTO.getEndTime() == null);
        log.info("요청 DTO: {}, {}", requestTaskDTO, hasTime);

        ResponseTaskDTO responseTaskDTO = hasTime ? taskService.updateTask(planId, taskId, requestTaskDTO) : taskService.updateTask(taskId, requestTaskDTO);
        return ApiResponse.success(responseTaskDTO, "성공적으로 수정되었습니다.", HttpStatus.OK);
    }

    // 작업 삭제
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse<Long>> deleteTask(@PathVariable("taskId") Long taskId) {
        return ApiResponse.success(taskService.deleteTask(taskId), "성공적으로 삭제되었습니다.", HttpStatus.OK);
    }
}
