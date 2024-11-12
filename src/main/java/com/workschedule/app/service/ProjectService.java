package com.workschedule.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.workschedule.app.entity.Project;
import com.workschedule.app.repository.ProjectRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Page<Project> getProjectsForUser(Long userId, Pageable pageable) {
        return projectRepository.findProjectsPerPageByIdUser(userId, pageable);

    }

    public long getTotalProjectsCount(Long userId) {
        return projectRepository.countAllProjectsPerUser(userId);
    }

}
