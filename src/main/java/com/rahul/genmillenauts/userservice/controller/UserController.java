package com.rahul.genmillenauts.userservice.controller;


import com.rahul.genmillenauts.global.config.CustomUserDetails;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rahul.genmillenauts.userservice.dto.UserFullResponse;
import com.rahul.genmillenauts.userservice.dto.UserPublicResponse;
import com.rahul.genmillenauts.userservice.dto.UserUpdateDto;

import com.rahul.genmillenauts.userservice.userserviceinner.Userservice;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	
	private final Userservice userService ;
	
	@PostConstruct
	public void init() {
	    System.out.println("✅ UserController loaded!");
	}

	//Get your profile
	@GetMapping("/me")
	public ResponseEntity<UserFullResponse> getMyProfile(Authentication auth){
		String email =auth.getName();
		
		return ResponseEntity.ok(userService.getMyProfile(email));
		
	}
	
	//Get any id
	@GetMapping("/{anyName}")
		public ResponseEntity<UserPublicResponse> getUserPublicProfile(@PathVariable String anyName){
		return ResponseEntity.ok(userService.getUserPublicProfile(anyName));
	}
	
	
	//Updaate City And OfflineMeet
	
	@PutMapping("/me")
	public ResponseEntity<UserUpdateDto> updateProfile(
			@RequestBody @Valid UserUpdateDto updateDto,
			@AuthenticationPrincipal CustomUserDetails currentUser
			){
		
		UserUpdateDto updated =userService.updateUserProfile(currentUser.getId(), updateDto);
		
		return ResponseEntity.ok(updated);
		
	}
	
	
	
}
