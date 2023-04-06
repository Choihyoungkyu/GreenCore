import React, { useEffect, useRef, useState } from 'react';
import AppLayout from '@/layout/AppLayout';
import Skeleton from 'react-loading-skeleton';
import { useRouter } from 'next/router';
import FeedCommentList from '@/components/FeedCommentList';
import styles from '@/styles/post/post.module.scss';
import { useAppDispatch, useAppSelector } from '@/core/hooks';
import { deletePost, getPost } from '@/core/post/postAPI';
import { SET_IS_SEARCH_STATE } from '@/core/common/commonSlice';
import { SET_SEARCH_TAG } from '@/core/search/searchSlice';
import { createLike, deleteLike } from '@/core/feed/feedAPI';
import { deleteFollow, updateFollow } from '@/core/follow/followAPI';
import CommentDeleteModal from '@/components/modal/CommentDeleteModal';
import Image from 'next/image';
import { getStorage, ref, getDownloadURL } from 'firebase/storage';
import { getTodayDate } from '@/lib/utils';
import { createAlert } from '@/core/alert/alertAPI';
import Link from 'next/link';

export default function PostDetail() {
  const router = useRouter();
  const storage = getStorage();
  const dispatch = useAppDispatch();
  const popUpRef = useRef<HTMLDivElement>(null);

  const [isOpenPostDeleteModal, setIsOpenPostDeleteModal] = useState(false);
  const myNickname = useAppSelector((state) => state.common?.userInfo?.nickname);
  const divRef = useRef<HTMLDivElement>(null);
  const { postId } = router.query;
  const [post, setPost] = useState<any>({});
  const [hasPost, setHasPost] = useState<boolean>(false);

  const [isLiked, setIsLiked] = useState<boolean>(false);
  const [likeCount, setLikeCount] = useState<number>(0);
  const [isfollowed, setIsFollowed] = useState<boolean>(false);
  const [followerCount, setFollowerCount] = useState<number>(0);
  const [isOpenMenu, setIsOpenMenu] = useState(false);
  const [commentCount, setCommentCount] = useState<number>(0);

  const [userProfileImagePath, setUserProfileImagePath] = useState<string>('');

  const [isEditPopUp, setIsEditPopUp] = useState<boolean>(false);

  useEffect(() => {
    document.addEventListener('mousedown', handleModalOutsideClick);
    return () => {
      document.removeEventListener('mousedown', handleModalOutsideClick);
    };
  }, []);

  /** 팝업 바깥 클릭 시 */
  function handleModalOutsideClick(e) {
    if (popUpRef.current && !popUpRef.current.contains(e.target)) setIsEditPopUp(false);
  }

  /** 사용자 프로필 이미지 가져오는 함수 */
  function getUserProfile(nickname: string) {
    const profileRef = ref(storage, `${nickname}/profileImage`);

    getDownloadURL(profileRef)
      .then((downloadURL) => {
        setUserProfileImagePath(downloadURL);
      })
      .catch((error) => {
        console.error(error);
      });
  }

  // searchState 변경
  function changeSearchState() {
    dispatch(SET_IS_SEARCH_STATE('default'));
  }

  // 바깥 클릭시 닫기
  useEffect(() => {
    const handleClickOutside = (event: any) => {
      if (event.target.innerText != 'more_vert') {
        setIsOpenMenu(false);
      }
    };
    document.addEventListener('click', handleClickOutside);

    changeSearchState();
    return () => {};
  }, []);

  useEffect(() => {
    if (postId) {
      getPost(Number(postId)).then((res) => {
        if (res.result === 'SUCCESS') {
          setPost(res.data);
          setCommentCount(res.data.commentCount);
          getUserProfile(res.data.user.nickname);
          setIsLiked(res.data.isLiked);
          setLikeCount(res.data.likeCount);
          setIsFollowed(res.data.user.isFollowed);
          setFollowerCount(res.data.user.followingCount);
          setHasPost(true);
        }
      });
    }
  }, [postId]);

  // 포스트 삭제
  const handleDeletePost = () => {
    try {
      const requestData = { router, postId: Number(postId) };
      dispatch(deletePost(requestData));
    } catch (err) {
      console.log(err);
    }
  };

  // 뒤로가기
  const handleGoBack = () => {
    router.back();
  };

  // 좋아요
  function handlePostLike(e: any) {
    e.stopPropagation();
    createLike(Number(postId)).then((res) => {
      if (res.result === 'SUCCESS') {
        setIsLiked(true);
        setLikeCount((prev) => prev + 1);
        if (post.user.nickname !== myNickname) {
          const payload = {
            nickname: post.user.nickname,
            mentionNickname: myNickname,
            type: 'ALERT_LIKE',
            urlPath: `/post/${Number(postId)}`,
            createdAt: getTodayDate(),
            isRead: false,
          };
          dispatch(createAlert(payload));
        }
      }
    });
  }

  // 좋아요 취소
  function handleDeleteLike(e: any) {
    e.stopPropagation();
    deleteLike(Number(postId)).then((res) => {
      if (res.result === 'SUCCESS') {
        setIsLiked(false);
        setLikeCount((prev) => prev - 1);
      }
    });
  }

  // 유저 팔로우
  function handleUserFollow(e: any) {
    e.stopPropagation();
    updateFollow(post.user.nickname).then((res) => {
      if (res.result === 'SUCCESS') {
        setIsFollowed(true);
        setFollowerCount((prev) => prev + 1);
        if (post.user.nickname !== myNickname) {
          const payload = {
            nickname: post.user.nickname,
            mentionNickname: myNickname,
            type: 'ALERT_FOLLOW',
            urlPath: `/user/feed/${myNickname}`,
            createdAt: getTodayDate(),
            isRead: false,
          };
          dispatch(createAlert(payload));
        }
      }
    });
  }

  // 유저 팔로우 취소
  function handleDeleteFollow(e: any) {
    e.stopPropagation();
    deleteFollow(post.user.nickname).then((res) => {
      if (res.result === 'SUCCESS') {
        setIsFollowed(false);
        setFollowerCount((prev) => prev - 1);
      }
    });
  }

  // 시간 포맷
  const elapsedTime = (date: any) => {
    const created = new Date(date);

    const SECONDS_IN_MINUTE = 60;
    const SECONDS_IN_HOUR = 3600;
    const SECONDS_IN_DAY = 86400;

    const today = new Date();
    const diffSeconds = Math.floor((today.getTime() - created.getTime()) / 1000);

    if (diffSeconds < SECONDS_IN_MINUTE) {
      return `${diffSeconds}초 전`;
    } else if (diffSeconds < SECONDS_IN_HOUR) {
      const diffMinutes = Math.floor(diffSeconds / SECONDS_IN_MINUTE);
      return `${diffMinutes}분 전`;
    } else if (diffSeconds < SECONDS_IN_DAY) {
      const diffHours = Math.floor(diffSeconds / SECONDS_IN_HOUR);
      return `${diffHours}시간 전`;
    } else if (diffSeconds < SECONDS_IN_DAY * 7) {
      const diffDays = Math.floor(diffSeconds / SECONDS_IN_DAY);
      return `${diffDays}일 전`;
    } else {
      const year = created.getFullYear();
      const month = created.getMonth() + 1;
      const day = created.getDate();
      return `${year}년 ${month}월 ${day}일`;
    }
  };

  // 유저 프로필 이동
  const goProfile = () => {
    router.push(`/user/feed/${post.user.nickname}`);
  };

  // 태그 클릭 이벤트
  function handleTagClick(e) {
    const searchValue = e.target.innerText.slice(1);
    dispatch(SET_SEARCH_TAG(searchValue));
  }

  return (
    <AppLayout>
      {isOpenPostDeleteModal && (
        <CommentDeleteModal
          isOpen={isOpenPostDeleteModal}
          modalTitle='게시글 삭제'
          handleDelete={handleDeletePost}
          handleModalClose={() => setIsOpenPostDeleteModal(false)}
        />
      )}
      {!hasPost ? (
        <ul>
          <Skeleton width={150} height={150} />
          <Skeleton />
          <Skeleton />
        </ul>
      ) : (
        <div className={`${styles.container} overflow-auto flex-1 mx-auto px-4 py-4`}>
          <div className='flex items-center'>
            <span className={`material-symbols-outlined cursor-pointer mr-2`} onClick={handleGoBack} style={{ fontSize: '2rem', fontWeight: '600' }}>
              arrow_back
            </span>
            <span className={`${styles.title} py-1`}>포스트</span>
          </div>

          <div className='flex 2xl:py-10 py-4'>
            {/* 게시글 작성자 정보 */}
            <div className='flex flex-col items-center'>
              <div className={`${styles.helpTip} flex`}>
                {userProfileImagePath ? (
                  <Image
                    priority
                    src={userProfileImagePath}
                    width={80}
                    height={80}
                    className={`${styles.profileImg} border border-2 border-black`}
                    alt='프로필 이미지'
                    onClick={goProfile}
                    style={{ width: '80px', height: '80px' }}
                  />
                ) : (
                  <Skeleton width={80} height={80} className={`${styles.profileImg}`} />
                )}

                {/* 프로필 팝업 */}
                <div className={`flex flex-col div ${styles.userInfo}`}>
                  <div className={`flex`}>
                    <div className={`flex flex-col justify-center items-center `}>
                      {userProfileImagePath ? (
                        <Image
                          priority
                          src={userProfileImagePath}
                          width={80}
                          height={80}
                          alt='프로필 이미지'
                          className={`${styles.profileImg2} mb-3`}
                          onClick={goProfile}
                        />
                      ) : (
                        <Skeleton width={80} height={80} className={`${styles.profileImg2} mb-3`} />
                      )}
                    </div>

                    <div className='flex flex-col justify-center items-center'>
                      <div className='text-xl font-bold'>
                        <span>{post.user.nickname || <Skeleton />}</span>
                      </div>
                      <div className='flex justify-center items-center text-sm'>
                        <div className='flex flex-col justify-center items-center w-20'>
                          <span>팔로워</span>
                          <span>{followerCount}</span>
                        </div>
                        <div className='flex flex-col justify-center items-center w-20'>
                          <span>팔로잉</span>
                          <span>{post.user.followingCount}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div className='flex py-5'>{post.user.introduction}</div>
                  {post.user.nickname !== myNickname && (
                    <div className='flex justify-center rounded-lg overflow-hidden'>
                      {isfollowed ? (
                        <button className={`w-full `} onClick={handleDeleteFollow} style={{ backgroundColor: 'var(--thin-color)' }}>
                          팔로우 취소
                        </button>
                      ) : (
                        <button className={`text-white w-full`} onClick={handleUserFollow} style={{ backgroundColor: 'var(--main-color)' }}>
                          팔로우
                        </button>
                      )}
                    </div>
                  )}
                </div>
              </div>

              <div className={`flex flex-col space-y-2`}>
                <div className={`${styles.nickname} mt-2`}>{post.user.nickname}</div>

                <div className='flex items-center'>
                  {isLiked ? (
                    <span
                      className={`material-symbols-outlined ${styles.heart} cursor-pointer flex-1 text-right mt-0.5`}
                      onClick={handleDeleteLike}
                      style={{ fontSize: '1.2rem' }}>
                      favorite
                    </span>
                  ) : (
                    <span
                      className={`material-symbols-outlined cursor-pointer flex-1 text-right mt-0.5`}
                      onClick={handlePostLike}
                      style={{ fontSize: '1.2rem' }}>
                      favorite
                    </span>
                  )}
                  <div className='font-extrabold flex-1 flex justify-start ml-3 text-sm'>{likeCount}</div>
                </div>

                <div className={`flex items-center`}>
                  <span className={`material-symbols-outlined flex-1 flex text-right mt-0.5`} style={{ fontSize: '1.2rem' }}>
                    chat
                  </span>
                  <div className='font-extrabold flex-1 flex justify-start ml-3 text-sm'>{commentCount}</div>
                </div>
              </div>
            </div>

            {/* 게시글 정보 */}
            <div className={`${styles.subContainer} flex flex-1`} style={myNickname !== post.user.nickname ? { paddingRight: '24px' } : null}>
              <div className='flex-1 px-3'>
                <div className='flex flex-col justify-between mb-2'>
                  <div className={`flex flex-wrap flex-1 mr-5`}>
                    {post?.tags.map((tag: string, index: number) => {
                      return (
                        // TODO : 태그 검색
                        <div className='pr-2' key={index}>
                          <span onClick={handleTagClick} className={`${styles.tagBtn}`}>
                            #{tag}
                          </span>
                        </div>
                      );
                    })}
                  </div>
                </div>

                <div className={`${styles.box}`}>
                  <Image priority src={post?.imagePath} width={100} height={100} alt='img' className={`${styles.imageWarrper} w-full h-full`} />
                </div>
                <div className='relative'>
                  <div className='w-fit absolute mt-3 right-0 text-xs text-gray-500'>{elapsedTime(post?.createdAt)}</div>
                </div>
                <div className='my-10'>{post?.content}</div>

                {/* 댓글 컴포넌트 */}
                <div>
                  {!Number.isNaN(postId) && (
                    <FeedCommentList feedId={Number(postId)} setCommentCount={setCommentCount} feedType='post' nickname={post.user.nickname} />
                  )}
                </div>
              </div>

              {/* 옵션 버튼 */}
              {/* {myNickname === post.user.nickname && (
                <>
                  <span className='material-symbols-outlined cursor-pointer h-fit' onClick={() => setIsOpenMenu((prev) => !prev)}>
                    more_vert
                  </span>

                  <div className={`${styles.popUp}`}>
                    <div ref={divRef} className={`${isOpenMenu ? styles.editPopUp : 'hidden'} rounded-xl overflow-hidden`}>
                      <div
                        className={`border-b border-slate-300 bg-white flex justify-center items-center cursor-pointer ${styles.dropdownMenu}`}
                        onClick={() => {
                          router.push(`update/${postId}`);
                        }}>
                        <span className='text-lg p-2'>수정</span>
                        <span className='material-symbols-outlined'>edit</span>
                      </div>
                      <div
                        className={`border-b border-slate-300 bg-white flex justify-center items-center text-red-400 cursor-pointer ${styles.dropdownMenu}`}
                        onClick={() => setIsOpenPostDeleteModal(true)}>
                        <span className='text-lg p-2'>삭제</span>
                        <span className='material-symbols-outlined'>delete</span>
                      </div>
                    </div>
                  </div>
                </>
              )} */}

              {myNickname === post.user.nickname ? (
                <>
                  <span className='material-symbols-outlined cursor-pointer h-fit' onClick={() => setIsEditPopUp(true)}>
                    more_vert
                  </span>

                  {isEditPopUp ? (
                    <div className='relative' ref={popUpRef}>
                      <div className={`popUp ${styles.popUp}`}>
                        <Link href={`/post/update/${postId}`}>
                          <div>수정</div>
                        </Link>
                        <div
                          onClick={() => {
                            setIsOpenPostDeleteModal(true);
                            setIsEditPopUp(false);
                          }}>
                          삭제
                        </div>
                      </div>
                    </div>
                  ) : null}
                </>
              ) : null}
            </div>
          </div>
        </div>
      )}
    </AppLayout>
  );
}
