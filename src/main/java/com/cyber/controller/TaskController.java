package com.cyber.controller;

import com.cyber.annotation.DefaultExceptionMessage;
import com.cyber.dto.TaskDTO;
import com.cyber.entity.ResponseWrapper;
import com.cyber.enums.Status;
import com.cyber.exception.TicketNGProjectException;
import com.cyber.service.ProjectService;
import com.cyber.service.TaskService;
import com.cyber.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@Tag(name = "Task Controller",description = "Task API")
public class TaskController {

    TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //retrieve tasks
    @GetMapping
    @Operation(summary = "Retrieve all tasks")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving tasks !!!")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAll()  {
        List<TaskDTO> listTasks = taskService.listAllTasks();
        return ResponseEntity.ok(new ResponseWrapper("Tasks are retrieved successfully",listTasks));
    }

    //retrieve all tasks by manager
    @GetMapping("/project-manager")
    @Operation(summary = "Retrieve all tasks by project manager")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving all tasks by project manager !!!")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> readAllByProjectManager() throws TicketNGProjectException {
        List<TaskDTO> listTasks = taskService.listAllTasksByProjectManager();
        return ResponseEntity.ok(new ResponseWrapper("All tasks by project manager are retrieved successfully",listTasks));
    }

    //retrieve task by id
    @GetMapping("/{id}")
    @Operation(summary = "Retrieve task by id")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving task by id !!!")
    @PreAuthorize("hasAnyAuthority('Manager','Employee')")
    public ResponseEntity<ResponseWrapper> readById(@PathVariable("id") Long id) throws TicketNGProjectException {
        TaskDTO taskDTO = taskService.findById(id);
        return ResponseEntity.ok(new ResponseWrapper("Task by id is retrieved successfully",taskDTO));
    }

    //create task
    @PostMapping
    @Operation(summary = "Create task")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while creating task !!!")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> create(@RequestBody TaskDTO taskDTO)  {
        TaskDTO createdTask = taskService.save(taskDTO);
        return ResponseEntity.ok(new ResponseWrapper("Task is created successfully",createdTask));
    }

    //delete task by id
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while deleting task !!!")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> delete(@PathVariable("id") Long id) throws TicketNGProjectException {
        taskService.delete(id);
        return ResponseEntity.ok(new ResponseWrapper("Task is deleted successfully"));
    }

    //update task
    @PutMapping
    @Operation(summary = "Update task")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while updating task !!!")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ResponseWrapper> update(@RequestBody TaskDTO taskDTO) throws TicketNGProjectException {
        TaskDTO updatedTaskDTO = taskService.update(taskDTO);
        return ResponseEntity.ok(new ResponseWrapper("Task is updated successfully",updatedTaskDTO));
    }

    //employee retrieves all non-completest tasks
    @GetMapping("/employee")
    @Operation(summary = "Retrieve all non-completed tasks")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving non-completed tasks !!!")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<ResponseWrapper> employeeReadNonCompletedTasks() throws TicketNGProjectException {
        List<TaskDTO> listTaskDTO = taskService.listAllTasksByStatusIsNot(Status.COMPLETE);
        return ResponseEntity.ok(new ResponseWrapper("Non-completed tasks are retrieved successfully",listTaskDTO));
    }

    //employee updates task status
    @PutMapping("/employee/update")
    @Operation(summary = "Employee updates task status")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while employee is updating task status !!!")
    @PreAuthorize("hasAuthority('Employee')")
    public ResponseEntity<ResponseWrapper> employeeUpdateTask(@RequestBody TaskDTO taskDTO) throws TicketNGProjectException {
        TaskDTO updatedTaskDTO = taskService.updateStatus(taskDTO);
        return ResponseEntity.ok(new ResponseWrapper("Employee updated task status successfully",updatedTaskDTO));
    }
















}
