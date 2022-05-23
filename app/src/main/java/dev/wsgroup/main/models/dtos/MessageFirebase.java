package dev.wsgroup.main.models.dtos;

import java.io.Serializable;

public class MessageFirebase implements Serializable {
    private String id, from, message, status, to, file;

    public MessageFirebase() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "MessageFirebase{" +
                "id='" + id + '\'' +
                ", from='" + from + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", to='" + to + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
