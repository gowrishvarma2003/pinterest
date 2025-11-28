//package com.infy.pintrest.dto;
//
//public class UserDto {
//
//}

package com.infy.pintrest.dto;

import com.infy.pinterest.enums.AccountType;
import com.infy.pintrest.entity.User;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class UserDto {

	private Integer userId;
	private String name;
	private String email;
	private String password;
	private String fullname;
	private String mobile;
	private String bio;

	@Enumerated(EnumType.STRING)
	private AccountType accountType = AccountType.USER;

	private String businessName;
	private String websiteUrl;
	private String description;

	private User user;

	public UserDto() {
		super();
	}

	public UserDto(Integer id, String name, String email, String password) {
		super();
		this.userId = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer id) {
		this.userId = id;
	}

	// Retained for backward compatibility where getId/setId might be used
	public Integer getId() {
		return userId;
	}

	public void setId(Integer id) {
		this.userId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
