package com.example.pms.service;

import com.example.pms.dto.ProjectRequest;
import com.example.pms.dto.ProjectResponse;
import com.example.pms.enums.GlobalRole;
import com.example.pms.enums.RoleInProject;
import com.example.pms.exception.*;
import com.example.pms.model.Project;
import com.example.pms.model.ProjectMember;
import com.example.pms.model.User;
import com.example.pms.repository.ProjectMemberRepository;
import com.example.pms.repository.ProjectRepository;
import com.example.pms.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public ProjectResponse createProject(ProjectRequest request) {

        User currentUser = getCurrentUser();

        Project project = new Project(request.getName(), request.getDescription());

        projectRepository.save(project);

        ProjectMember projectMember = new ProjectMember(project, currentUser, RoleInProject.OWNER);

        projectMemberRepository.save(projectMember);

        return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
    }


    public Page<ProjectResponse> getMyProjects(Pageable pageable) {

        User currentUser = getCurrentUser();

        Page<ProjectMember> projectectMemberPage = projectMemberRepository.findByUser(currentUser, pageable);

        return projectectMemberPage.map(projectMember -> {

            Project project = projectMember.getProject();

            return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
        });
    }


    public ProjectResponse getProjectById(Long projectId){

        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        boolean isMember = projectMemberRepository
                .existsByUserAndProject(currentUser, project);

        if(!isMember){
            throw new UnauthorizedActionException("You are not a member of this project");
        }

        return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
    }

    public void addMember(Long projectId, Long userId) {

        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        boolean isOwner = projectMemberRepository.existsByUserAndProjectAndRole(currentUser, project, RoleInProject.OWNER);

        boolean isAdmin = currentUser.getRole() == GlobalRole.ADMIN;

        if(!isOwner && !isAdmin){
            throw new UnauthorizedActionException("You are not allowed to add a member to this project");
        }

        User addUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isMember = projectMemberRepository
                .existsByUserAndProject(addUser, project);

        if(isMember){
            throw new BadRequestException("The user is already a part of the project");
        }

        ProjectMember projectMember = new ProjectMember(project, addUser, RoleInProject.MEMBER);

        projectMemberRepository.save(projectMember);
    }

    @Transactional
    public void deleteProjectById(Long projectId) {

        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        boolean isOwner = projectMemberRepository.existsByUserAndProjectAndRole(currentUser, project, RoleInProject.OWNER);

        boolean isAdmin = currentUser.getRole() == GlobalRole.ADMIN;

        if (isAdmin || isOwner) {
            projectMemberRepository.deleteByProject(project);
            projectRepository.delete(project);
        } else {
            throw new UnauthorizedActionException("You are not allowed to delete this project");
        }
    }

}
