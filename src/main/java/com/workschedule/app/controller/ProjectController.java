package com.workschedule.app.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workschedule.app.dto.ProjectDTO;
import com.workschedule.app.entity.Project;
import com.workschedule.app.entity.User;
import com.workschedule.app.repository.ProjectRepository;
import com.workschedule.app.repository.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @GetMapping("/{idUser}/all-projects")
    ResponseEntity<List<Project>> allProjectsPerUser(@PathVariable Long idUser) {
        try {
            Optional<List<Project>> optionalProject = projectRepository.findAllProjectsByIdUser(idUser);
            if (optionalProject.isPresent()) {
                List<Project> allProjects = optionalProject.get();
                return ResponseEntity.ok(allProjects);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{idUser}/get-project/{idProject}")
    ResponseEntity<ProjectDTO> getProjectByiD(@PathVariable Long idUser, @PathVariable Long idProject) {
        try {
            Optional<User> optionalUser = userRepository.findById(idUser);
            Optional<Project> optionalVideo = projectRepository.findById(idProject);

            if (!optionalUser.isPresent() || !optionalVideo.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Project project = optionalVideo.get();
            ProjectDTO projectDTO = new ProjectDTO(project.getNameProject(), project.getDescription(),
                    project.getValuePerHour());

            return ResponseEntity.ok(projectDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{idUser}/add-project")
    ResponseEntity<?> addNewProject(@PathVariable Long idUser, @RequestBody Project newProject) {
        try {
            System.out.println(newProject);
            Optional<User> optionalUser = userRepository.findById(idUser);
            if (optionalUser.isPresent()) {

                User user = optionalUser.get();
                boolean isUnique = projectRepository.checkIfNameProjectIsUnique(idUser, newProject.getNameProject());

                if (!isUnique) {
                    Project project = new Project();

                    project.setNameProject(newProject.getNameProject());
                    project.setValuePerHour(newProject.getValuePerHour());
                    project.setCreationDate(new Date());
                    project.setDescription(newProject.getDescription());

                    project.setUser(user);
                    handleProjectData(project, newProject);

                    return ResponseEntity.ok(project);
                } else {
                    return ResponseEntity.badRequest().body("YOU ALREADY HAVE A PROJECT WITH THIS NAME");
                }

            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{idUser}/update-project/{idProject}")
    ResponseEntity<Project> updateProject(@PathVariable Long idUser, @PathVariable Long idProject,
            @RequestBody Project newProject) {
        try {
            Optional<User> optionalUser = userRepository.findById(idUser);
            Optional<Project> optionalVideo = projectRepository.findById(idProject);

            if (!optionalUser.isPresent() || !optionalVideo.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = optionalUser.get();
            Project project = optionalVideo.get();

            handleProjectData(project, newProject);

            return ResponseEntity.ok(project);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{idUser}/projects/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long idUser, @PathVariable Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty() || !project.get().getUser().getId().equals(idUser)) {
            return ResponseEntity.notFound().build();
        }

        projectRepository.deleteById(projectId);
        return ResponseEntity.ok().build();
    }

    private void handleProjectData(Project project, Project newProject) {
        project.setNameProject(newProject.getNameProject());
        project.setValuePerHour(newProject.getValuePerHour());
        project.setCreationDate(new Date());
        project.setDescription(newProject.getDescription());

        projectRepository.save(project);
    }
}
