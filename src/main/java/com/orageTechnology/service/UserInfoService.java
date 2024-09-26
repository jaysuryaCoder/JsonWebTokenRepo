package com.orageTechnology.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.orageTechnology.entity.UserInfo;
import com.orageTechnology.repository.UserInfoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {
	@Autowired
	private UserInfoRepository userInfoRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		Optional<UserInfo> userInfo = userInfoRepository.findByName(userName);
		return userInfo.map(UserInfoDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found" + userName));
	}

	public String addUser(UserInfo userInfo) {
		userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
		userInfoRepository.save(userInfo);
		return "User added successfully";
	}

	public List<UserInfo> getAllUser() {
		return userInfoRepository.findAll();
	}

	public UserInfo getUser(Integer id) {
		return userInfoRepository.findById(id).get();
	}

	public void deleteUser(Integer id) {
		userInfoRepository.deleteById(id);
	}

	public UserInfo updateuser(UserInfo updatedUserInfo, Integer id) {
		return userInfoRepository.findById(id).map(existingUser -> {
			existingUser.setName(updatedUserInfo.getName());
			existingUser.setEmail(updatedUserInfo.getEmail());
			existingUser.setRoles(updatedUserInfo.getRoles());
			existingUser.setPassword(passwordEncoder.encode(updatedUserInfo.getPassword()));
			return userInfoRepository.save(existingUser);
		}).orElse(null);
	}

	public UserInfo updateUserFields(Integer id, UserInfo updatedUserInfo) {
		Optional<UserInfo> existingUserOpt = userInfoRepository.findById(id);
		if (existingUserOpt.isPresent()) {
			UserInfo existingUser = existingUserOpt.get();

			// Only update fields if they are present in the updatedUserInfo object
			if (updatedUserInfo.getName() != null) {
				existingUser.setName(updatedUserInfo.getName());
			}
			if (updatedUserInfo.getEmail() != null) {
				existingUser.setEmail(updatedUserInfo.getEmail());
			}
			if (updatedUserInfo.getPassword() != null) {
				existingUser.setPassword(updatedUserInfo.getPassword());
			}
			if (updatedUserInfo.getRoles() != null) {
				existingUser.setRoles(updatedUserInfo.getRoles());
			}
			return userInfoRepository.save(existingUser); // Save the updated user to the database
		} else {
			throw new RuntimeException("User not found with id: " + id);
		}
	}
}
