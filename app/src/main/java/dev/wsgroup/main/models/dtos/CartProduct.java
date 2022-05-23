package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartProduct implements Serializable {
    private String id;
    private List<String> typeList;
    private int quantity;
    private Product product;
    private Campaign campaign;
    private boolean selectedFlag, selectableFlag, campaignFlag;

    public CartProduct() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(boolean selectedFlag) {
        this.selectedFlag = selectedFlag;
    }

    public boolean getSelectableFlag() {
        return selectableFlag;
    }

    public void setSelectableFlag(boolean selectableFlag) {
        this.selectableFlag = selectableFlag;
    }

    public boolean getCampaignFlag() {
        return campaignFlag;
    }

    public void setCampaignFlag(boolean campaignFlag) {
        this.campaignFlag = campaignFlag;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    public static CartProduct getObjectFromJSON(JSONObject jsonObject) throws Exception {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setId(jsonObject.getString("id"));
        cartProduct.setQuantity(jsonObject.getInt("quantity"));
        cartProduct.setCampaignFlag(jsonObject.getBoolean("inCampaign"));
        if (cartProduct.getCampaignFlag()) {
            Campaign campaign = new Campaign();
            campaign.setId(jsonObject.getString("campaignid"));
            cartProduct.setCampaign(campaign);
        }
        cartProduct.setSelectableFlag(true);
        Product product = new Product();
        product.setProductId(jsonObject.getString("productid"));
        product.setName(jsonObject.getString("productname"));
        product.setRetailPrice(jsonObject.getDouble("productretailprice"));
        product.setQuantity(jsonObject.getInt("productquantity"));
        product.setImageLink(jsonObject.getString("productimage"));
        product.setWeight(jsonObject.getDouble("productweight"));
        Supplier supplier = new Supplier();
        supplier.setId(jsonObject.getString("supplierid"));
        supplier.setName(jsonObject.getString("suppliername"));
        supplier.setAddressString(jsonObject.getString("supplieraddress"));
        product.setSupplier(supplier);
        cartProduct.setProduct(product);
        return cartProduct;
    }
}
