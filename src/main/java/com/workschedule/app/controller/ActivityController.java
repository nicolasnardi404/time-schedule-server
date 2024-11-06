package com.workschedule.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.workschedule.app.dto.ActivityDTO;
import com.workschedule.app.entity.Activity;
import com.workschedule.app.entity.Project;
import com.workschedule.app.repository.ActivityRepository;
import com.workschedule.app.repository.ProjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityRepository activityRepository;
    private final ProjectRepository projectRepository;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Activity>> getAllActivities(@PathVariable Long projectId) {
        return ResponseEntity.ok(activityRepository.findAllByProjectId(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivity(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<Activity> createActivity(@PathVariable Long projectId, @RequestBody ActivityDTO activityDTO) {
        Optional<Project> project = projectRepository.findById(projectId);
        System.out.println(project.get());
        if (project.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Activity activity = Activity.builder()
                .project(project.get())
                .beginning(activityDTO.getBeginning())
                .end(activityDTO.getEnd())
                .description(activityDTO.getDescription())
                .build();

        return ResponseEntity.ok(activityRepository.save(activity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody ActivityDTO activityDTO) {
        Optional<Activity> existingActivity = activityRepository.findById(id);
        if (existingActivity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Activity activity = existingActivity.get();
        activity.setBeginning(activityDTO.getBeginning());
        activity.setEnd(activityDTO.getEnd());
        activity.setDescription(activityDTO.getDescription());

        return ResponseEntity.ok(activityRepository.save(activity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        if (!activityRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        activityRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
