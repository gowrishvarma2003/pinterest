//package com.infy.pintrest.entity;
//
//public class Board {
//
//}

package com.infy.pintrest.entity;



import java.util.ArrayList;

import java.util.List;



import jakarta.persistence.Column;

import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;

import jakarta.persistence.OneToMany;

import jakarta.persistence.Table;

import lombok.Data;



@Entity

@Data

@Table(name = "boards")

public class Board {

@Id

@GeneratedValue(strategy=GenerationType.IDENTITY)

private Integer id;



private String title;



@Column(length=2000)

private String description;



private Boolean isPrivate;



private String coverImageUrl;



private boolean showcase=false;



@ManyToOne

@JoinColumn(name="owner_id", nullable = false)

private User owner;



@OneToMany(mappedBy="board")

private List<Pin> pins = new ArrayList<>();



}






