package com.chicochico.domain.user.controller;


import com.chicochico.common.dto.ResultDto;
import com.chicochico.domain.user.dto.LoginRequestDto;
import com.chicochico.domain.user.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@Api(tags = "유저 로그인 API")
public class LoginController {

	private final LoginService loginService;


	@PostMapping("/login")
	@ApiOperation(value = "로그인합니다.", notes = "")
	public ResponseEntity<ResultDto<Boolean>> login(@RequestBody LoginRequestDto loginRequestDto) {
		loginService.login(loginRequestDto);
		HttpHeaders httpHeaders = new HttpHeaders();
		// httpHeaders.add(); 헤더에 토큰 추가

		return ResponseEntity.ok().headers(httpHeaders).body(ResultDto.ofSuccess());
	}


	@PostMapping("/login/github")
	@ApiOperation(value = "깃허브로 로그인합니다.", notes = "")
	public ResponseEntity<ResultDto<Boolean>> githubLogin(@RequestBody Map<String, Object> loginRequestHeader) {
		loginService.githubLogin(loginRequestHeader);
		HttpHeaders httpHeaders = new HttpHeaders();
		// httpHeaders.add(); 헤더에 토큰 추가

		return ResponseEntity.ok().headers(httpHeaders).body(ResultDto.ofSuccess());
	}


	@PostMapping("/refresh")
	@ApiOperation(value = "엑세스 토큰을 발급합니다.", notes = "")
	public ResponseEntity<ResultDto<Boolean>> createAccessToken(@RequestHeader Map<String, Object> loginRequestHeader) {
		loginService.createAccessToken(loginRequestHeader);

		return ResponseEntity.ok().body(ResultDto.ofSuccess());
	}


	@DeleteMapping("/logout")
	@ApiOperation(value = "로그아웃합니다.", notes = "")
	public ResponseEntity<ResultDto<Boolean>> logout(@RequestHeader Map<String, Object> logoutRequestHeader) {
		loginService.deleteAccessToken(logoutRequestHeader);

		return ResponseEntity.ok().body(ResultDto.ofSuccess());
	}

}
