package com.chicochico.domain.plant.controller;


import com.chicochico.common.dto.ResultDto;
import com.chicochico.domain.plant.dto.response.PlantDocResponseDto;
import com.chicochico.domain.plant.dto.response.PlantResponseDto;
import com.chicochico.domain.plant.dto.response.PlantWithImageResponseDto;
import com.chicochico.domain.plant.entity.PlantEntity;
import com.chicochico.domain.plant.service.PlantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/plant")
@RequiredArgsConstructor
@Api(tags = "식물 API")
public class PlantController {

	private final PlantService plantService;


	@GetMapping
	@ApiOperation(value = "홈화면에서 식물이름을 검색합니다.", notes = "")
	public ResponseEntity<ResultDto<Page<PlantWithImageResponseDto>>> getPlantWithImageList(@RequestParam("search") String search, Pageable pageable) {
		Page<PlantEntity> plantList = plantService.getPlantWithImageList(search, pageable);
		// TODO : entity page -> dto page 변환 추가

		return ResponseEntity.ok().body(ResultDto.of(Page.empty()));
	}


	@GetMapping(value = "/docs", params = { "search" })
	@ApiOperation(value = "도감페이지에서 식물이름을 검색합니다.", notes = "")
	public ResponseEntity<ResultDto<Page<PlantResponseDto>>> getPlantList(@RequestParam("search") String search, Pageable pageable) {
		Page<PlantEntity> plantList = plantService.getPlantList(search, pageable);
		// TODO : entity page -> dto page 변환 추가

		return ResponseEntity.ok().body(ResultDto.of(Page.empty()));
	}


	@GetMapping(value = "/docs", params = { "index" })
	@ApiOperation(value = "식물 도감 목록을 조회합니다.", notes = "")
	public ResponseEntity<ResultDto<Page<PlantResponseDto>>> getPlantListByIndex(@RequestParam("index") String index, Pageable pageable) {
		Page<PlantEntity> plantList = plantService.getPlantListByIndex(index, pageable);
		// TODO : entity page -> dto page 변환 추가

		return ResponseEntity.ok().body(ResultDto.of(Page.empty()));
	}


	@GetMapping("/docs/{plantId}")
	@ApiOperation(value = "식물 도감에서 상세 조회합니다.", notes = "")
	public ResponseEntity<ResultDto<PlantDocResponseDto>> getPlant(@PathVariable("plantId") String plantId) {
		PlantEntity plant = plantService.getPlant(plantId);
		PlantDocResponseDto plantDocResponseDto = PlantDocResponseDto.fromEntity(plant);

		return ResponseEntity.ok().body(ResultDto.of(plantDocResponseDto));
	}


	@GetMapping("/population")
	@ApiOperation(value = "인기 식물을 조회합니다.", notes = "")
	public ResponseEntity<ResultDto<List<PlantWithImageResponseDto>>> getPopularPlantList() {
		List<PlantEntity> plantList = plantService.getPopularPlantList();
		List<PlantWithImageResponseDto> plantWithImageResponseDtoList = PlantWithImageResponseDto.fromEnityList(plantList);

		return ResponseEntity.ok().body(ResultDto.of(plantWithImageResponseDtoList));
	}

}
