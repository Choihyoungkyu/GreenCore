package com.chicochico.domain.feed.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;


/**
 * 관찰 일지 생성 요청
 * userPlantId : Long
 * image: MultipartFile,
 * startDate": LocalDate,
 * title: String,
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiarySetRequestDto {

	private Long userPlantId;
	private MultipartFile image;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate;
	private String title;

}
