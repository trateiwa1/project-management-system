package com.example.pms.controller;

import com.example.pms.dto.CommentRequest;
import com.example.pms.dto.CommentResponse;
import com.example.pms.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long taskId, @Valid @RequestBody CommentRequest request){

        CommentResponse response = commentService.addComment(taskId, request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(@PathVariable Long taskId, Pageable pageable){
        Page<CommentResponse> comments = commentService.getCommentsByTask(taskId, pageable);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId){

        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }

}
