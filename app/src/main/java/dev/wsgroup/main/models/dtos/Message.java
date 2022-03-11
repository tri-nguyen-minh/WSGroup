package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class Message implements Serializable {

    private String id, fromId, toId, message, createDate;
    private boolean messageRead;
    private Supplier supplier;

    public Message() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public boolean getMessageRead() {
        return messageRead;
    }

    public void setMessageRead(boolean messageRead) {
        this.messageRead = messageRead;
    }

    public static Message getObjectFromJSON(JSONObject data) throws Exception {
        Message message = new Message();
        message.setId(data.getString("id"));
        message.setFromId(data.getString("from"));
        message.setToId(data.getString("to"));
        message.setMessage(data.getString("message"));
        message.setMessageRead(data.getString("status").equals("read"));
        message.setCreateDate(data.getString("createdat"));
        return message;
    }
}
