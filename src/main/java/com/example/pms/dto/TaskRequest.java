package com.example.pms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TaskRequest {

    @NotNull(message = "Assigned user ID is required")
    private Long assignedUserId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Task deadline is required")
    private LocalDateTime dueDate;

    public TaskRequest(){}

    public TaskRequest(Long assignedUserId, String title, String description, LocalDateTime dueDate){
        this.assignedUserId = assignedUserId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Long getAssignedUserId(){
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate(){
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}