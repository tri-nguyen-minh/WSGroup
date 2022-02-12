package dev.wsgroup.main.models.dtos;

import java.io.Serializable;

public class Payment implements Serializable {

    private String id;

    public Payment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
