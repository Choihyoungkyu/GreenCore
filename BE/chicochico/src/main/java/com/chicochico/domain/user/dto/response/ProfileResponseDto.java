package com.chicochico.domain.user.dto.response;


import com.chicochico.domain.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


@Data
@Builder
@JsonAutoDetect // unit test시 object mapper에서 사용
public class ProfileResponseDto implements Serializable {

	private String nickname;
	private String profileImagePath;
	private String introduction;
	private Integer followingCount;
	private Integer followerCount;
	private Boolean isFollowed;


	public static ProfileResponseDto fromEntity(UserEntity user, Function<Long, Boolean> isFollowed) {
		return ProfileResponseDto.builder()
			.nickname(user.getNickname())
			.profileImagePath("/images/" + user.getProfileImagePath())
			.introduction(user.getIntroduction())
			.followingCount(user.getFollowingCount())
			.followerCount(user.getFollowerCount())
			.isFollowed(isFollowed.apply(user.getId()))
			.build();
	}


	public static List<ProfileResponseDto> fromEnityList(List<UserEntity> userList, Function<Long, Boolean> isFollowed) {
		List<ProfileResponseDto> result = new ArrayList<>();
		for (UserEntity user : userList) {
			ProfileResponseDto profileResponseDto = ProfileResponseDto.fromEntity(user, isFollowed);
			result.add(profileResponseDto);
		}
		return result;
	}

}
