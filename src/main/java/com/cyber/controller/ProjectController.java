package com.cyber.controller;

import com.cyber.dto.ProjectDTO;
import com.cyber.dto.UserDTO;
import com.cyber.service.ProjectService;
import com.cyber.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/project")
public class ProjectController {

    private ProjectService projectService;
    private UserService userService; // we just basically put all the users as managers !!

    public ProjectController(@Lazy ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/create")
    public String createProject(Model model){
        model.addAttribute("project", new ProjectDTO());
        model.addAttribute("managers",userService.listAllByRole("manager")); // assign managers
        model.addAttribute("projects",projectService.listAllProjects());
        return "/project/create";
    }

    @PostMapping("/create")
    public String insertProject(ProjectDTO project, Model model){
        projectService.save(project);
        return "redirect:/project/create";
    }

    @GetMapping("/delete/{projectCode}")
    public String deleteProject(@PathVariable("projectCode") String projectCode){
        projectService.delete(projectCode);
        return "redirect:/project/create";
    }

    @GetMapping("/complete/{projectCode}")
    public String completeProject(@PathVariable("projectCode") String projectCode){
        projectService.complete(projectCode);
        return "redirect:/project/create";
    }

    @GetMapping("/update/{projectCode}")
    public String editProject(@PathVariable("projectCode") String projectCode, Model model){
        model.addAttribute("project", projectService.getByProjectCode(projectCode));
        model.addAttribute("managers",userService.listAllByRole("manager"));
        model.addAttribute("projects",projectService.listAllProjects());
        return "/project/update";
    }

    @PostMapping ("/update/{projectCode}")
    public String updateProject(@PathVariable("projectCode") String projectCode, ProjectDTO project, Model model){
        projectService.update(project);
        return "redirect:/project/create";
    }

    @GetMapping("/manager/complete")
    public String getProjectByManager(Model model){
        List<ProjectDTO> projects = projectService.listAllProjectDetails();
        model.addAttribute("projects",projects);
        return "/manager/project-status";
    }

    @GetMapping("manager/complete/{projectCode}")
    public String manager_completed(@PathVariable("projectCode") String projectCode,Model model){
        projectService.complete(projectCode);
        return "redirect:/project/manager/complete";
    }
}
