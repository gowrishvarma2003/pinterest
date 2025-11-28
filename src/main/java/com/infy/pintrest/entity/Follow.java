//package com.infy.pintrest.entity;
//
//public class Follow {
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

import jakarta.persistence.Table;

import lombok.Data;



@Entity

@Table(name="follow")

@Data

public class Follow {

@Id

@GeneratedValue(strategy=GenerationType.IDENTITY)

private Integer id;

@ManyToOne

@JoinColumn(name="follower_id", nullable = false)

private User follower;

@ManyToOne

@JoinColumn(name="following_id", nullable = false)

private User following;



private LocalDateTime createdAt = LocalDateTime.now();

}