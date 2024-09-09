package com.workschedule.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.workschedule.app.entity.Role;
import com.workschedule.app.entity.User;

@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, Long> {

}
