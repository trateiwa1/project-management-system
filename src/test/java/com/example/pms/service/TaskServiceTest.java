package com.example.pms.service;

import com.example.pms.dto.TaskRequest;
import com.example.pms.dto.TaskResponse;
import com.example.pms.dto.UpdateTaskStatusRequest;
import com.example.pms.enums.GlobalRole;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private SecurityContextService securityContextService;

    @InjectMocks
    private TaskService taskService;

    private User user1;
    private User user2;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp(){
        user1 = new User("Emily", "emily@test.com", "Password", GlobalRole.USER);
        user1.setId(1L);

        user2 = new User("James", "james@test.com", "Password", GlobalRole.USER);
        user2.setId(2L);

        project = new Project("Project", "First project");
        project.setId(1L);

        task = new Task("Task", "Basic description", project, user2, LocalDateTime.now().plusDays(3));
        task.setId(1L);
    }

    //---------------------- SUCCESS CASES ----------------------

    @Test
    void createTask(){

        TaskRequest request = new TaskRequest(2L, "Task", "My first task",
                LocalDateTime.now().plusDays(3));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        doNothing().when(securityContextService).requireMember(project);
        when(projectMemberRepository.existsByUserAndProject(user2, project)).thenReturn(true);

        TaskResponse result = taskService.createTask(1L, request);

        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTasksByProject() {

        Page<Task> page = new PageImpl<>(List.of(task));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(securityContextService).requireMemberOrAdmin(project);
        when(taskRepository.findByProject(eq(project), any())).thenReturn(page);

        Page<TaskResponse> result = taskService.getTasksByProject(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void assignTask(){

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        doNothing().when(securityContextService).requireOwnerOrAdmin(project);
        when(projectMemberRepository.existsByUserAndProject(user2, project)).thenReturn(true);

        taskService.assignTask(1L, 2L);

        verify(taskRepository).save(task);
        assertEquals(user2, task.getAssignedUser());
    }

    @Test
    void updateTaskStatus(){

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(1L, TaskStatus.DONE);

        when(securityContextService.getCurrentUser()).thenReturn(user2);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.updateTaskStatus(request);

        assertEquals(TaskStatus.DONE, task.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void deleteTask(){

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(securityContextService).requireOwnerOrAdmin(project);

        taskService.deleteTask(1L);
        verify(taskRepository).delete(task);
    }

    //---------------------- FAILURE CASES ----------------------

    //CREATE TASK

    @Test
    void createTask_projectNotFound() {

        TaskRequest request = new TaskRequest(2L, "Task", "My first task",
                LocalDateTime.now().plusDays(3));

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.createTask(1L, request));
    }

    @Test
    void createTask_assignedUserNotFound(){

        TaskRequest request = new TaskRequest(2L, "Task", "My first task",
                LocalDateTime.now().plusDays(3));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.createTask(1L, request));
    }

    @Test
    void createTask_assignedUserNotMember(){

        TaskRequest request = new TaskRequest(2L, "Task", "Desc",
                LocalDateTime.now());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        doNothing().when(securityContextService).requireMember(project);
        when(projectMemberRepository.existsByUserAndProject(user2, project)).thenReturn(false);

        assertThrows(UnauthorizedActionException.class,
                () -> taskService.createTask(1L, request));
    }

    //GET TASKS

    @Test
    void getTasksByProject_notFound(){

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.getTasksByProject(1L, PageRequest.of(0, 10)));
    }

    @Test
    void getTasksByProject_notMember(){

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        doThrow(new UnauthorizedActionException("Not allowed"))
                .when(securityContextService).requireMemberOrAdmin(project);

        assertThrows(UnauthorizedActionException.class,
                () -> taskService.getTasksByProject(1L, PageRequest.of(0, 10)));
    }

    //ASSIGN TASK

    @Test
    void assignTask_taskNotFound(){

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.assignTask(1L, 2L));
    }

    @Test
    void assignTask_notMember(){

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        doNothing().when(securityContextService).requireOwnerOrAdmin(project);
        when(projectMemberRepository.existsByUserAndProject(user2, project)).thenReturn(false);

        assertThrows(UnauthorizedActionException.class,
                () -> taskService.assignTask(1L, 2L));

        verify(taskRepository, never()).save(any(Task.class));
    }

    //UPDATE TASK STATUS

    @Test
    void updateTaskStatus_taskNotFound(){

        UpdateTaskStatusRequest request =
                new UpdateTaskStatusRequest(1L, TaskStatus.DONE);

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTaskStatus(request));
    }

    @Test
    void updateTaskStatus_notOwner(){

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(1L, TaskStatus.DONE);

        when(securityContextService.getCurrentUser()).thenReturn(user1);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedActionException.class,
                () -> taskService.updateTaskStatus(request));
    }

    //DELETE TASK

    @Test
    void deleteTask_notFound(){

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(1L));
    }

    @Test
    void deleteTask_notOwner(){

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        doThrow(new UnauthorizedActionException("Not allowed"))
                .when(securityContextService).requireOwnerOrAdmin(project);

        assertThrows(UnauthorizedActionException.class,
                () -> taskService.deleteTask(1L));
    }
}