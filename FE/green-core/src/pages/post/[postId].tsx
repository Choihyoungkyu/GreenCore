import React from 'react';
import AppLayout from '@/layout/AppLayout';
import Skeleton from 'react-loading-skeleton';
import http from '@/lib/http';
import useSWR from 'swr';
import { useRouter } from 'next/router';
import Link from 'next/link';
import FeedCommentList from '@/components/FeedCommentList';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEllipsisVertical, faHeart } from '@fortawesome/free-solid-svg-icons';
import { faMessage } from '@fortawesome/free-solid-svg-icons';
import UserInfo from '@/components/common/UserInfo';
import styles from '@/components/common/UserInfo.module.scss';

const fetcher = (url: string) => http.get(url).then((res) => res.data);

export default function PostDetail() {
  const router = useRouter();
  const postId = router.query.postId;
  const { data: post, error, isLoading: hasPost } = useSWR(`/post/${postId}`, fetcher);
  return (
    <AppLayout>
      <div style={{ display: 'flex' }}>
        {!hasPost ? (
          <div style={{ margin: '16px' }}>
            <div className={`${styles.helpTip} flex `}>
              <img src={post.data.user.profileImagePath} style={{ width: '70px', height: '70px', borderRadius: '50%' }} alt='프로필 사진' />
              <div id='userInfo'>
                {post.data.user.profileImagePath ? (
                  <Link href={`/user/feed/${post.data.user.nickname}`}>
                    <img
                      className='mb-3'
                      src={post.data.user.profileImagePath}
                      alt='프로필 사진'
                      style={{ width: '70px', height: '70px', borderRadius: '50%' }}
                    />
                  </Link>
                ) : (
                  <Skeleton width={30} height={30} />
                )}
                <span>
                  <span>{post.data.user.followerCount + ' 팔로워 / ' || <Skeleton />}</span>
                  <span>{post.data.user.followingCount + ' 팔로잉' || <Skeleton />}</span>
                </span>
                <br />
                <span>팔로잉 여부 : {post.data.user.isFollowed ? <i className='fa-solid fa-heart'></i> : 'false'}</span>
                <br />
                <span>{post.data.user.nickname || <Skeleton />}</span>
                <br />
                <span>{post.data.user.introduction || <Skeleton />}</span>
                <br />
              </div>
            </div>
            <div>
              <FontAwesomeIcon icon={faHeart} />
              {post.data.likeCount}
            </div>
            <div>
              <FontAwesomeIcon icon={faMessage} />
              {post.data.commentCount}
            </div>
          </div>
        ) : (
          <div style={{ margin: '16px' }}>
            <Skeleton style={{ width: '70px', height: '70px', borderRadius: '50%' }} />
            <Skeleton />
            <Skeleton />
          </div>
        )}
        {!hasPost ? (
          <div style={{ margin: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <div>{post?.data.user.nickname}</div>
              <div>{post.data.createdAt.slice(0, 10).replace(/-/g, '.')}</div>
            </div>
            <div>
              <img src={post.data.imagePath} alt='포스트 이미지' style={{ width: '400px', height: '400px' }} />
            </div>
          </div>
        ) : (
          <div style={{ margin: '16px' }}>
            <div>
              <Skeleton />
            </div>
            <div>
              <Skeleton style={{ width: '400px', height: '400px' }} />
            </div>
          </div>
        )}
        {!hasPost ? (
          <div>
            <FontAwesomeIcon icon={faEllipsisVertical} style={{ margin: '8px' }} />
          </div>
        ) : (
          <Skeleton />
        )}
      </div>
      <div>
        <Link href={`/post/update/${postId}`}>
          <button>수정</button>
        </Link>
        <button>삭제</button>
      </div>
      <FeedCommentList />
    </AppLayout>
  );
}
