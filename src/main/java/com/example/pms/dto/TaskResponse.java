package com.example.pms.dto;

import com.example.pms.enums.TaskStatus;
import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private Long assignedUserId;
    private String assignedUsername;

    public TaskResponse(Long id, Long projectId, String title, String description, TaskStatus status,
                        LocalDateTime dueDate, Long assignedUserId, String assignedUsername){
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.assignedUserId = assignedUserId;
        this.assignedUsername = assignedUsername;
    }

    public Long getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public TaskStatus getStatus(){
        return status;
    }

    public LocalDateTime getDueDate(){
        return dueDate;
    }

    public Long getProjectId(){
        return projectId;
    }

    public String getAssignedUsername(){
        return assignedUsername;
    }

    public Long getAssignedUserId(){
        return assignedUserId;
    }
}