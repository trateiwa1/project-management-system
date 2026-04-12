package com.example.pms.service;

import com.example.pms.dto.ProjectRequest;
import com.example.pms.dto.ProjectResponse;
import com.example.pms.enums.GlobalRole;
import com.example.pms.enums.RoleInProject;
import com.example.pms.exception.BadRequestException;
import com.example.pms.exception.ResourceNotFoundException;
import com.example.pms.model.Project;
import com.example.pms.model.ProjectMember;
import com.example.pms.model.User;
import com.example.pms.repository.ProjectMemberRepository;
import com.example.pms.repository.ProjectRepository;
import com.example.pms.repository.UserRepository;
import com.example.pms.security.SecurityContextService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private SecurityContextService securityContextService;

    @InjectMocks
    private ProjectService projectService;

    private User user1;
    private User user2;
    private Project project;

    @BeforeEach
    void setUp(){

        user1 = new User("Tom", "tom@test.com", "Password", GlobalRole.USER);
        user1.setId(1L);

        user2 = new User("Anne", "anne@test.com", "Password", GlobalRole.USER);
        user2.setId(2L);

        project = new Project("Project", "First project");
        project.setId(1L);
    }

    //---------------------- SUCCESS CASES ----------------------

    @Test
    void createProject(){

        ProjectRequest request = new ProjectRequest("Project", "My first project");

        when(securityContextService.getCurrentUser()).thenReturn(user1);

        ProjectResponse result = projectService.createProject(request);

        assertNotNull(result);
        verify(projectRepository).save(any(Project.class));
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    void getMyProjects(){

        ProjectMember projectMember = new ProjectMember(project, user1, RoleInProject.OWNER);

        Page<ProjectMember> page = new PageImpl<>(List.of(projectMember));

        when(securityContextService.getCurrentUser()).thenReturn(user1);
        when(projectMemberRepository.findByUser(eq(user1), any())).thenReturn(page);

        Page<ProjectResponse> result = projectService.getMyProjects(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(project.getName(), result.getContent().get(0).getName());
    }

    @Test
    void getProjectById(){

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(securityContextService).requireMember(project);

        ProjectResponse result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals(project.getName(), result.getName());
    }

    @Test
    void addMember(){

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(securityContextService).requireOwnerOrAdmin(project);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(projectMemberRepository.existsByUserAndProject(user2, project)).thenReturn(false);

        projectService.addMember(1L, 2L);

        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    void deleteProjectById(){

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(securityContextService).requireOwnerOrAdmin(project);

        projectService.deleteProjectById(1L);

        verify(projectMemberRepository).deleteByProject(project);
        verify(projectRepository).delete(project);
    }

    //---------------------- FAILURE CASES ----------------------

    //GET PROJECTS

    @Test
    void getMyProjects_empty(){

        Page<ProjectMember> page = new PageImpl<>(List.of());

        when(securityContextService.getCurrentUser()).thenReturn(user1);
        when(projectMemberRepository.findByUser(eq(user1), any())).thenReturn(page);

        Page<ProjectResponse> result =
                projectService.getMyProjects(PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getProjectById_notFound(){

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getProjectById(1L));
    }

    //ADD MEMBER

    @Test
    void addMember_projectNotFound(){

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.addMember(1L, 2L));
    }

    @Test
    void addMember_userNotFound(){

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(securityContextService).requireOwnerOrAdmin(project);

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.addMember(1L, 2L));
    }

    @Test
    void addMember_alreadyMember(){

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(securityContextService).requireOwnerOrAdmin(project);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(projectMemberRepository.existsByUserAndProject(user2, project)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> projectService.addMember(1L, 2L));
    }

    //DELETE PROJECT
    
    @Test
    void deleteProject_notFound(){

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.deleteProjectById(1L));
    }

    @Test
    void deleteProject_notOwner(){

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        doThrow(new RuntimeException("Access denied"))
                .when(securityContextService).requireOwnerOrAdmin(project);

        assertThrows(RuntimeException.class,
                () -> projectService.deleteProjectById(1L));
    }
}