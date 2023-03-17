import { DiarySetType } from './diarySetType';
import { createAsyncThunk } from '@reduxjs/toolkit';
import http from '@/lib/http';

import Toastify from 'toastify-js';
import message from '@/assets/message.json';
import toastifyCSS from '@/assets/toastify.json';
import { PageType } from '../common/commonType';

// 관찰일지 생성
export const createDiarySet = async (payload: DiarySetType) => {
  try {
    const { data } = await http.post(`/diaryset`, payload);
    return data;
  } catch (error) {}
};

// 관찰일지 리스트 조회
export const getDiarySetList = async (nickname: string | string[], params: PageType) => {
  try {
    const { data } = await http.get(`/diaryset/${nickname}`, { params });
    return data;
  } catch (error) {}
};

// 관찰일지 수정
export const updateDiarySet = async (diarysetId: string) => {
  try {
    const { data } = await http.put(`/diaryset/${diarysetId}`);
    return data;
  } catch (error) {}
};

// 관찰일지 삭제
export const deleteDiarySet = async (diarysetId: string) => {
  try {
    const { data } = await http.delete(`/diaryset/${diarysetId}`);
    return data;
  } catch (error) {}
};

// 유저가 북마크한 관찰일지 목록 조회
export const getBookmarkedDiarySet = async (nickname: string, params: PageType) => {
  try {
    const { data } = await http.get(`/diaryset/${nickname}/bookmark`, { params });
    return data;
  } catch (error) {}
};

// 인기 관찰일지 목록 조회
export const getTopDiarySet = async () => {
  try {
    const { data } = await http.get(`/diaryset/population`);
    return data;
  } catch (error) {}
};

// 관찰일지 북마크
export const createBookmark = async (diarysetId: number) => {
  try {
    const { data } = await http.post(`/diaryset/${diarysetId}/bookmark`);
    return data;
  } catch (error) {}
};

// 관찰일지 북마크 취소
export const deleteBookmark = async (diarysetId: number) => {
  try {
    const { data } = await http.delete(`/diaryset/${diarysetId}/bookmark`);
    return data;
  } catch (error) {}
};
