package com.cyber.service;

import com.cyber.dto.ProjectDTO;
import com.cyber.entity.User;

import java.util.List;

public interface ProjectService {

    ProjectDTO getByProjectCode(String code);
    List<ProjectDTO> listAllProjects();
    void save(ProjectDTO dto);
    void update(ProjectDTO dto);
    void delete(String code);
    void complete(String code);
    List<ProjectDTO> listAllProjectDetails();
    List<ProjectDTO> readAllByAssignedManager(User user);
    List<ProjectDTO> listAllNonCompletedProjects();
}
