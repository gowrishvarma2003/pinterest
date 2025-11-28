//package com.infy.pintrest.entity;
//
//public class SponsoredPin {
//
//}
package com.infy.pintrest.entity;



import java.time.LocalDateTime;



import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;

import lombok.Data;



@Entity

@Data
public class SponsoredPin {

// @Id

// @GeneratedValue(strategy=GenerationType.IDENTITY)

// private Integer id;

// @ManyToOne

// @JoinColumn(name="business_profile_id")

// private BusinessProfile bussinessProfile;

// @ManyToOne

// @JoinColumn(name="pin_id")

// private Pin pin;

// private String campaignTitle;

// private Double budget;

// private String startDate;

// private String endDate;

@Id

@GeneratedValue(strategy = GenerationType.IDENTITY)

private Long id;



private String title;

private String description;

private String imageUrl;

private String targetUrl;

private String advertiseName;

private String keywords; // comma seperated :"travel,food,shopping"

private Double budget; // remaining ad budget

private boolean active = true;

private LocalDateTime createdAt = LocalDateTime.now();





}