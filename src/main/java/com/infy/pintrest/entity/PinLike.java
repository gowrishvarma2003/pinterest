//package com.infy.pintrest.entity;
//
//public class PinLike {
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

@Table(name="pin_likes")

@Data



public class PinLike {



@Id

@GeneratedValue(strategy=GenerationType.IDENTITY)

private Integer id;



@ManyToOne

@JoinColumn(name="user_id")

private User user;



@ManyToOne

@JoinColumn(name="pin_id")

private Pin pin;





private LocalDateTime likedAt = LocalDateTime.now();





}
