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
import com.example.pms.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, UserRepository userRepository, ProjectMemberRepository projectMemberRepository){
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public User getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public CommentResponse addComment(Long taskId, CommentRequest request){

        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Project project = task.getProject();

        boolean isMember = projectMemberRepository.existsByUserAndProject(currentUser,project);

        if(!isMember){
            throw new UnauthorizedActionException("Only members of this project can add a comment");
        }

        Comment comment = new Comment(request.getContent(), task, currentUser);

        commentRepository.save(comment);

        return new CommentResponse(comment.getId(), comment.getContent(), currentUser.getId(), currentUser.getEmail());

    }

    public Page<CommentResponse> getCommentsByTask(Long taskId, Pageable pageable) {

        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Project project = task.getProject();

        boolean isMember = projectMemberRepository.existsByUserAndProject(currentUser, project);

        if (!isMember) {
            throw new UnauthorizedActionException("Only members of this project can view comments");
        }

        Page<Comment> commentPage = commentRepository.findByTask(task, pageable);

        return commentPage.map(comment -> new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getUser().getEmail()
        ));
    }

    public void deleteComment(Long commentId){

        User currentUser = getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        Task task = comment.getTask();

        Project project = task.getProject();

        boolean isCommentOwner = comment.getUser().getId().equals(currentUser.getId());

        boolean isProjectOwner = projectMemberRepository.existsByUserAndProjectAndRole(currentUser, project, RoleInProject.OWNER);

        boolean isAdmin = currentUser.getRole() == GlobalRole.ADMIN;

        if(!isCommentOwner && !isProjectOwner && !isAdmin){
            throw new UnauthorizedActionException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
