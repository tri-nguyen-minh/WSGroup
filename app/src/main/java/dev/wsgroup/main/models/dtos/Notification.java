package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class Notification implements Serializable {

    private String id, message, link, createdDate;
    private boolean notificationRead;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean getNotificationRead() {
        return notificationRead;
    }

    public void setNotificationRead(boolean notificationRead) {
        this.notificationRead = notificationRead;
    }

    public static Notification getObjectFromJSON(JSONObject data) throws Exception {
        Notification notification = new Notification();
        notification.setId(data.getString("id"));
        notification.setMessage(data.getString("message"));
        notification.setLink(data.getString("link"));
        notification.setNotificationRead(data.getString("status").equals("read"));
        notification.setCreatedDate(data.getString("createdAt"));
        return notification;
    }
}
