package com.cyber.implementation;

import com.cyber.dto.ProjectDTO;
import com.cyber.dto.TaskDTO;
import com.cyber.entity.Project;
import com.cyber.entity.Task;
import com.cyber.entity.User;
import com.cyber.enums.Status;
import com.cyber.exception.TicketNGProjectException;
import com.cyber.util.MapperUtil;
import com.cyber.repository.TaskRepository;
import com.cyber.repository.UserRepository;
import com.cyber.service.TaskService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private MapperUtil mapperUtil;

    public TaskServiceImpl(@Lazy TaskRepository taskRepository, @Lazy UserRepository userRepository, MapperUtil mapperUtil) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(obj -> mapperUtil.convert(obj,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByProjectManager() throws TicketNGProjectException {
        String id = SecurityContextHolder.getContext().getAuthentication().getName(); //WebSecurityConfig - security holds the user [id] to authenticate !!
        Long currentId = Long.parseLong(id);
        //get user based on user [id]
        User user = userRepository.findById(currentId).orElseThrow(() -> new TicketNGProjectException("This manager does not exist !!!"));
        //get tasks based on the user
        List<Task> tasks = taskRepository.findAllByProjectAssignedManager(user);
        return tasks.stream().map(obj -> mapperUtil.convert(obj,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public TaskDTO findById(Long id) throws TicketNGProjectException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TicketNGProjectException("Task does not exist !!!"));
        return mapperUtil.convert(task, new TaskDTO());
    }

    @Override
    public TaskDTO save(TaskDTO taskDTO) {
        taskDTO.setTaskStatus(Status.OPEN);
        taskDTO.setAssignedDate(LocalDate.now());
        Task task = mapperUtil.convert(taskDTO,new Task());
        taskRepository.save(task);
        return mapperUtil.convert(task,new TaskDTO());
    }

    @Override
    public void delete(Long id) throws TicketNGProjectException {
        Task foundTask = taskRepository.findById(id).orElseThrow(() -> new TicketNGProjectException("Task does not exist !!!"));
        foundTask.setIsDeleted(true);
        taskRepository.save(foundTask);
    }

    @Override
    public TaskDTO update(TaskDTO dto) throws TicketNGProjectException {
        taskRepository.findById(dto.getId()).orElseThrow(() -> new TicketNGProjectException("Task does not exist !!!"));
        Task convertedTask = mapperUtil.convert(dto,new Task());
        Task save = taskRepository.save(convertedTask);
        return mapperUtil.convert(save,new TaskDTO());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) throws TicketNGProjectException {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentId = Long.parseLong(id);
        User user = userRepository.findById(currentId).orElseThrow(() -> new TicketNGProjectException("This manager does not exist !!!"));
        List<Task> list = taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee(status, user);
        return list.stream().map(obj -> mapperUtil.convert(obj,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public TaskDTO updateStatus(TaskDTO taskDTO) throws TicketNGProjectException {
        Task task = taskRepository.findById(taskDTO.getId()).orElseThrow(() -> new TicketNGProjectException("Task does not exist !!!"));
        task.setTaskStatus(taskDTO.getTaskStatus());
        Task save = taskRepository.save(task);
        return mapperUtil.convert(save,new TaskDTO());
    }

    // -----------

    @Override
    public int totalUncompletedTasks(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    @Override
    public int totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO project) {
        List<TaskDTO> taskList = listAllByProject(project);
        taskList.forEach(taskDTO -> {
            try {
                delete(taskDTO.getId());
            } catch (TicketNGProjectException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<TaskDTO> listAllByProject(ProjectDTO projectDTO) {
        Project project = mapperUtil.convert(projectDTO,new Project());
        List<Task> list = taskRepository.findAllByProject(project);
        return list.stream().map(obj -> mapperUtil.convert(obj,new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> readAllByEmployee(User employee) {
        List<Task> tasks = taskRepository.findAllByAssignedEmployee(employee);
        return tasks.stream().map(obj -> mapperUtil.convert(obj,new TaskDTO())).collect(Collectors.toList());
    }
}
