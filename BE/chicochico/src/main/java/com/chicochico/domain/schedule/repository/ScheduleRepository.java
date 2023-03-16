package com.chicochico.domain.schedule.repository;


import com.chicochico.domain.schedule.entity.ScheduleEntity;
import com.chicochico.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

	List<ScheduleEntity> findAllByUserAndBetweenDateAndDate(UserEntity user, LocalDate localDatest, LocalDate localDateEd);

}
