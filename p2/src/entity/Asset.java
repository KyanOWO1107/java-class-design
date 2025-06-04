package entity;

import java.util.Date;

public class Asset {
    private String assetId;
    private String assetName;
    private String type;
    private String model;
    private double price;
    private Date purchaseDate;
    private int status;
    private String remark;

    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }
    
    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}