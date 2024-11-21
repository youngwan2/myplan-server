package com.myplan.server.controller;

import com.myplan.server.config.response.ApiResponse;
import com.myplan.server.dto.TaskDTO;
import com.myplan.server.model.Task;
import com.myplan.server.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/plans/{planId}/tasks")
    public ResponseEntity<ApiResponse<Task>> addTask(@RequestBody @Valid TaskDTO taskDto, @PathVariable("planId") Long planId){

       Task task = taskService.addTask(taskDto, planId);

        return ApiResponse.success( task ,"성공적으로 추가되었습니다." );
    }
}
