package com.chicochico.domain.schedule.dto.request;


import com.chicochico.common.code.IsDeletedType;
import com.chicochico.common.code.ScheduleType;
import com.chicochico.domain.schedule.entity.ScheduleEntity;
import com.chicochico.domain.user.entity.UserEntity;
import com.chicochico.domain.user.entity.UserPlantEntity;
import lombok.Data;

import java.time.LocalDate;


/**
 * 스케줄 생성, 수정 요청
 */
@Data
public class ScheduleRequestDto {

	private LocalDate scheduleDate;
	private ScheduleType scheduleCode;
	private Long userPlantId;
	private String content;


	public ScheduleEntity toEntity(UserPlantEntity userPlant, UserEntity user) {

		return ScheduleEntity.builder()
			.date(scheduleDate)
			.userPlant(userPlant)
			.content(content)
			.userPlant(userPlant)
			.user(user)
			.isCompleted(false)
			.isDeleted(IsDeletedType.N)
			.build();
	}

}
