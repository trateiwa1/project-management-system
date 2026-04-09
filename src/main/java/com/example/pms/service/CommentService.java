package com.example.pms.service;

import com.example.pms.dto.CommentRequest;
import com.example.pms.dto.CommentResponse;
import com.example.pms.enums.GlobalRole;
import com.example.pms.enums.RoleInProject;
import com.example.pms.exception.ResourceNotFoundException;
import com.example.pms.exception.UnauthorizedActionException;
import com.example.pms.model.*;
import com.example.pms.repository.CommentRepository;
import com.example.pms.repository.ProjectMemberRepository;
import com.example.pms.repository.TaskRepository;
import com.example.pms.security.SecurityContextService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SecurityContextService securityContextService;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, ProjectMemberRepository projectMemberRepository, SecurityContextService securityContextService){
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.securityContextService = securityContextService;
    }

    @Transactional
    public CommentResponse addComment(Long taskId, CommentRequest request){

        User user = securityContextService.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Project project = task.getProject();

        securityContextService.requireMember(project);

        Comment comment = new Comment(request.getContent(), task, user);
        commentRepository.save(comment);

        return new CommentResponse(comment.getId(), comment.getContent(), user.getId(), user.getEmail());

    }

    public Page<CommentResponse> getCommentsByTask(Long taskId, Pageable pageable) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Project project = task.getProject();

        securityContextService.requireMember(project);

        Page<Comment> commentPage = commentRepository.findByTask(task, pageable);

        return commentPage.map(comment -> new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getUser().getEmail()
        ));
    }

    public void deleteComment(Long commentId){

        User user = securityContextService.getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        Task task = comment.getTask();

        Project project = task.getProject();

        boolean isCommentOwner = comment.getUser().getId().equals(user.getId());

        boolean isProjectOwner = projectMemberRepository.existsByUserAndProjectAndRole(user, project, RoleInProject.OWNER);

        boolean isAdmin = user.getRole() == GlobalRole.ADMIN;

        if(!isCommentOwner && !isProjectOwner && !isAdmin){
            throw new UnauthorizedActionException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
