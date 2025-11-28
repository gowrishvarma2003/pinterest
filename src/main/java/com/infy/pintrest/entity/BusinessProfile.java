//package com.infy.pintrest.entity;
//
//public class BusinesProfile {
//
//}

package com.infy.pintrest.entity;



import java.util.List;



import jakarta.persistence.Entity;

import jakarta.persistence.FetchType;

import jakarta.persistence.GeneratedValue;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToMany;

import jakarta.persistence.OneToOne;



@Entity

public class BusinessProfile {

@Id

@GeneratedValue(strategy=GenerationType.IDENTITY)

private Integer id;

@OneToOne(fetch = FetchType.EAGER)

@JoinColumn(name="user_id",unique=true)

private User user;



private String businessName;



private String websiteUrl;



private String description;



private String category;



private boolean verified = false;



@OneToMany

private List<Board> showcaseBoards;



public Integer getId() {

return id;

}



public void setId(Integer id) {

this.id = id;

}



public User getUser() {

return user;

}



public void setUser(User user) {

this.user = user;

}



public String getBusinessName() {

return businessName;

}



public void setBusinessName(String businessName) {

this.businessName = businessName;

}



public String getWebsiteUrl() {

return websiteUrl;

}



public void setWebsiteUrl(String websiteUrl) {

this.websiteUrl = websiteUrl;

}



public String getDescription() {

return description;

}



public void setDescription(String description) {

this.description = description;

}

public String getCategory() {

return category;

}

public void setCategory(String category) {

this.category = category;

}

public boolean isVerified() {

return verified;

}

public void setVerified(boolean verified) {

this.verified = verified;

}

}
