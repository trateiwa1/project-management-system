package com.example.pms.service;

import com.example.pms.dto.TaskRequest;
import com.example.pms.dto.TaskResponse;
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
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                       ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public User getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public TaskResponse createTask(Long projectId, TaskRequest request){

        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User assignedUser = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean currentUserIsMember = projectMemberRepository.existsByUserAndProject(currentUser, project);

        if(!currentUserIsMember){
            throw new UnauthorizedActionException("You are not allowed to create a task for this project");
        }

        boolean assignedUserIsMember = projectMemberRepository.existsByUserAndProject(assignedUser, project);

        if(!assignedUserIsMember){
            throw new UnauthorizedActionException("The assigned user is not a member of this project");
        }

        Task task = new Task(request.getTitle(), request.getDescription(), project, assignedUser, request.getDueDate());

        taskRepository.save(task);

        return new TaskResponse(task.getId(), project.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getDueDate(), assignedUser.getId(), assignedUser.getEmail());
    }

    @Transactional
    public void assignTask(Long taskId, Long userId){

        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        User assignedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = task.getProject();

        boolean isOwner = projectMemberRepository.existsByUserAndProjectAndRole(currentUser, project, RoleInProject.OWNER);

        boolean isAdmin = currentUser.getRole() == GlobalRole.ADMIN;

        if(!isOwner && !isAdmin){
            throw new UnauthorizedActionException("You do not have permission to assign tasks in this project.");
        }

        boolean isMember = projectMemberRepository.existsByUserAndProject(assignedUser, project);

        if(!isMember){
            throw new UnauthorizedActionException("Task can only be assigned to a project member.");
        }

        task.setAssignedUser(assignedUser);

        taskRepository.save(task);
    }

    @Transactional
    public void updateTaskStatus(Long taskId, TaskStatus status) {

        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (task.getAssignedUser() == null || !task.getAssignedUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not allowed to update task status");
        }

        task.setStatus(status);

        taskRepository.save(task);
    }

    public Page<TaskResponse> getTasksByProject(Long projectId, Pageable pageable){

        User user = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        boolean isMember = projectMemberRepository.existsByUserAndProject(user, project);

        boolean isAdmin = user.getRole() == GlobalRole.ADMIN;

        if(!isMember && !isAdmin){
            throw new UnauthorizedActionException("You are not allowed to retrieve the tasks");
        }

        Page<Task> taskPage = taskRepository.findByProject(project, pageable);

        return taskPage.map(taskResponsePage -> {
            return new TaskResponse(taskResponsePage.getId(), taskResponsePage.getProject().getId(),
                    taskResponsePage.getTitle(), taskResponsePage.getDescription(),
                    taskResponsePage.getStatus(), taskResponsePage.getDueDate(),
                    taskResponsePage.getAssignedUser().getId(), taskResponsePage.getAssignedUser().getEmail());
        });

    }

    public void deleteTask(Long taskId){

        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Project project = task.getProject();

        boolean isOwner = projectMemberRepository.existsByUserAndProjectAndRole(currentUser, project, RoleInProject.OWNER);

        boolean isAdmin = currentUser.getRole() == GlobalRole.ADMIN;

        if( !isOwner && !isAdmin && !currentUser.getId().equals(task.getAssignedUser().getId())){
            throw new UnauthorizedActionException("You are not allowed to delete this task");
        }

        taskRepository.delete(task);
    }
}
