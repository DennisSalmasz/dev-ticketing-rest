package com.cyber.controller;

import com.cyber.dto.UserDTO;
import com.cyber.exception.TicketNGProjectException;
import com.cyber.service.RoleService;
import com.cyber.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
public class UserController {

    RoleService roleService;
    UserService userService;

    public UserController(@Lazy RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("create")
    public String createUser(Model model){

        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles",roleService.listAllRoles());
        model.addAttribute("users",userService.listAllUsers());

        return "user/create";
    }

    @PostMapping("/create")
    public String insertUser(UserDTO user,Model model){
        userService.save(user);
        return "redirect:/user/create";
    }

    @GetMapping("update/{username}")
    public String editUser(@PathVariable("username") String username, Model model){

        model.addAttribute("user", userService.findByUserName(username));
        model.addAttribute("roles",roleService.listAllRoles());
        model.addAttribute("users",userService.listAllUsers());

        return "user/update";
    }

    @PostMapping("update/{username}")
    public String updateUser(@PathVariable("username") String username, UserDTO user, Model model){

        userService.update(user);
        return "redirect:/user/create";
    }

    @GetMapping("delete/{username}")
    public String deleteUser(@PathVariable("username") String username) throws TicketNGProjectException {
        userService.delete(username);
        return "redirect:/user/create";
    }
}
