//package com.infy.pintrest.entity;
//
//public class SavedPin {
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



public class SavedPin {



@Id

@GeneratedValue(strategy = GenerationType.IDENTITY)

private Integer id;



@ManyToOne

@JoinColumn(name="pin_id")

private Pin pin;



@ManyToOne

@JoinColumn(name="user_id")

private User user;



private LocalDateTime savedAt = LocalDateTime.now();

}