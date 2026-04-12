package com.example.pms.service;

import com.example.pms.dto.TaskRequest;
import com.example.pms.dto.TaskResponse;
import com.example.pms.dto.UpdateTaskStatusRequest;
import com.example.pms.enums.GlobalRole;
import com.example.pms.enums.RoleInProject;
import com.example.pms.enums.TaskStatus;
import com.example.pms.exception.ResourceNotFoundException;
import com.example.pms.exception.UnauthorizedActionException;
import com.example.pms.model.Project;
import com.example.pms.model.Task;
import com.example.pms.model.User;
import com.example.pms.repository.ProjectMemberRepository;
import com.example.pms.repository.ProjectRepository;
import com.example.pms.repository.TaskRepository;
import com.example.pms.repository.UserRepository;
import com.example.pms.security.SecurityContextService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SecurityContextService securityContextService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                       ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository, SecurityContextService securityContextService){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.securityContextService = securityContextService;
    }

    @Transactional
    public TaskResponse createTask(Long projectId, TaskRequest request){

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User assignedUser = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        securityContextService.requireMember(project);

        boolean assignedUserIsMember = projectMemberRepository.existsByUserAndProject(assignedUser, project);

        if(!assignedUserIsMember){
            throw new UnauthorizedActionException("The assigned user is not a member of this project");
        }

        Task task = new Task(request.getTitle(), request.getDescription(), project, assignedUser, request.getDueDate());
        taskRepository.save(task);

        return new TaskResponse(task.getId(), project.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getDueDate(), assignedUser.getId(), assignedUser.getEmail());
    }

    public Page<TaskResponse> getTasksByProject(Long projectId, Pageable pageable){

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        securityContextService.requireMemberOrAdmin(project);

        Page<Task> taskPage = taskRepository.findByProject(project, pageable);

        return taskPage.map(taskResponsePage -> {
            return new TaskResponse(taskResponsePage.getId(), taskResponsePage.getProject().getId(),
                    taskResponsePage.getTitle(), taskResponsePage.getDescription(),
                    taskResponsePage.getStatus(), taskResponsePage.getDueDate(),
                    taskResponsePage.getAssignedUser().getId(), taskResponsePage.getAssignedUser().getEmail());
        });

    }

    @Transactional
    public void assignTask(Long taskId, Long userId){

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        User assignedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = task.getProject();

        securityContextService.requireOwnerOrAdmin(project);

        boolean isMember = projectMemberRepository.existsByUserAndProject(assignedUser, project);

        if(!isMember){
            throw new UnauthorizedActionException("Task can only be assigned to a project member.");
        }

        task.setAssignedUser(assignedUser);
        taskRepository.save(task);
    }

    @Transactional
    public void updateTaskStatus(UpdateTaskStatusRequest request) {

        User user = securityContextService.getCurrentUser();

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (task.getAssignedUser() == null || !task.getAssignedUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("You are not allowed to update task status");
        }

        task.setStatus(request.getStatus());
        taskRepository.save(task);
    }

    public void deleteTask(Long taskId){

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Project project = task.getProject();

       securityContextService.requireOwnerOrAdmin(project);
       taskRepository.delete(task);
    }
}
