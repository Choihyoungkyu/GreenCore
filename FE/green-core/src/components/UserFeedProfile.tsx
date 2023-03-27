import React, { useCallback, useEffect, useState } from 'react';
import { useAppSelector } from '@/core/hooks';
import { getStorage, ref, uploadBytesResumable, getDownloadURL } from 'firebase/storage';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import Image from 'next/image';
import Link from 'next/link';

import Toastify from 'toastify-js';
import message from '@/assets/message.json';
import toastifyCSS from '@/assets/toastify.json';
import Skeleton from 'react-loading-skeleton';

import { deleteFollow, updateFollow } from '@/core/follow/followAPI';
import { getProfile } from '@/core/user/userAPI';
import AppLoading from './common/AppLoading';

import styles from './UserFeedProfile.module.scss';
import AppButton from './button/AppButton';
import UserProfileUpdateModal from '@/components/modal/UserProfileUpdateModal';

type ProfileType = {
  followerCount: number;
  followingCount: number;
  introduction: string;
  isFollowed: boolean;
  nickname: string;
  profileImagePath: string;
};

type StateType = {
  uploadProfileImage: File;
};

const initialState: StateType = {
  uploadProfileImage: null,
};

export default function UserFeedProfile() {
  const router = useRouter();
  const storage = getStorage();

  const { nickname } = router.query;
  const myNickname = useAppSelector((state) => state.common.userInfo?.nickname);

  const { register, getValues, watch } = useForm<StateType>({ defaultValues: initialState });

  const [isSameUser, setIsSameUser] = useState<boolean>(false);
  const [isOpenUserProfileUpdateModal, setIsOpenUserProfileUpdateModal] = useState<boolean>(false);
  const [userProfile, setUserProfile] = useState<ProfileType>(null);
  const [uploadProfileImage] = getValues(['uploadProfileImage']);
  const [userProfileImagePath, setUserProfileImagePath] = useState<string>(null);

  useEffect(() => {
    watch();
    return () => {};
  }, []);

  useEffect(() => {
    if (!router.isReady) return;
    if (!router.query.nickname) return;

    console.log('path nickname:', nickname, ' my nickname: ', myNickname);
    checkSameUser();
    fetchUserProfile();
  }, [nickname, uploadProfileImage]);

  useEffect(() => {
    handleProfileImageUpdate();
  }, [uploadProfileImage]);

  /** url path의 유저와 현재 로그인 유저가 같은지 확인하는 함수 */
  const checkSameUser = useCallback(() => {
    if (myNickname == nickname) setIsSameUser(true);
  }, [myNickname, nickname]);

  /** storage 에서 유저 프로필 이미지 가져오는 함수 */
  const fetchUserProfile = useCallback(async () => {
    const { data } = await getProfile(nickname);
    setUserProfile(data);

    if (nickname) {
      const profileRef = ref(storage, `${nickname}/profileImage`);

      getDownloadURL(profileRef)
        .then((downloadURL) => {
          setUserProfileImagePath(downloadURL);
        })
        .catch((error) => {
          switch (error.code) {
            case 'storage/object-not-found':
              setUserProfileImagePath(null);
              break;
            case 'storage/unauthorized':
              break;
            case 'storage/canceled':
              break;
            case 'storage/unknown':
              break;
          }
        });
    }
  }, [nickname]);

  /** 이미지 수정 누르면 파일 선택 창 뜨는 함수 */
  function handleImageExploerOpen() {
    const profileImageInput: HTMLElement = document.querySelector(`.profileImageInput`);
    profileImageInput.click();
  }

  /** 프로필 이미지 수정하는 함수 */
  function handleProfileImageUpdate() {
    if (!uploadProfileImage) return;

    const profileRef = ref(storage, `${myNickname}/profileImage`);
    const uploadTask = uploadBytesResumable(profileRef, uploadProfileImage[0]);

    uploadTask.on(
      'state_changed',
      (snapshot) => {
        const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;

        switch (snapshot.state) {
          case 'paused':
            console.log('Upload is paused');
            break;
          case 'running':
            console.log('Upload is running');
            break;
        }
      },
      (error) => {
        Toastify({
          text: message.UpdateProfileImageFail,
          duration: 1500,
          position: 'center',
          stopOnFocus: true,
          style: toastifyCSS.fail,
        }).showToast();
        console.error(error);
      },
      () => {
        Toastify({
          text: message.UpdateProfileImageSuccess,
          duration: 1500,
          position: 'center',
          stopOnFocus: true,
          style: toastifyCSS.success,
        }).showToast();

        getDownloadURL(uploadTask.snapshot.ref).then((downloadURL) => {
          setUserProfileImagePath(downloadURL);
        });
      }
    );
  }

  /** 팔로우하기 함수 */
  async function handleFollowUpdate() {
    const { data } = await updateFollow(nickname);
    console.log(data);
  }

  /** 언팔로우 함수 */
  async function handleFollowDelete() {
    const { data } = await deleteFollow(nickname);
    console.log(data);
  }

  return (
    <div className={`${styles.container} pb-10`}>
      <UserProfileUpdateModal
        isOpen={isOpenUserProfileUpdateModal}
        userProfile={userProfile}
        handleModalClose={() => setIsOpenUserProfileUpdateModal(false)}
      />

      <div className={`mx-5`}>
        <div className='flex justify-center space-x-36'>
          <div className='flex items-center'>
            {/* 프로필 이미지 라인 */}
            <input type='file' accept='image/*' hidden className='profileImageInput' {...register('uploadProfileImage')} />
            {userProfile ? (
              <div onClick={handleImageExploerOpen}>
                {userProfileImagePath ? (
                  (
                    <Image
                      src={userProfileImagePath}
                      alt='사용자 프로필 이미지'
                      width={90}
                      height={90}
                      className='rounded-full bg-cover'
                      onClick={handleProfileImageUpdate}
                      priority
                    />
                  ) || <Skeleton width={90} height={90} circle />
                ) : (
                  <Image src='/images/noProfile.png' alt='사용자 프로필 이미지' width={90} height={90} className='rounded-full bg-cover' priority />
                )}
              </div>
            ) : (
              <Skeleton width={90} height={90} circle />
            )}

            {/* 닉네임 & 한 줄 자기소개 */}
            <div className='pl-3'>
              {userProfile ? (
                <>
                  <div className='flex items-center space-x-1'>
                    <div className={styles.nickname}>{userProfile?.nickname}</div>
                    {isSameUser ? (
                      <span className='material-symbols-outlined main text-lg cursor-pointer' onClick={() => setIsOpenUserProfileUpdateModal(true)}>
                        edit
                      </span>
                    ) : null}
                  </div>
                  <div className={styles.introduction}>{userProfile?.introduction}</div>
                </>
              ) : (
                <>
                  <div className='flex items-center space-x-1'>
                    <Skeleton width={100} />
                  </div>
                  <Skeleton width={150} />
                </>
              )}
            </div>
          </div>

          <div className='flex flex-col items-center justify-between'>
            <div className='flex justify-evenly w-full'>
              <div className='flex flex-col items-center justify-center'>
                {userProfile ? (
                  <>
                    <div className={styles.introduction}>팔로워</div>
                    <div className={styles.introduction}>{userProfile?.followerCount}</div>
                  </>
                ) : (
                  <>
                    <Skeleton width={150} /> <Skeleton width={150} />
                  </>
                )}
              </div>

              <div className='flex flex-col items-center justify-center'>
                {userProfile ? (
                  <>
                    <div className={styles.introduction}>팔로잉</div>
                    <div className={styles.introduction}>{userProfile?.followingCount}</div>
                  </>
                ) : null}
              </div>
            </div>

            <div className='flex items-center'>
              {userProfile ? (
                <>
                  {userProfile?.isFollowed ? (
                    <AppButton text='팔로우 중' className='w-2 h-1' handleClick={handleFollowDelete} />
                  ) : (
                    <AppButton text='팔로우 하기' size='small' className='w-3 h-0.5' handleClick={handleFollowUpdate} />
                  )}
                </>
              ) : null}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}