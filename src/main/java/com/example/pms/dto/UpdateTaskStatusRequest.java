package com.example.pms.dto;

import com.example.pms.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateTaskStatusRequest {

    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    public UpdateTaskStatusRequest() {}

    public UpdateTaskStatusRequest(Long taskId, TaskStatus status) {
        this.taskId = taskId;
        this.status = status;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}