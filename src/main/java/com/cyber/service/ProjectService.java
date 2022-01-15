package com.cyber.service;

import com.cyber.dto.ProjectDTO;
import com.cyber.entity.User;
import com.cyber.exception.TicketNGProjectException;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ProjectService {

    ProjectDTO getByProjectCode(String code);
    List<ProjectDTO> listAllProjects();
    ProjectDTO save(ProjectDTO dto) throws TicketNGProjectException;
    ProjectDTO update(ProjectDTO dto) throws TicketNGProjectException;
    void delete(String code) throws TicketNGProjectException;
    ProjectDTO complete(String code) throws TicketNGProjectException;
    List<ProjectDTO> listAllProjectDetails() throws AccessDeniedException, TicketNGProjectException;
    List<ProjectDTO> readAllByAssignedManager(User user);
    List<ProjectDTO> listAllNonCompletedProjects();
}
