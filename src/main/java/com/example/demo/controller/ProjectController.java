package com.example.demo.controller;

import com.example.demo.dto.ProjectSummary;
import com.example.demo.model.Project;
import com.example.demo.repository.ProjectRepository;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        // Check if the user is authorized to create a project
        System.err.println("Authorization:  inside create project name ");
        Project saved = projectRepository.save(project);
        // Logic to create a project
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllProjects() {
        // Logic to retrieve all projects
        List<ProjectSummary> projectSummaryList= projectRepository.findAll().stream()
                .map(p -> new ProjectSummary(
                        p.getId(),
                        p.getProjectName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(projectSummaryList);
    }

}
