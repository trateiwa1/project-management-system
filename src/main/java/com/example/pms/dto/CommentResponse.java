package com.example.pms.dto;

public class CommentResponse {

    private Long id;
    private String content;
    private Long userId;
    private String username;

    public CommentResponse(Long id, String content, Long userId, String username){
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.username = username;
    }

    public Long getId(){
        return id;
    }

    public String getContent(){
        return content;
    }

    public Long getUserId(){
        return userId;
    }

    public String getUsername(){
        return username;
    }
}