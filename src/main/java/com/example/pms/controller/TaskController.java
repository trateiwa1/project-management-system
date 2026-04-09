package com.example.pms.controller;

import com.example.pms.dto.TaskRequest;
import com.example.pms.dto.TaskResponse;
import com.example.pms.dto.UpdateTaskStatusRequest;
import com.example.pms.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Page<TaskResponse>> getTasksByProject(@PathVariable Long projectId, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskResponse> response = taskService.getTasksByProject(projectId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request){

        TaskResponse response = taskService.createTask(projectId, request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @PutMapping("/tasks/{taskId}/assign/{userId}")
    public ResponseEntity<Void> assignTask(@PathVariable Long taskId, @PathVariable Long userId){
        taskService.assignTask(taskId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long taskId, @Valid @RequestBody UpdateTaskStatusRequest request){
        taskService.updateTaskStatus(taskId, request.getTaskStatus());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long taskId){
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

}
