package com.workschedule.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.workschedule.app.entity.Project;
import com.workschedule.app.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p JOIN p.user  u WHERE u.id = :userId")
    Optional<List<Project>> findAllProjectsByIdUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) > 0 FROM Project p WHERE p.user.id = :userId AND p.nameProject = :nameProject")
    boolean checkIfNameProjectIsUnique(@Param("userId") Long userId, @Param("nameProject") String nameProject);

    @Query("SELECT p FROM Project p  WHERE p.user.id = :userId AND LOWER(p.nameProject) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Optional<List<Project>> findBySearchTerm(@Param("searchTerm") String searchTerm, @Param("userId") Long userId);

    @Query("SELECT p FROM Project p JOIN p.user u WHERE u.id = :userId ORDER BY p.id DESC")
    Page<Project> findProjectsPerPageByIdUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.user.id = :userId")
    long countAllProjectsPerUser(@Param("userId") Long userId);

}
