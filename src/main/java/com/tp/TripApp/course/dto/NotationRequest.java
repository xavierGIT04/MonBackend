package com.tp.TripApp.course.dto;

public class NotationRequest {
    private Integer note;         // 1 à 5
    private String commentaire;

    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}
