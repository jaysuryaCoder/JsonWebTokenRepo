package com.orageTechnology.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orageTechnology.entity.RefreshToken;
import com.orageTechnology.repository.RefreshTokenRepository;
import com.orageTechnology.repository.UserInfoRepository;

@Service
public class RefreshTokenService {

	@Autowired
	private RefreshTokenRepository repository;

	@Autowired
	private UserInfoRepository userInfoRepository;

	public RefreshToken createRefreshToken(String userName) {
		RefreshToken token = RefreshToken.builder().userInfo(userInfoRepository.findByName(userName).get())
				.refreshToken(UUID.randomUUID().toString()).expiryDate(Instant.now().plusMillis(1800000)).build();

		return repository.save(token);

	}

	public Optional<RefreshToken> findByRefreshToken(String token) {
		return repository.findByRefreshToken(token);
	}

	public RefreshToken verifyToken(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			repository.delete(token);
			throw new RuntimeException(token.getRefreshToken() + " Refresh Token has been expired please login again ");
		}
		return token;
	}

}
