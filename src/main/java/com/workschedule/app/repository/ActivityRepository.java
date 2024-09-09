package com.workschedule.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.workschedule.app.entity.Activity;
import com.workschedule.app.entity.Project;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

}
