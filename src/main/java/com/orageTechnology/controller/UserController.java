package com.orageTechnology.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orageTechnology.entity.AuthRequest;
import com.orageTechnology.entity.RefreshToken;
import com.orageTechnology.entity.UserInfo;
import com.orageTechnology.logout.BlackList;
import com.orageTechnology.repository.UserInfoRepository;
import com.orageTechnology.response.AuthResponse;
import com.orageTechnology.response.RefreshTokenResponse;
import com.orageTechnology.service.JwtService;
import com.orageTechnology.service.RefreshTokenService;
import com.orageTechnology.service.UserInfoService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class UserController {
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;

	@Autowired
	private BlackList blackList;

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to Spring Security tutorials !!";
	}

	@PostMapping("/addUser")
	public String addUser(@RequestBody UserInfo userInfo) {
		return userInfoService.addUser(userInfo);
	}

	@PostMapping("/login")
	public AuthResponse login(@RequestBody AuthRequest authRequest) {
		Authentication authenticate = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));

		if (authenticate.isAuthenticated()) {
			UserInfo user = userInfoRepository.findByName(authRequest.getUserName()).orElseThrow(
					() -> new UsernameNotFoundException("User not found with email: " + authRequest.getUserName()));
			RefreshToken refreshTokens = refreshTokenService.createRefreshToken(authRequest.getUserName()); 
			return AuthResponse.builder().accessToken(jwtService.generateToken(user.getName()))
					.refreshToken(refreshTokens.getRefreshToken()).userId(user.getId()).build();
		} else {
			throw new UsernameNotFoundException("Invalid user credentials");
		}
	}

	@PostMapping("/logout")
	@PreAuthorize("hasAuthority('USER_ROLES') or hasAuthority('ADMIN_ROLES')")
	public String logoutUser(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		String token = null;
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			token = authHeader.substring(7);
		}
		blackList.blacKListToken(token);
		return "You have successfully logged out !!";
	}

	@GetMapping("/getUsers")
	@PreAuthorize("hasAuthority('ADMIN_ROLES') or hasAuthority('USER_ROLES')")
	public List<UserInfo> getAllUsers() {
		return userInfoService.getAllUser();
	}

	@GetMapping("/getUsers/{id}")
	public UserInfo getAllUsers(@PathVariable Integer id) {
		return userInfoService.getUser(id);
	}

	@DeleteMapping("/delete/{id}")
	public String deleteUser(@PathVariable Integer id) {
		userInfoService.deleteUser(id);
		return "User deleted successfully   " + id;
	}

	@PutMapping("/update/{id}")
	public String udateUser(@RequestBody UserInfo info, @PathVariable Integer id) {
		userInfoService.updateuser(info, id);
		return "User Update Successfully ";
	}

	@PatchMapping("/{id}")
	public ResponseEntity<UserInfo> updateUser(@PathVariable Integer id, @RequestBody UserInfo updatedUserInfo) {
		UserInfo updatedUser = userInfoService.updateUserFields(id, updatedUserInfo);
		return ResponseEntity.ok(updatedUser);
	}
	
	@PostMapping("/refresh")
	public AuthResponse refreshToken(@RequestBody RefreshTokenResponse response){
		return refreshTokenService.findByRefreshToken(response.getRefreshToken())
		.map(refreshTokenService::verifyToken)
		.map(RefreshToken::getUserInfo)
		.map(userInfo->{
			String accessToken=jwtService.generateToken(userInfo.getName());
			return AuthResponse.builder()
					.accessToken(accessToken)
					.refreshToken(response.getRefreshToken())
					.userId(userInfo.getId())
					.build();
		}).orElseThrow(()->new RuntimeException("Refersh Token is not in database"));
		
	}

}
