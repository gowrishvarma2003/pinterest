package com.infy.pintrest.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.infy.pinterest.enums.PinStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "pins")
@Data
public class Pin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(length = 2000)
    private String description;
    private String imageUrl;
    private String videoUrl;
    private String sourceUrl;
    private String keywords;
    private boolean isPrivate;
    private int likes = 0;

    @Column(length = 500)
    private String topics;

    @Column(length = 500)
    private String productTags;

    @Column(length = 500)
    private String attribution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PinStatus status = PinStatus.DRAFT;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"pins", "boards", "password", "failedLoginAttemps", "lastFailedAttempt", "businessProfile"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id")
    @JsonIgnoreProperties({"pins", "owner"})
    private Board board;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
