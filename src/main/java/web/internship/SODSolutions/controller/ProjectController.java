package web.internship.SODSolutions.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.internship.SODSolutions.dto.request.ReqProjectDTO;
import web.internship.SODSolutions.dto.response.ApiResponse;
import web.internship.SODSolutions.dto.response.ResProjectDTO;

import web.internship.SODSolutions.services.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ProjectController {
    ProjectService projectService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<ResProjectDTO>>> getAllProjects() {
        List<ResProjectDTO> projects = projectService.getAllProjects();
        ApiResponse<List<ResProjectDTO>> response = ApiResponse.<List<ResProjectDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Projects fetched successfully")
                .data(projects)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ResProjectDTO>>> getProjectsByEmail() {
        List<ResProjectDTO> projects = projectService.getProjectByEmail();
        ApiResponse<List<ResProjectDTO>> response = ApiResponse.<List<ResProjectDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Projects fetched successfully")
                .data(projects)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ResProjectDTO>> createProject(@Valid @RequestBody ReqProjectDTO request) {
        ResProjectDTO createdProject = projectService.createProject(request);
        ApiResponse<ResProjectDTO> response = ApiResponse.<ResProjectDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("Project created successfully")
                .data(createdProject)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping()
    public ResponseEntity<ApiResponse<ResProjectDTO>> updateProject(@Valid @RequestBody ReqProjectDTO request) {
        ResProjectDTO updatedProject = projectService.updateProject(request);
        ApiResponse<ResProjectDTO> response = ApiResponse.<ResProjectDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Project updated successfully")
                .data(updatedProject)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Project deleted successfully")
                .data("")
                .build();
        return ResponseEntity.ok(response);
    }
}