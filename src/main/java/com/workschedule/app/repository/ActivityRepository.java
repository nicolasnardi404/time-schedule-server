package com.workschedule.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.workschedule.app.entity.Activity;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query("SELECT a FROM Activity a WHERE a.project.id = :projectId")
    List<Activity> findAllByProjectId(Long projectId);

    List<Activity> findAllByProjectIdAndIsClosed(Long projectId, boolean isClosed);
}
