package dev.wsgroup.main.models.dtos;

import java.io.Serializable;

public class Conversation implements Serializable {
    private User mainUser;
    private Supplier supplier;
    private boolean readStatus, userMessageStatus;
    private Message lastMessage;

    public Conversation() { }

    public User getMainUser() {
        return mainUser;
    }

    public void setMainUser(User mainUser) {
        this.mainUser = mainUser;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public boolean getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public boolean getUserMessageStatus() {
        return userMessageStatus;
    }

    public void setUserMessageStatus(boolean userMessageStatus) {
        this.userMessageStatus = userMessageStatus;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
        if (lastMessage != null && this.mainUser != null) {
            this.userMessageStatus = lastMessage.getFromId().equals(mainUser.getAccountId());
            this.readStatus = lastMessage.getMessageRead() || this.userMessageStatus;
        }
    }
}
