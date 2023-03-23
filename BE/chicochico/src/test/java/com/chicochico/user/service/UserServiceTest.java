package com.chicochico.user.service;


import com.chicochico.common.code.IsDeletedType;
import com.chicochico.common.service.AuthService;
import com.chicochico.domain.alert.entity.AlertEntity;
import com.chicochico.domain.alert.service.AlertService;
import com.chicochico.domain.feed.entity.DiarySetEntity;
import com.chicochico.domain.feed.entity.FeedEntity;
import com.chicochico.domain.feed.entity.LikeEntity;
import com.chicochico.domain.feed.repository.DiarySetRepository;
import com.chicochico.domain.feed.service.DiarySetService;
import com.chicochico.domain.feed.service.FeedService;
import com.chicochico.domain.schedule.entity.ScheduleEntity;
import com.chicochico.domain.schedule.service.ScheduleService;
import com.chicochico.domain.user.dto.request.PasswordRequestDto;
import com.chicochico.domain.user.dto.request.RegisterRequestDto;
import com.chicochico.domain.user.entity.UserEntity;
import com.chicochico.domain.user.entity.UserPlantEntity;
import com.chicochico.domain.user.repository.UserPlantRepository;
import com.chicochico.domain.user.repository.UserRepository;
import com.chicochico.domain.user.service.FollowService;
import com.chicochico.domain.user.service.UserService;
import com.chicochico.exception.CustomException;
import com.chicochico.exception.ErrorCode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	private final String testNickname = "test";
	private final String testEmail = "test@naver.com";
	private final String testPassword = "1234";

	private final String testEncodePassword = "$2a$10$gIclEIh2XaBGnbhkTTqCD./2zjhl0nitd6Hi2S3mgnPMaQQrdmP8a";
	//	1	2023-03-14 13:59:49	2023-03-14 13:59:49	test@naver.com	0	0	default_profileImagePath	N	test	$2a$10$gIclEIh2XaBGnbhkTTqCD./2zjhl0nitd6Hi2S3mgnPMaQQrdmP8a	default_profileImagePath
	private final UserEntity user = UserEntity.builder()
		.id(1L)
		.createdAt(LocalDateTime.now())
		.updatedAt(LocalDateTime.now())
		.followerCount(0)
		.followingCount(0)
		.profileImagePath("default_profileImagePath")
		.isDeleted(IsDeletedType.N)
		.nickname(testNickname)
		.password(testEncodePassword)
		.introduction("default_introduction")
		.build();
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Mock
	private AuthService authService;
	@Mock
	private DiarySetService diarySetService;
	@Mock
	private ScheduleService scheduleService;
	@Mock
	private FeedService feedService;
	@Mock
	private AlertService alertService;
	@Mock
	private FollowService followService;
	@Mock
	private UserPlantRepository userPlantRepository;
	@Mock
	private DiarySetRepository diarySetRepository;

	@Nested
	@DisplayName("이메일 중복")
	public class 이메일중복 {

		@Test
		@DisplayName("이메일 중복 - 중복")
		void checkEmailTest_이메일중복() {
			// given
			Mockito.when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));

			// when
			Boolean checkEmail = userService.checkEmail(testEmail);

			// then
			Assertions.assertEquals(checkEmail, Boolean.FALSE);

		}


		@Test
		@DisplayName("이메일 중복 - 중복아님 통과")
		void checkEmailTest_이메일중복아님() {
			// given
			Optional<UserEntity> givenNullUser = Optional.empty();
			Mockito.when(userRepository.findByEmail(testEmail)).thenReturn(givenNullUser);

			// when
			Boolean checkEmail = userService.checkEmail(testEmail);

			// then
			Assertions.assertEquals(checkEmail, Boolean.TRUE);
		}

	}

	@Nested
	@DisplayName("닉네임 중복")
	public class 닉네임중복 {

		@Test
		@DisplayName("닉네임 중복 - 중복")
		void checkNicknameTest_닉네임중복() {
			// given
			Mockito.when(userRepository.findByNickname(testNickname)).thenReturn(Optional.of(user));

			// when
			Boolean checkNickname = userService.checkNickname(testNickname);

			// then
			Assertions.assertEquals(checkNickname, Boolean.FALSE);
		}


		@Test
		@DisplayName("닉네임 중복 - 중복아님 통과")
		void checkNicknameTest_닉네임중복아님() {
			// given
			Optional<UserEntity> givenNullUser = Optional.empty();
			Mockito.when(userRepository.findByNickname(testNickname)).thenReturn(givenNullUser);

			// when
			Boolean checkNickname = userService.checkNickname(testNickname);

			// then
			Assertions.assertEquals(checkNickname, Boolean.TRUE);

		}

	}

	@Nested
	@DisplayName("회원가입")
	public class 회원가입 {

		@Test
		@DisplayName("회원가입 - 이미 존재하는 이메일일 경우")
		void createUserTest_존재하는이메일() {
			// given
			Optional<UserEntity> givenUser = Optional.of(user);
			Mockito.when(userRepository.findByEmail(testEmail)).thenReturn(givenUser);

			// when
			CustomException customException = Assertions.assertThrows(CustomException.class, () -> {
				userService.createUser(new RegisterRequestDto(testEmail, testNickname, testPassword));
			});

			// then
			Assertions.assertEquals(customException.getErrorCode(), ErrorCode.DUPLICATE_RESOURCE);

		}


		@Test
		@DisplayName("회원가입 - 이미 존재하는 닉네임일 경우")
		void createUserTest_존재하는닉네임() {
			// given
			Optional<UserEntity> givenNullUser = Optional.empty();
			Mockito.when(userRepository.findByEmail(testEmail)).thenReturn(givenNullUser);

			Optional<UserEntity> givenUser = Optional.of(user);
			Mockito.when(userRepository.findByNickname(testNickname)).thenReturn(givenUser);

			// when
			CustomException customException = Assertions.assertThrows(CustomException.class, () -> {
				userService.createUser(new RegisterRequestDto(testEmail, testNickname, testPassword));
			});

			// then
			Assertions.assertEquals(customException.getErrorCode(), ErrorCode.DUPLICATE_RESOURCE);

		}


		@Test
		@DisplayName("회원가입 - 성공")
		void createUserTest_회원가입성공() {
			// given
			Optional<UserEntity> givenNullUser = Optional.empty();
			Mockito.when(userRepository.findByEmail(testEmail)).thenReturn(givenNullUser);

			Mockito.when(userRepository.findByNickname(testNickname)).thenReturn(givenNullUser);

			// when
			// then
			assertThatCode(() -> {
				userService.createUser(new RegisterRequestDto(testEmail, testNickname, testPassword));
			}).doesNotThrowAnyException();

		}

	}

	@Nested
	class AfterLoginTest {

		@BeforeEach
		void setUp() {
			UserDetails user = createUserDetails();

			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
		}


		@AfterEach
		void afterSetting() {
			SecurityContextHolder.clearContext();
		}


		private UserDetails createUserDetails() {
			return new UserDetails() {
				@Override
				public Collection<? extends GrantedAuthority> getAuthorities() {
					return null;
				}


				@Override
				public String getPassword() {
					return testPassword;
				}


				@Override
				public String getUsername() {
					return user.getId().toString();
				}


				@Override
				public boolean isAccountNonExpired() {
					return true;
				}


				@Override
				public boolean isAccountNonLocked() {
					return true;
				}


				@Override
				public boolean isCredentialsNonExpired() {
					return true;
				}


				@Override
				public boolean isEnabled() {
					return true;
				}
			};
		}


		@Nested
		@DisplayName("회원탈퇴")
		public class 회원탈퇴 {

			@Test
			@DisplayName("회원탈퇴 - 실패")
			public void deleteUserTest_회원탈퇴실패UserNotFound() {
				// given
				Mockito.when(authService.getUserId()).thenReturn(1L);
				Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

				// when
				CustomException customException = Assertions.assertThrows(CustomException.class, () -> {
					userService.deleteUser();
				});

				// then
				Assertions.assertEquals(customException.getErrorCode(), ErrorCode.USER_NOT_FOUND);
			}


			@Test
			@DisplayName("회원탈퇴 - 성공")
			void deleteUserTest_회원탈퇴성공() {
				// given
				// mock AuthService
				Long userId = 1L;
				Mockito.when(authService.getUserId()).thenReturn(userId);

				// mock UserRepository
				Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
				Mockito.when(userRepository.save(user)).thenReturn(user);
				// Mock the diarySetList
				DiarySetEntity diarySet = DiarySetEntity.builder().id(1L).build();
				List<DiarySetEntity> diarySetList = Collections.singletonList(diarySet);
				Mockito.when(diarySetService.getDiarySetBookmarkList(user.getNickname(), Pageable.unpaged())).thenReturn(new PageImpl<>(diarySetList));

				// Mock the scheduleList
				ScheduleEntity schedule = ScheduleEntity.builder().id(1L).build();
				List<ScheduleEntity> scheduleList = Collections.singletonList(schedule);
				Mockito.when(scheduleService.getAllScheduleByUser()).thenReturn(scheduleList);

				// Mock the userPlantList
				UserPlantEntity userPlant = UserPlantEntity.builder().id(1L).user(user).build();
				List<UserPlantEntity> userPlantList = Collections.singletonList(userPlant);
				Mockito.when(userRepository.findByNicknameAndIsDeleted(user.getNickname(), IsDeletedType.N)).thenReturn(Optional.of(user));
				Mockito.when(userPlantRepository.findByUserAndIsDeleted(user, IsDeletedType.N)).thenReturn(userPlantList);
				Mockito.when(userPlantRepository.findByIdAndIsDeleted(userPlant.getId(), IsDeletedType.N)).thenReturn(Optional.of(userPlant));
				Mockito.when(diarySetRepository.findByUserAndUserPlant(userPlant.getUser(), userPlant)).thenReturn(Optional.empty());

				// Mock the likeList
				FeedEntity feed = FeedEntity.builder().id(1L).build();
				LikeEntity like = LikeEntity.builder().id(1L).feed(feed).build();
				List<LikeEntity> likeList = Collections.singletonList(like);
				Mockito.when(feedService.getAllLikeByUser()).thenReturn(likeList);

				// Mock the alertList
				AlertEntity alert = AlertEntity.builder().id(1L).build();
				List<AlertEntity> alertList = Collections.singletonList(alert);
				Mockito.when(alertService.getAlertList(Pageable.unpaged())).thenReturn(alertList);

				// Mock the followerList
				UserEntity follower = UserEntity.builder()
					.id(2L).nickname("test_nickname_2").email("test2@test.com").password("test_password_2").build();
				List<UserEntity> followerList = Collections.singletonList(follower);
				Mockito.when(followService.getFollowerList(user.getNickname(), Pageable.unpaged())).thenReturn(new PageImpl<>(followerList));

				// Mock the followingList
				UserEntity following = UserEntity.builder().id(3L).nickname("test_nickname_3").email("test3@test.com").password("test_password_3").build();
				List<UserEntity> followingList = Collections.singletonList(following);
				Mockito.when(followService.getFollowingList(user.getNickname(), Pageable.unpaged())).thenReturn(new PageImpl<>(followingList));

				// when
				userService.deleteUser();

				// then
				Assertions.assertEquals(user.getIsDeleted(), IsDeletedType.Y);
				Mockito.verify(userRepository, times(1)).save(user);

			}

		}

		@Nested
		@DisplayName("비밀번호수정")
		public class 비밀번호수정 {

			@Test
			@DisplayName("비밀번호수정 - 실패 (변경 전 후 비번 동일)")
			void modifyPasswordTest_비밀번호수정실패_비번동일() {
				// given
				Long userId = user.getId();
				Mockito.when(authService.getUserId()).thenReturn(userId);
				Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

				PasswordRequestDto passwordRequestDto = new PasswordRequestDto(testPassword);
				Mockito.when(passwordEncoder.matches(passwordRequestDto.getPassword(), user.getPassword())).thenReturn(true);

				// when
				CustomException customException = Assertions.assertThrows(CustomException.class, () -> {
					userService.modifyPassword(passwordRequestDto);
				});

				// then
				Assertions.assertEquals(customException.getErrorCode(), ErrorCode.DUPLICATE_RESOURCE);
			}


			@Test
			@DisplayName("비밀번호수정 - 실패 (사용자 없음)")
			void modifyPasswordTest_비밀번호수정실패_사용자없음() {
				// given
				Long userId = 1L;
				Mockito.when(authService.getUserId()).thenReturn(userId);
				Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

				PasswordRequestDto passwordRequestDto = new PasswordRequestDto("12345");

				// when
				CustomException customException = Assertions.assertThrows(CustomException.class, () -> {
					userService.modifyPassword(passwordRequestDto);
				});

				// then
				Assertions.assertEquals(customException.getErrorCode(), ErrorCode.USER_NOT_FOUND);
			}


			@Test
			@DisplayName("비밀번호수정 - 성공")
			void modifyPasswordTest_비밀번호수정성공() {
				// given
				Long userId = user.getId();
				Mockito.when(authService.getUserId()).thenReturn(userId);
				Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

				PasswordRequestDto passwordRequestDto = new PasswordRequestDto("12345");
				Mockito.when(passwordEncoder.matches(passwordRequestDto.getPassword(), user.getPassword())).thenReturn(false);

				Optional<UserEntity> givenNullUser = Optional.of(user);
				Mockito.when(userRepository.findById(1L)).thenReturn(givenNullUser);

				// when
				userService.modifyPassword(passwordRequestDto);

				// then
				Assertions.assertNotEquals(user.getPassword(), testEncodePassword);

				Mockito.verify(userRepository, times(1)).findById(1L);
				Assertions.assertNotEquals(user.getPassword(), testEncodePassword);
				Mockito.verify(userRepository, times(1)).save(user);
			}

		}

		@Nested
		@DisplayName("비밀번호확인")
		public class 비밀번호확인 {

			@Test
			@DisplayName("비밀번호확인 - 실패:사용자없음")
			void checkPasswordTest_비밀번호확인실패_사용자없음() {
				// given
				Long userId = 1L;
				Mockito.when(authService.getUserId()).thenReturn(userId);
				Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

				PasswordRequestDto passwordRequestDto = new PasswordRequestDto(testPassword);

				// when
				CustomException customException = Assertions.assertThrows(CustomException.class, () -> {
					userService.checkPassword(passwordRequestDto);
				});

				// then
				Assertions.assertEquals(customException.getErrorCode(), ErrorCode.USER_NOT_FOUND);
			}


			@Test
			@DisplayName("비밀번호확인 - 불일치")
			void checkPasswordTest_비밀번호확인불일치() {
				// given
				Long userId = 1L;
				Mockito.when(authService.getUserId()).thenReturn(userId);
				Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

				PasswordRequestDto passwordRequestDto = new PasswordRequestDto(testPassword);
				Mockito.when(passwordEncoder.matches(passwordRequestDto.getPassword(), user.getPassword())).thenReturn(false);

				// when
				Boolean checkPassword = userService.checkPassword(passwordRequestDto);

				// then
				Assertions.assertEquals(checkPassword, Boolean.FALSE);
			}


			@Test
			@DisplayName("비밀번호확인 - 성공")
			void checkPasswordTest_비밀번호확인성공() {
				// given
				Long userId = 1L;
				Mockito.when(authService.getUserId()).thenReturn(userId);
				Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

				PasswordRequestDto passwordRequestDto = new PasswordRequestDto(testPassword);
				Mockito.when(passwordEncoder.matches(passwordRequestDto.getPassword(), user.getPassword())).thenReturn(true);

				// when
				Boolean checkPassword = userService.checkPassword(passwordRequestDto);

				// then
				Assertions.assertEquals(checkPassword, Boolean.TRUE);
			}

		}

	}

}
