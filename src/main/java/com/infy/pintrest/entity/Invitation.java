//package com.infy.pintrest.entity;
//
//public class Invitation {
//
//}
package com.infy.pintrest.entity;



import java.time.LocalDateTime;



import com.infy.pinterest.enums.InvitationStatus;



import jakarta.persistence.Entity;

import jakarta.persistence.EnumType;

import jakarta.persistence.Enumerated;

import jakarta.persistence.GeneratedValue;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;

import lombok.Data;



@Entity

@Data

public class Invitation {

@Id

@GeneratedValue(strategy=GenerationType.IDENTITY)

private Integer id;

@ManyToOne

@JoinColumn(name="sender_id")

private User sender;

@ManyToOne

@JoinColumn(name="receiver_id")

private User receiver;

//private String type;

@ManyToOne

@JoinColumn(name="board_id",nullable=true)

private Board board;

@Enumerated(EnumType.STRING)

private InvitationStatus status=InvitationStatus.PENDING;

private LocalDateTime sentAt=LocalDateTime.now();

}

