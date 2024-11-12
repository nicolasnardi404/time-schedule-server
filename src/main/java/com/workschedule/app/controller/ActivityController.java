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

import java.util.Calendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ArrayList;

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
                        .isClosed(activity.isClosed())
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
                    .isClosed(false)
                    .build();

            Activity savedActivity = activityRepository.save(activity);
            return ResponseEntity.ok(savedActivity);
        } catch (Exception e) {
            System.err.println("Error creating activity: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Failed to create activity",
                            "error", e.getMessage()));
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

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleActivityStatus(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Optional<Activity> activityOpt = activityRepository.findById(id);
            if (activityOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Activity activity = activityOpt.get();

            // Toggle the status
            activity.setClosed(!activity.isClosed());
            Activity updatedActivity = activityRepository.save(activity);

            return ResponseEntity.ok(Map.of(
                    "id", updatedActivity.getId(),
                    "isClosed", updatedActivity.isClosed(),
                    "message", "Activity status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to update activity status"));
        }
    }

    // Add a new endpoint to close all activities for a specific month
    @PutMapping("/project/{projectId}/close-month")
    public ResponseEntity<?> closeActivitiesForMonth(
            @PathVariable Long projectId,
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        try {
            Optional<Project> project = projectRepository.findById(projectId);
            if (project.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Verify user has access to this project
            if (!project.get().getUser().getUsername().equals(authentication.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "User does not have access to this project"));
            }

            // Get all activities for the project
            List<Activity> activities = activityRepository.findAllByProjectId(projectId);

            // Calculate the start and end dates for the specified month
            Calendar startCal = Calendar.getInstance();
            startCal.set(year, month - 1, 1, 0, 0, 0);
            startCal.set(Calendar.MILLISECOND, 0);

            Calendar endCal = Calendar.getInstance();
            endCal.set(year, month - 1, 1, 0, 0, 0);
            endCal.set(Calendar.MILLISECOND, 0);
            endCal.add(Calendar.MONTH, 1);
            endCal.add(Calendar.MILLISECOND, -1);

            Date startDate = startCal.getTime();
            Date endDate = endCal.getTime();

            int updatedCount = 0;
            List<Activity> updatedActivities = new ArrayList<>();

            // Close all activities that fall within the specified month
            for (Activity activity : activities) {
                if (activity.getBeginning().compareTo(startDate) >= 0 &&
                        activity.getBeginning().compareTo(endDate) <= 0) {
                    activity.setClosed(true);
                    updatedActivities.add(activity);
                    updatedCount++;
                }
            }

            // Batch save all updated activities
            if (!updatedActivities.isEmpty()) {
                activityRepository.saveAll(updatedActivities);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Activities closed successfully",
                    "updatedCount", updatedCount,
                    "monthYear", String.format("%d-%02d", year, month)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to close activities"));
        }
    }

    @GetMapping("/project/{projectId}/open")
    public ResponseEntity<List<ActivityDTO>> getOpenActivities(@PathVariable Long projectId) {
        List<Activity> activities = activityRepository.findAllByProjectIdAndIsClosed(projectId, false);
        List<ActivityDTO> activityDTOs = activities.stream()
                .map(activity -> ActivityDTO.builder()
                        .id(activity.getId())
                        .description(activity.getDescription())
                        .beginning(activity.getBeginning())
                        .end(activity.getEnd())
                        .isClosed(activity.isClosed())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(activityDTOs);
    }

    @GetMapping("/project/{projectId}/closed")
    public ResponseEntity<List<ActivityDTO>> getClosedActivities(@PathVariable Long projectId) {
        List<Activity> activities = activityRepository.findAllByProjectIdAndIsClosed(projectId, true);
        List<ActivityDTO> activityDTOs = activities.stream()
                .map(activity -> ActivityDTO.builder()
                        .id(activity.getId())
                        .description(activity.getDescription())
                        .beginning(activity.getBeginning())
                        .end(activity.getEnd())
                        .isClosed(activity.isClosed())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(activityDTOs);
    }
}
