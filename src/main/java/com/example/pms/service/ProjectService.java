package com.example.pms.service;

import com.example.pms.dto.ProjectRequest;
import com.example.pms.dto.ProjectResponse;
import com.example.pms.enums.RoleInProject;
import com.example.pms.exception.*;
import com.example.pms.model.Project;
import com.example.pms.model.ProjectMember;
import com.example.pms.model.User;
import com.example.pms.repository.ProjectMemberRepository;
import com.example.pms.repository.ProjectRepository;
import com.example.pms.repository.UserRepository;
import com.example.pms.security.SecurityContextService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SecurityContextService securityContextService;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ProjectMemberRepository projectMemberRepository, SecurityContextService securityContextService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.securityContextService = securityContextService;
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {

        User user = securityContextService.getCurrentUser();

        Project project = new Project(request.getName(), request.getDescription());
        projectRepository.save(project);

        ProjectMember projectMember = new ProjectMember(project, user, RoleInProject.OWNER);
        projectMemberRepository.save(projectMember);

        return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
    }

    public Page<ProjectResponse> getMyProjects(Pageable pageable) {

        User user = securityContextService.getCurrentUser();

        Page<ProjectMember> projectMemberPage = projectMemberRepository.findByUser(user, pageable);

        return projectMemberPage.map(projectMember -> {

            Project project = projectMember.getProject();

            return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
        });
    }

    public ProjectResponse getProjectById(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        securityContextService.requireMember(project);

        return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
    }

    public void addMember(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        securityContextService.requireOwnerOrAdmin(project);

        User addUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isMember = projectMemberRepository
                .existsByUserAndProject(addUser, project);

        if (isMember) {
            throw new BadRequestException("The user is already a part of the project");
        }

        ProjectMember projectMember = new ProjectMember(project, addUser, RoleInProject.MEMBER);
        projectMemberRepository.save(projectMember);
    }

    @Transactional
    public void deleteProjectById(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        securityContextService.requireOwnerOrAdmin(project);

        projectMemberRepository.deleteByProject(project);
        projectRepository.delete(project);

    }

}
