package com.ditan.entity;

import org.locationtech.jts.geom.Point;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stall")
public class Stall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "category", length = 30)
    private String category;

    @Column(name = "location", columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point location;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "status", nullable = false)
    private String status = "OPEN";

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ======= 新增字段 =======
    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.expireAt == null) {
            this.expireAt = this.createdAt.plusHours(6);
        }
    }

    // ======= Getter & Setter (包括新增的) =======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Point getLocation() { return location; }
    public void setLocation(Point location) { this.location = location; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
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