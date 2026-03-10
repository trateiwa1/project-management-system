package com.example.pms.repository;

import com.example.pms.enums.RoleInProject;
import com.example.pms.model.Project;
import com.example.pms.model.ProjectMember;
import com.example.pms.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByUserAndProjectAndRole(User user, Project project, RoleInProject roleInProject);

    boolean existsByUserAndProject(User user, Project project);

    Page<ProjectMember> findByUser(User user, Pageable pageable);

    void deleteByProject(Project project);
}
