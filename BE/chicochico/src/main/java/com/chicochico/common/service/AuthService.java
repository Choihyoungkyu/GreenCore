package com.chicochico.common.service;


import com.chicochico.domain.user.entity.UserEntity;
import com.chicochico.exception.CustomException;
import com.chicochico.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@Log4j2
@RequiredArgsConstructor
public class AuthService {

	//	private final AuthTokenProvider authTokenProvider;


	public Long getUserId() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal != null && principal instanceof UserEntity) {
			UserEntity user = (UserEntity) principal;
			return user.getId();
		} else {
			log.error("Context에 유저 정보가 저장되어있지 않습니다.");
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}
	}


	public String getUserNickname() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal != null && principal instanceof UserEntity) {
			UserEntity user = (UserEntity) principal;
			return user.getNickname();
		} else {
			log.error("Context에 유저 정보가 저장되어있지 않습니다.");
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}
	}

}
