package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class CampaignMilestone implements Serializable {
    private int quantity;
    private double price;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static CampaignMilestone getObjectFromJSON(JSONObject data) throws Exception {
        CampaignMilestone milestone = new CampaignMilestone();
        milestone.setQuantity(data.getInt("quantity"));
        milestone.setPrice(data.getDouble("price"));
        return milestone;
    }
}
