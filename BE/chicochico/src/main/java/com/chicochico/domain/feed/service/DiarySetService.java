package com.chicochico.domain.feed.service;


import com.chicochico.domain.feed.dto.DiarySetRequestDto;
import com.chicochico.domain.feed.entity.DiarySetEntity;
import com.chicochico.domain.feed.repository.DiarySetRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class DiarySetService {

	private DiarySetRepository diarySetRepository;


	/**
	 * 관찰 일지 목록을 조회합니다
	 *
	 * @param nickname
	 * @param pageable
	 * @return
	 */
	public List<DiarySetEntity> getDiarySetList(String nickname, Pageable pageable) {
		return new ArrayList<>();
	}


	/**
	 * 관찰 일지를 생성합니다
	 *
	 * @param diarySetRequestDto
	 */
	public void createDiarySet(DiarySetRequestDto diarySetRequestDto) {
	}


	/**
	 * 관찰 일지를 수정합니다
	 *
	 * @param diarySetId
	 * @param diarySetRequestDto
	 */
	public void modifyDiarySet(Long diarySetId, DiarySetRequestDto diarySetRequestDto) {
	}


	/**
	 * 관찰 일지를 삭제합니다
	 *
	 * @param diarySetId
	 */
	public void deleteDiarySet(Long diarySetId) {
	}


	/**
	 * 유저가 북마크한 관찰 일지 목록을 조회합니다
	 *
	 * @param diarySetId
	 * @param pageable
	 * @return
	 */
	public List<DiarySetEntity> getDiarySetBookmarkList(Long diarySetId, Pageable pageable) {
		return new ArrayList<>();
	}


	/**
	 * 인기 관찰 일지를 5개 조회합니다
	 *
	 * @return
	 */
	public List<DiarySetEntity> getPopularDiarySetList() {
		return new ArrayList<>();
	}


	/**
	 * 관찰 일지를 북마크합니다
	 *
	 * @param diarySetId
	 */
	public void createBookmark(Long diarySetId) {
	}


	/**
	 * 관찰 일지 북마크를 취소(삭제)합니다
	 *
	 * @param diarySetId
	 */
	public void deleteBookmark(Long diarySetId) {
	}

}
