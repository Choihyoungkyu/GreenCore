package com.chicochico.domain.plant.service;


import com.chicochico.domain.plant.entity.PlantEntity;
import com.chicochico.domain.plant.repository.PlantRepository;
import com.chicochico.exception.CustomException;
import com.chicochico.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PlantService {

	private final PlantRepository plantRepository;
	

	/**
	 * 홈화면에서 식물이름을 검색합니다.
	 *
	 * @param search   검색할 식물 이름
	 * @param pageable 페이지네이션
	 * @return 식물 조회 페이지
	 */
	public Page<PlantEntity> getPlantWithImageList(String search, Pageable pageable) {
		Page<PlantEntity> plantEntityPage = plantRepository.findAllByNameContaining(search, pageable);
		return plantEntityPage;
	}


	/**
	 * 도감페이지에서 식물이름을 검색합니다.
	 *
	 * @param search   검색할 식물 이름
	 * @param pageable 페이지네이션
	 * @return 식물 조회 페이지
	 */
	public Page<PlantEntity> getPlantList(String search, Pageable pageable) {
		Page<PlantEntity> plantEntityPage = plantRepository.findAllByNameContaining(search, pageable);
		return plantEntityPage;
	}


	/**
	 * 식물 도감 목록을 조회합니다.
	 *
	 * @param index    식물도감 index (ex, "ㄱ")
	 * @param pageable 페이지네이션
	 * @return 식물 조회 페이지
	 */
	public Page<PlantEntity> getPlantListByIndex(String index, Pageable pageable) {
		Page<PlantEntity> plantEntityPage = plantRepository.findAllByNameContaining(index, pageable);
		return plantEntityPage;
	}


	/**
	 * 식물 도감에서 상세 조회합니다.
	 *
	 * @param plantId 상세조회할 식물 id
	 * @return 조회된 식물 정보
	 */
	public PlantEntity getPlant(Long plantId) {
		PlantEntity plant = plantRepository.findById(plantId).orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
		return plant;
	}


	/**
	 * 인기 식물을 조회합니다.
	 *
	 * @return 인기 식물 리스트
	 */
	public List<PlantEntity> getPopularPlantList() {
		List<PlantEntity> plantList = plantRepository.findTop5ByOrderByUserCountDesc();
		return plantList;
	}

}
