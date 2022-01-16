package com.cyber.service;

import com.cyber.dto.ProjectDTO;
import com.cyber.dto.TaskDTO;
import com.cyber.entity.Task;
import com.cyber.entity.User;
import com.cyber.enums.Status;
import com.cyber.exception.TicketNGProjectException;

import java.util.List;

public interface TaskService {

    List<TaskDTO> listAllTasks();
    List<TaskDTO> listAllTasksByProjectManager() throws TicketNGProjectException;
    TaskDTO findById(Long id) throws TicketNGProjectException;
    TaskDTO save(TaskDTO dto);
    void delete(Long id) throws TicketNGProjectException;
    TaskDTO update(TaskDTO dto) throws TicketNGProjectException;
    List<TaskDTO> listAllTasksByStatusIsNot(Status status) throws TicketNGProjectException;
    TaskDTO updateStatus(TaskDTO dto) throws TicketNGProjectException;

    int totalUncompletedTasks(String projectCode);
    int totalCompletedTasks(String projectCode);
    void deleteByProject(ProjectDTO project);
    List<TaskDTO> listAllByProject(ProjectDTO project);
    List<TaskDTO> readAllByEmployee(User employee);


}
