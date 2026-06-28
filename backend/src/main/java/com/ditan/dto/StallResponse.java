package com.ditan.dto;

import com.ditan.entity.Stall;
import java.time.LocalDateTime;

public class StallResponse {

    private Long id;
    private String title;
    private String category;
    private double longitude;
    private double latitude;
    private String address;
    private String status;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;

    // ======= 新增的字段 =======
    private String phone;
    private String description;
    private String imageUrl;

    public static StallResponse fromEntity(Stall stall) {
        StallResponse resp = new StallResponse();
        resp.id = stall.getId();
        resp.title = stall.getTitle();
        resp.category = stall.getCategory();
        resp.longitude = stall.getLocation().getX();
        resp.latitude = stall.getLocation().getY();
        resp.address = stall.getAddress();
        resp.status = stall.getStatus();
        resp.expireAt = stall.getExpireAt();
        resp.createdAt = stall.getCreatedAt();
        
        // ======= 新增字段赋值 =======
        resp.phone = stall.getPhone();
        resp.description = stall.getDescription();
        resp.imageUrl = stall.getImageUrl();
        
        return resp;
    }

    // ======= 加上新增字段的 Getter 和 Setter =======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getExpireAt() { return expireAt; }
    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}