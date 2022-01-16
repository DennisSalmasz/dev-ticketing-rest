package com.cyber.service;

import com.cyber.dto.ProjectDTO;
import com.cyber.dto.TaskDTO;
import com.cyber.dto.UserDTO;
import com.cyber.entity.User;
import com.cyber.exception.TicketNGProjectException;
import com.cyber.util.MapperUtil;
import com.cyber.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    private ProjectService projectService;
    private TaskService taskService;
    private MapperUtil mapperUtil;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(@Lazy UserRepository userRepository, @Lazy ProjectService projectService, TaskService taskService, MapperUtil mapperUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.taskService = taskService;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll(Sort.by("firstName"));
        return list.stream().map(obj -> {return mapperUtil.convert(obj,new UserDTO());}).collect(Collectors.toList());
    }

    //check for authorities !!
    @Override
    public UserDTO findByUserName(String username) throws AccessDeniedException {
        User user = userRepository.findByUserName(username);
        checkForAuthorities(user);
        return mapperUtil.convert(user,new UserDTO());
    }

    @Override
    public UserDTO save(UserDTO dto) throws TicketNGProjectException {
        User foundUser = userRepository.findByUserName(dto.getUserName());
        if(foundUser != null){
            throw new TicketNGProjectException("User already exist !!!");
        }
        User user = mapperUtil.convert(dto,new User());
        user.setPassWord(passwordEncoder.encode(user.getPassWord())); //encode password, before saving in DB
        User save = userRepository.save(user);
        return mapperUtil.convert(save,new UserDTO());
    }

    @Override
    public UserDTO update(UserDTO dto) throws TicketNGProjectException, AccessDeniedException {
        //find current user - that has id
        User user = userRepository.findByUserName(dto.getUserName());
        //check if user exists in DB to avoid the crash of the app !!
        if(user == null){
            throw new TicketNGProjectException("User does not exist !!");
        }
        //map user dto into entity object
        User convertedUser = mapperUtil.convert(dto,new User());
        //encode password, before saving in DB
        convertedUser.setPassWord(passwordEncoder.encode(convertedUser.getPassWord()));
        //don't do any update [set enable = true], until user does confirmation through email !!
        if(!user.getEnabled()){
            throw new TicketNGProjectException("User is not confirmed by email");
        }
        //check for authorities
        checkForAuthorities(user);
        //do the update
        convertedUser.setEnabled(true);
        //set id to the converted object
        convertedUser.setId(user.getId());
        //save updated user
        userRepository.save(convertedUser);
        return  findByUserName(dto.getUserName());
    }

    @Override
    public void delete(String username) throws TicketNGProjectException {
        User user = userRepository.findByUserName(username);
        //check if user exists in DB to avoid the crash of the app !!
        if(user == null){
            throw new TicketNGProjectException("User does not exist !!");
        }
        //check if user can be deleted
        if(!checkIfUserCanBeDeleted(user)){
            throw new TicketNGProjectException("User cannot be deleted. It is linked by a project or a task !!");
        }
        //since username is unique, if I delete it, it will be set true with the following code in DB -- so, I cannot create with same username
        user.setUserName(user.getUserName() + "-" + user.getId());
        user.setIsDeleted(true); //now, related row in DB will not be deleted !!
        userRepository.save(user);
    }

    @Override
    public void deleteByUserName(String username) {
        userRepository.deleteByUserName(username);
    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(role);
        return users.stream().map(obj -> mapperUtil.convert(obj,new UserDTO())).collect(Collectors.toList());
    }

    @Override
    public Boolean checkIfUserCanBeDeleted(User user) {

        switch (user.getRole().getDescription()){
            case "Manager":
                List<ProjectDTO> projectList = projectService.readAllByAssignedManager(user);
                return projectList.size() == 0;
            case "Employee":
                List<TaskDTO> taskList = taskService.readAllByEmployee(user);
                return taskList.size() == 0;
            default:
                return true;
        }
    }

    @Override
    public UserDTO confirm(User user) {
        user.setEnabled(true);
        User confirmedUser = userRepository.save(user);
        return mapperUtil.convert(confirmedUser,new UserDTO());
    }

    //readByUser()
    //admin can access anything, if role is not admin, and login user and searching user are not matching, deny access !!
    private void checkForAuthorities(User user) throws AccessDeniedException {
        //give me authentication object, not only [id] -- will return the role !!
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //if you don't log in, Spring returns getName() as [anonymousUser]
        //so, authentication should not be null & user should be logged in - if logged in, getName() will be [id]
        if(authentication != null && !authentication.getName().equals("anonymousUser")){
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            //authentication.getName() -- [id]
            //user.getId().toString() -- [id]
            if(!authentication.getName().equals(user.getId().toString()) || roles.contains("Admin")){
                throw new AccessDeniedException("Access is denied !!!!!");
            }
        }
    }
}
