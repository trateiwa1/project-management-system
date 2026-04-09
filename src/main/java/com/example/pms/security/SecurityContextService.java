package com.example.pms.security;

import com.example.pms.enums.GlobalRole;
import com.example.pms.enums.RoleInProject;
import com.example.pms.exception.ResourceNotFoundException;
import com.example.pms.exception.UnauthorizedActionException;
import com.example.pms.model.Project;
import com.example.pms.model.User;
import com.example.pms.repository.ProjectMemberRepository;
import com.example.pms.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {

    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public SecurityContextService(UserRepository userRepository, ProjectMemberRepository projectMemberRepository){
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public GlobalRole getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    public boolean isAdmin() {
        return getCurrentUserRole() == GlobalRole.ADMIN;
    }

    public boolean isMember(Project project) {
        return projectMemberRepository.existsByUserAndProject(getCurrentUser(), project);
    }

    public boolean isOwner(Project project) {
        return projectMemberRepository.existsByUserAndProjectAndRole(getCurrentUser(), project, RoleInProject.OWNER);
    }

    public boolean isOwnerOrAdmin(Project project) {
        return isOwner(project) || isAdmin();
    }

    public boolean isMemberOrAdmin(Project project) {
        return isMember(project) || isAdmin();
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new UnauthorizedActionException("Access denied: ADMIN role required");
        }
    }

    public void requireMember(Project project) {
        if (!isMember(project)) {
            throw new UnauthorizedActionException("Access denied: MEMBER role required");
        }
    }

    public void requireOwner(Project project) {
        if (!isOwner(project)) {
            throw new UnauthorizedActionException("Access denied: OWNER role required");
        }
    }

    public void requireMemberOrAdmin(Project project) {
        if (!isMemberOrAdmin(project)) {
            throw new UnauthorizedActionException("Access denied: MEMBER or ADMIN role required");
        }
    }

    public void requireOwnerOrAdmin(Project project) {
        if (!isOwnerOrAdmin(project)) {
            throw new UnauthorizedActionException("Access denied: OWNER or ADMIN role required");
        }
    }
}