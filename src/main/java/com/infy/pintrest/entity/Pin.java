//package com.infy.pintrest.entity;
//
//public class Pin {
//
//}
package com.infy.pintrest.entity;



import jakarta.persistence.Column;

import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;

import jakarta.persistence.Table;

import lombok.Data;



@Entity

@Table(name = "pins")

@Data

public class Pin {

@Id

@GeneratedValue(strategy=GenerationType.IDENTITY)

private Integer id;

private String title;



@Column(length=2000)

private String description;



private String imageUrl;



private String videoUrl;



private String sourceUrl;



private String keywords;



private boolean isPrivate;



private int likes=0;



@ManyToOne

@JoinColumn(name="user_id", nullable = false)

private User user;



@ManyToOne

@JoinColumn(name="board_id")

private Board board;



}









