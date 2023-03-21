package com.chicochico.domain.user.entity;


import com.chicochico.common.code.IsDeletedType;
import com.chicochico.common.entity.CommonEntity;
import com.chicochico.domain.feed.entity.BookmarkEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "user_table")
public class UserEntity extends CommonEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String profileImagePath;

	@Column(nullable = false)
	private String introduction;

	@Column(nullable = false)
	private Integer followingCount;

	@Column(nullable = false)
	private Integer followerCount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private IsDeletedType isDeleted;

	@OneToMany(mappedBy = "user")
	@Builder.Default
	private List<BookmarkEntity> bookmarkList = new ArrayList<>();

	@OneToMany(mappedBy = "follower")
	@Builder.Default
	private List<FollowEntity> followerList = new ArrayList<>();

	@OneToMany(mappedBy = "following")
	@Builder.Default
	private List<FollowEntity> followingList = new ArrayList<>();


	public void setPassword(String password) {
		this.password = password;
	}


	public void setIsDeleted(IsDeletedType isDeleted) {
		this.isDeleted = isDeleted;
	}


	@PrePersist
	public void prePersist() {
		this.profileImagePath = this.profileImagePath == null ? "default_profileImagePath" : this.profileImagePath;
		this.introduction = this.introduction == null ? "default_introduction" : this.introduction;
		this.followingCount = this.followingCount == null ? 0 : this.followingCount;
		this.followerCount = this.followerCount == null ? 0 : this.followerCount;
		this.isDeleted = this.isDeleted == null ? IsDeletedType.N : this.isDeleted;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}


	/**
	 * 계정의 고유한 값을 리턴
	 *
	 * @return
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Override
	public String getUsername() {
		return this.id.toString();
	}


	/**
	 * 계정 만료 여부
	 * true : 만료 안됨
	 * false : 만료
	 *
	 * @return
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}


	/**
	 * 계정 잠김 여부
	 * true : 잠기지 않음
	 * false : 잠김
	 *
	 * @return
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Override
	public boolean isAccountNonLocked() {
		return isDeleted == IsDeletedType.N;
	}


	/**
	 * 비밀번호 만료 여부
	 * true : 만료 안됨
	 * false : 만료
	 *
	 * @return
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}


	/**
	 * 사용자 활성화 여부
	 * ture : 활성화
	 * false : 비활성화
	 *
	 * @return
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Override
	public boolean isEnabled() {
		return isDeleted == IsDeletedType.N;
	}

}
