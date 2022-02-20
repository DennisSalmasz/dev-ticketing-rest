package com.cyber.controller;

import com.cyber.annotation.DefaultExceptionMessage;
import com.cyber.dto.ProjectDTO;
import com.cyber.entity.ResponseWrapper;
import com.cyber.exception.TicketNGProjectException;
import com.cyber.service.ProjectService;
import com.cyber.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
@Tag(name = "Project Controller",description = "Project API")
public class ProjectController {

    private ProjectService projectService;
    private UserService userService;

    public ProjectController(@Lazy ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    //admin & manager retrieve all projects
    @GetMapping
    @Operation(summary = "Retrieve all projects")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving all projects !!!")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> readAll()  {
        List<ProjectDTO> listProjectDTO = projectService.listAllProjects();
        return ResponseEntity.ok(new ResponseWrapper("Projects are retrieved successfully",listProjectDTO));
    }

    //retrieve certain project by project code
    @GetMapping("/{projectCode}")
    @Operation(summary = "Retrieve certain project by project code")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving certain project by project code !!!")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> readByProjectCode(@PathVariable("projectCode") String projectCode)  {
        ProjectDTO projectDTO = projectService.getByProjectCode(projectCode);
        return ResponseEntity.ok(new ResponseWrapper("Certain project is retrieved successfully",projectDTO));
    }

    //create project
    @PostMapping
    @Operation(summary = "Create project")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while creating project !!!")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> create(@RequestBody ProjectDTO projectDTO) throws TicketNGProjectException {
        ProjectDTO createdProject = projectService.save(projectDTO);
        return ResponseEntity.ok(new ResponseWrapper("Project is created successfully",createdProject));
    }

    //update project by project code
    @PutMapping
    @Operation(summary = "Update project")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while updating project !!!")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO projectDTO) throws TicketNGProjectException {
        ProjectDTO updatedProject = projectService.update(projectDTO);
        return ResponseEntity.ok(new ResponseWrapper("Project is updated successfully",updatedProject));
    }

    //delete project by project code
    @DeleteMapping("/{projectCode}")
    @Operation(summary = "Delete certain project by project code")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while deleting certain project by project code !!!")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable("projectCode") String projectCode) throws TicketNGProjectException {
        projectService.delete(projectCode);
        return ResponseEntity.ok(new ResponseWrapper("Certain project is deleted successfully"));
    }

    //complete certain project by project code
    @PutMapping("/complete/{projectCode}")
    @Operation(summary = "Complete certain project by project code")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while completing certain project by project code !!!")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> completeProject(@PathVariable("projectCode") String projectCode) throws TicketNGProjectException {
        ProjectDTO completedProject = projectService.complete(projectCode);
        return ResponseEntity.ok(new ResponseWrapper("Certain project is complete successfully",completedProject));
    }

    //retrieve all project details by manager
    @GetMapping("/details")
    @Operation(summary = "Retrieve all project details")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving all project details by manager !!!")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllProjectDetails() throws AccessDeniedException, TicketNGProjectException {
        List<ProjectDTO> listProjectDTO = projectService.listAllProjectDetails();
        return ResponseEntity.ok(new ResponseWrapper("All project details are retrieved successfully",listProjectDTO));
    }

    //get updated project by project code
}
