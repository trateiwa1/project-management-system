package com.example.pms.service;

import com.example.pms.dto.CommentRequest;
import com.example.pms.dto.CommentResponse;
import com.example.pms.enums.GlobalRole;
import com.example.pms.enums.RoleInProject;
import com.example.pms.exception.ResourceNotFoundException;
import com.example.pms.exception.UnauthorizedActionException;
import com.example.pms.model.Comment;
import com.example.pms.model.Project;
import com.example.pms.model.Task;
import com.example.pms.model.User;
import com.example.pms.repository.CommentRepository;
import com.example.pms.repository.ProjectMemberRepository;
import com.example.pms.repository.TaskRepository;
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
public class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private SecurityContextService securityContextService;

    @InjectMocks
    private CommentService commentService;

    private User user1;
    private User user2;
    private Project project;
    private Task task;
    private Comment comment;

    @BeforeEach
    void setUp(){

        user1 = new User("John", "john@test.com", "Password", GlobalRole.USER);
        user1.setId(1L);

        user2 = new User("Henry", "henry@test.com", "Password", GlobalRole.USER);
        user2.setId(2L);

        project = new Project("Project", "First project");
        project.setId(1L);

        task = new Task("Task", "Task description", project, user1, LocalDateTime.now().plusDays(3));
        task.setId(1L);

        comment = new Comment("Comment", task, user1);
        comment.setId(1L);
    }

    //---------------------- SUCCESS CASES ----------------------

    @Test
    void addComment(){

        CommentRequest request = new CommentRequest("My first comment");

        when(securityContextService.getCurrentUser()).thenReturn(user1);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        doNothing().when(securityContextService).requireMember(project);

        CommentResponse result = commentService.addComment(1L, request);

        assertNotNull(result);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void getCommentsByTask(){

        Page<Comment> page = new PageImpl<>(List.of(comment));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(securityContextService).requireMember(project);
        when(commentRepository.findByTask(eq(task), any())).thenReturn(page);

        Page<CommentResponse> result =
                commentService.getCommentsByTask(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void deleteComment_owner(){

        when(securityContextService.getCurrentUser()).thenReturn(user1);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_projectOwner(){

        when(securityContextService.getCurrentUser()).thenReturn(user2);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        when(projectMemberRepository.existsByUserAndProjectAndRole(user2, project, RoleInProject.OWNER))
                .thenReturn(true);

        commentService.deleteComment(1L);
        verify(commentRepository).delete(comment);
    }

    //---------------------- FAILURE CASES ----------------------

    //ADD COMMENT

    @Test
    void addComment_taskNotFound(){

        CommentRequest request = new CommentRequest("My first comment");

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.addComment(1L, request));
    }

    @Test
    void addComment_notMember(){

        CommentRequest request = new CommentRequest("My first comment");

        when(securityContextService.getCurrentUser()).thenReturn(user1);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        doThrow(new UnauthorizedActionException("Not a member"))
                .when(securityContextService).requireMember(project);

        assertThrows(UnauthorizedActionException.class,
                () -> commentService.addComment(1L, request));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    //GET COMMENTS

    @Test
    void getCommentsByTask_taskNotFound(){

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.getCommentsByTask(1L, PageRequest.of(0, 10)));
    }

    @Test
    void getCommentsByTask_notMember(){

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        doThrow(new UnauthorizedActionException("Not a member"))
                .when(securityContextService).requireMember(project);

        assertThrows(UnauthorizedActionException.class,
                () -> commentService.getCommentsByTask(1L, PageRequest.of(0, 10)));
    }

    //DELETE COMMENT

    @Test
    void deleteComment_notAuthorized(){

        when(securityContextService.getCurrentUser()).thenReturn(user2);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        when(projectMemberRepository.existsByUserAndProjectAndRole(user2, project, RoleInProject.OWNER))
                .thenReturn(false);

        assertThrows(UnauthorizedActionException.class,
                () -> commentService.deleteComment(1L));
    }

    @Test
    void deleteComment_notFound(){

        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.deleteComment(1L));
    }
}