package com.tp.TripApp.notification.dto;

import com.tp.TripApp.notification.entity.Notification;
import java.time.LocalDateTime;

public class NotificationDTO {

    private Long id;
    private String type;
    private String titre;
    private String message;
    private boolean lu;
    private LocalDateTime dateCreation;
    private Long courseId;

    public static NotificationDTO from(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.id = n.getId();
        dto.type = n.getType().name();
        dto.titre = n.getTitre();
        dto.message = n.getMessage();
        dto.lu = n.isLu();
        dto.dateCreation = n.getDateCreation();
        dto.courseId = n.getCourseId();
        return dto;
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public String getTitre() { return titre; }
    public String getMessage() { return message; }
    public boolean isLu() { return lu; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public Long getCourseId() { return courseId; }
}
