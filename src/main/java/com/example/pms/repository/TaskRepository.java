package com.example.pms.repository;

import com.example.pms.model.Project;
import com.example.pms.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByProject(Project project, Pageable pageable);
}
