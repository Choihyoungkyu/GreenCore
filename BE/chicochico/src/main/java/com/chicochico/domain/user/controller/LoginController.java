package com.chicochico.domain.user.controller;


import com.chicochico.common.dto.ResultDto;
import com.chicochico.domain.user.dto.request.LoginRequestDto;
import com.chicochico.domain.user.dto.request.RefreshRequestDto;
import com.chicochico.domain.user.dto.response.ProfileSimpleResponseDto;
import com.chicochico.domain.user.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Log4j2
@RestController
@RequiredArgsConstructor
@Api(tags = "유저 로그인 API")
public class LoginController {

	private final LoginService loginService;


	@PostMapping("/login")
	@ApiOperation(value = "로그인합니다.", notes = "")
	public ResponseEntity<ResultDto<ProfileSimpleResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
		ProfileSimpleResponseDto profileSimpleResponseDto = loginService.login(loginRequestDto, response);

		return ResponseEntity.ok().body(ResultDto.of(profileSimpleResponseDto));
	}


	@PostMapping("/login/oauth")
	@ApiOperation(value = "구글/깃허브로 로그인합니다. (firebase)", notes = "")
	public ResponseEntity<ResultDto<ProfileSimpleResponseDto>> oauthLogin(@RequestHeader Map<String, String> loginRequestHeader, HttpServletResponse response) {
		log.info("[oauthLogin] Controller 들어옴");
		ProfileSimpleResponseDto profileSimpleResponseDto = loginService.oauthLogin(loginRequestHeader, response);

		return ResponseEntity.ok().body(ResultDto.of(profileSimpleResponseDto));
	}


	@PostMapping("/login/kakao")
	@ApiOperation(value = "카카오로 로그인합니다. (firebase)", notes = "")
	public ResponseEntity<ResultDto<ProfileSimpleResponseDto>> kakaoLogin(@RequestHeader Map<String, String> loginRequestHeader, HttpServletResponse response) {
		log.info("[kakaoLogin] Controller 들어옴");
		ProfileSimpleResponseDto profileSimpleResponseDto = loginService.kakaoLogin(loginRequestHeader, response);

		return ResponseEntity.ok().body(ResultDto.of(profileSimpleResponseDto));
	}


	@PostMapping("/refresh")
	@ApiOperation(value = "엑세스 토큰을 재발급합니다.", notes = "")
	public ResponseEntity<ResultDto<Boolean>> createAccessToken(@RequestHeader Map<String, String> loginRequestHeader, HttpServletResponse response, @RequestBody RefreshRequestDto refreshRequestDto) {
		loginService.createAccessToken(loginRequestHeader, response, refreshRequestDto);

		return ResponseEntity.ok().body(ResultDto.ofSuccess());
	}


	@GetMapping("/logout/redirect")
	@ApiOperation(value = "로그아웃합니다.", notes = "")
	public ResponseEntity<ResultDto<Boolean>> logoutPost(@RequestHeader Map<String, String> logoutRequestHeader) {
		loginService.deleteAccessToken(logoutRequestHeader);

		return ResponseEntity.ok().body(ResultDto.ofSuccess());
	}

}
