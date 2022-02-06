package dev.wsgroup.main.models.dtos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartProduct implements Serializable {
    private String id, productType, campaignId;
    private List<String> typeList;
    private int quantity;
    private Product product;
    private boolean selected, selectableFlag;

    public CartProduct() {

    }

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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
        List<String> productTypeList = new ArrayList<>();
        if (!productType.isEmpty()) {
            productTypeList.add(productType);
        }
        productTypeList.add("test");
        this.typeList = productTypeList;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean getSelectableFlag() {
        return selectableFlag;
    }

    public void setSelectableFlag(boolean selectableFlag) {
        this.selectableFlag = selectableFlag;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    public static CartProduct getCartProductFromJSON(JSONObject jsonObject) throws Exception {
        CartProduct cartProduct = new CartProduct();
        Product product = new Product();
        Supplier supplier = new Supplier();
        cartProduct.setId(jsonObject.getString("id"));
        cartProduct.setQuantity(jsonObject.getInt("quantity"));
        cartProduct.setProductType(jsonObject.getString("typeofproduct"));
        cartProduct.setCampaignId("");
        cartProduct.setSelectableFlag(true);
        product.setProductId(jsonObject.getString("productid"));
        product.setName(jsonObject.getString("productname"));
        product.setRetailPrice(jsonObject.getDouble("productretailprice"));
        product.setQuantity(jsonObject.getInt("productquantity"));
        if (product.getQuantity() < cartProduct.getQuantity()) {
            cartProduct.setQuantity(product.getQuantity());
        }
        product.setImageLink(jsonObject.getString("productimage"));
        supplier.setId(jsonObject.getString("supplierid"));
        supplier.setName(jsonObject.getString("suppliername"));
        supplier.setAddress(jsonObject.getString("supplieraddress"));
        product.setSupplier(supplier);
        cartProduct.setProduct(product);

//        work with type of product

        return cartProduct;
    }
}
