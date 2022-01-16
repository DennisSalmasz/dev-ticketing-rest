package com.cyber.service;

import com.cyber.dto.ProjectDTO;
import com.cyber.entity.Project;
import com.cyber.entity.User;
import com.cyber.enums.Status;
import com.cyber.exception.TicketNGProjectException;
import com.cyber.util.MapperUtil;
import com.cyber.repository.ProjectRepository;
import com.cyber.repository.UserRepository;
import com.cyber.service.ProjectService;
import com.cyber.service.TaskService;
import com.cyber.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectRepository projectRepository;
    private UserService userService;
    private TaskService taskService;
    private MapperUtil mapperUtil;
    private UserRepository userRepository;

    public ProjectServiceImpl(@Lazy ProjectRepository projectRepository, UserService userService, TaskService taskService, MapperUtil mapperUtil, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.taskService = taskService;
        this.mapperUtil = mapperUtil;
        this.userRepository = userRepository;
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
    public ProjectDTO save(ProjectDTO dto) throws TicketNGProjectException {
        Project foundProject = projectRepository.findByProjectCode(dto.getProjectCode());
        if(foundProject != null){
            throw new TicketNGProjectException("This project already exists !!!");
        }
        Project project = mapperUtil.convert(dto,new Project());
        Project createdProject = projectRepository.save(project);
        return mapperUtil.convert(createdProject,new ProjectDTO());
    }

    @Override
    public ProjectDTO update(ProjectDTO dto) throws TicketNGProjectException {
        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
        if(project == null){
            throw new TicketNGProjectException("This project does not exist !!!");
        }
        Project convertedProject = mapperUtil.convert(dto,new Project());
        Project updatedProject = projectRepository.save(convertedProject);
        return mapperUtil.convert(updatedProject,new ProjectDTO());
    }

    @Override
    public void delete(String code) throws TicketNGProjectException {
        Project project = projectRepository.findByProjectCode(code);
        if(project == null){
            throw new TicketNGProjectException("This project does not exist !!!");
        }
        //related row in DB will not be deleted !!
        project.setIsDeleted(true);

        //we change project code of the deleted project - so that we can create a new project with the same code - projectCode unique !!
        project.setProjectCode(project.getProjectCode() + "-" + project.getId());
        projectRepository.save(project);

        //when I delete a project, its tasks should be deleted as well
        taskService.deleteByProject(mapperUtil.convert(project,new ProjectDTO()));
    }

    @Override
    public ProjectDTO complete(String code) throws TicketNGProjectException {
        Project project = projectRepository.findByProjectCode(code);
        if(project == null){
            throw new TicketNGProjectException("This project does not exist !!!");
        }
        project.setProjectStatus(Status.COMPLETE);
        Project completedProject = projectRepository.save(project);
        return mapperUtil.convert(completedProject,new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() throws AccessDeniedException, TicketNGProjectException {

        String id = SecurityContextHolder.getContext().getAuthentication().getName(); //check WebSecurityConfig class !!
        Long currentId = Long.parseLong(id);
        User user = userRepository.findById(currentId).orElseThrow(() -> new TicketNGProjectException("This manager does not exist !!!"));
        List<Project> projectList = projectRepository.findAllByAssignedManager(user);
        if(projectList.size() == 0){
            throw new TicketNGProjectException("This manager does not have any project assigned !!!");
        }
        return projectList.stream().map(project -> {
                        ProjectDTO obj = mapperUtil.convert(project,new ProjectDTO());
                        obj.setIncompleteTaskCount(taskService.totalUncompletedTasks(obj.getProjectCode()));
                        obj.setCompleteTaskCount(taskService.totalCompletedTasks(obj.getProjectCode()));
                        return obj;
                    }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(obj -> mapperUtil.convert(obj,new ProjectDTO())).collect(Collectors.toList());
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
