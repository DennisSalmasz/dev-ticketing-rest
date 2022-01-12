package com.cyber.implementation;

import com.cyber.dto.ProjectDTO;
import com.cyber.dto.UserDTO;
import com.cyber.entity.Project;
import com.cyber.entity.User;
import com.cyber.enums.Status;
import com.cyber.mapper.MapperUtil;
import com.cyber.repository.ProjectRepository;
import com.cyber.service.ProjectService;
import com.cyber.service.TaskService;
import com.cyber.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectRepository projectRepository;
    private UserService userService;
    private TaskService taskService;
    private MapperUtil mapperUtil;

    public ProjectServiceImpl(@Lazy ProjectRepository projectRepository, UserService userService, TaskService taskService, MapperUtil mapperUtil) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.taskService = taskService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project = projectRepository.findByProjectCode(code);
        return mapperUtil.convert(project,new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(obj -> {return mapperUtil.convert(obj,new ProjectDTO());}).collect(Collectors.toList());
    }

    @Override
    public void save(ProjectDTO dto) {
        dto.setProjectStatus(Status.OPEN);
        Project obj = mapperUtil.convert(dto,new Project());
        //need to convert assigned manager to entity as well - it is dependant !!
        //obj.setAssignedManager(userMapper.convertToEntity(dto.getAssignedManager()));
        projectRepository.save(obj);
    }

    @Override
    public void update(ProjectDTO dto) {
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        Project convertedProject = mapperUtil.convert(dto,new Project());
        convertedProject.setId(project.getId());
        convertedProject.setProjectStatus(project.getProjectStatus());
        projectRepository.save(convertedProject);
    }

    @Override
    public void delete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setIsDeleted(true); //now, related row in DB will not be deleted !!

        //when I delete a project, its tasks should be deleted as well

        //now I am able to create another project with the same project code
        project.setProjectCode(project.getProjectCode() + "-" + project.getId());
        projectRepository.save(project);

        taskService.deleteByProject(mapperUtil.convert(project,new ProjectDTO()));
    }

    @Override
    public void complete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {
        //in header.html -- sec:authentication="name" -- getName() comes from here
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO currentUserDTO = userService.findByUserName(username);
        User user = mapperUtil.convert(currentUserDTO,new User());
        List<Project> list = projectRepository.findAllByAssignedManager(user);

        return list.stream().map(project -> {
                    ProjectDTO obj = mapperUtil.convert(project,new ProjectDTO());
                    obj.setIncompleteTaskCount(taskService.totalUncompletedTasks(obj.getProjectCode()));
                    obj.setCompleteTaskCount(taskService.totalCompletedTasks(obj.getProjectCode()));
                    return obj;
                }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(obj -> {return mapperUtil.convert(obj,new ProjectDTO());}).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {

        return projectRepository
                .findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream()
                .map(project -> mapperUtil.convert(project,new ProjectDTO()))
                .collect(Collectors.toList());
    }


}
