package dev.wsgroup.main.models.dtos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderHistory implements Serializable {

    private Order order;
    private String id, status, description, imageLink, createDate, updateDate, type;
    private List<String> imageList;

    public OrderHistory() {
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
        List<String> imageList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(imageLink);
            if(jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    imageList.add(jsonArray.getJSONObject(i).getString("url"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageList = new ArrayList<>();
        }
        setImageList(imageList);
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public static OrderHistory getObjectFromJSON(JSONObject data) throws Exception {
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setId(data.getString("id"));
        orderHistory.setType(data.getString("type"));
        Order order = new Order();
        if (orderHistory.getType().equals("retail")) {
            order.setId(data.getString("retailorderid"));
        } else {
            order.setId(data.getString("campaignorderid"));
        }
        order.setCode(data.getString("ordercode"));
        orderHistory.setOrder(order);
        orderHistory.setStatus(data.getString("orderstatus"));
        orderHistory.setImageLink(data.getString("image"));
        orderHistory.setDescription(data.getString("description"));
        orderHistory.setCreateDate(data.getString("createdat"));
        orderHistory.setUpdateDate(data.getString("updatedat"));
        return orderHistory;
    }
}
