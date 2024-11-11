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
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityRepository activityRepository;
    private final ProjectRepository projectRepository;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ActivityDTO>> getAllActivities(@PathVariable Long projectId) {
        List<Activity> activities = activityRepository.findAllByProjectId(projectId);
        List<ActivityDTO> activityDTOs = activities.stream()
            .map(activity -> ActivityDTO.builder()
                .id(activity.getId())
                .description(activity.getDescription())
                .beginning(activity.getBeginning())
                .end(activity.getEnd())
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(activityDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivity(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<?> createActivity(
            @PathVariable Long projectId, 
            @RequestBody ActivityDTO activityDTO,
            Authentication authentication) {
        try {
            System.out.println("User attempting to create activity: " + authentication.getName());
            System.out.println("Project ID: " + projectId);
            System.out.println("Activity data: " + activityDTO);

            Optional<Project> project = projectRepository.findById(projectId);
            if (project.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Verify user has access to this project
            if (!project.get().getUser().getUsername().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "User does not have access to this project"));
            }

            Activity activity = Activity.builder()
                    .project(project.get())
                    .beginning(activityDTO.getBeginning())
                    .end(activityDTO.getEnd())
                    .description(activityDTO.getDescription())
                    .build();

            Activity savedActivity = activityRepository.save(activity);
            return ResponseEntity.ok(savedActivity);
        } catch (Exception e) {
            System.err.println("Error creating activity: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", "Failed to create activity",
                    "error", e.getMessage()
                ));
        }
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
