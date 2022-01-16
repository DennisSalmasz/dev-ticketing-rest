package com.cyber.controller;

import com.cyber.annotation.DefaultExceptionMessage;
import com.cyber.dto.MailDTO;
import com.cyber.dto.UserDTO;
import com.cyber.entity.ConfirmationToken;
import com.cyber.entity.ResponseWrapper;
import com.cyber.entity.User;
import com.cyber.exception.TicketNGProjectException;
import com.cyber.util.MapperUtil;
import com.cyber.service.ConfirmationTokenService;
import com.cyber.service.RoleService;
import com.cyber.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Controller",description = "User API")
public class UserController {

    @Value("${app.local-url}")
    private String BASE_URL;

    private UserService userService;
    private MapperUtil mapperUtil;
    private RoleService roleService;
    private ConfirmationTokenService confirmationTokenService;

    public UserController(UserService userService, MapperUtil mapperUtil, RoleService roleService, ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.mapperUtil = mapperUtil;
        this.roleService = roleService;
        this.confirmationTokenService = confirmationTokenService;
    }

    //admin is able to create user
    @PostMapping("/create-user")
    @Operation(summary = "Create new account")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong with email !!!")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> doRegister(@RequestBody UserDTO userDTO) throws TicketNGProjectException {
        //create user, save it in DB, and then get it as dto
        UserDTO createdUser = userService.save(userDTO);
        sendEmail(createEmail(createdUser));
        return ResponseEntity.ok(new ResponseWrapper("User has been created",createdUser));
    }

    //admin is able to retrieve all the users
    @GetMapping
    @Operation(summary = "Read all users")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving users !!!")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> readAll()  {
        //retrieve users with business logic, and bind it to API
        List<UserDTO> result = userService.listAllUsers();
        return ResponseEntity.ok(new ResponseWrapper("Users are retrieved successfully",result));
    }

    //only admin should see other profiles, and all roles should see solely their profiles - WORK ON THAT !!
    @GetMapping("/{username}")
    @Operation(summary = "Read certain user by username")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving certain user !!!")
    //@PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> readByUsername(@PathVariable("username") String username) throws AccessDeniedException {
        UserDTO userDTO = userService.findByUserName(username);
        return ResponseEntity.ok(new ResponseWrapper("Certain user is retrieved successfully",userDTO));
    }

    //roles should be able to update certain user - no authorization ?
    @PutMapping
    @Operation(summary = "Update certain user")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while updating certain user !!!")
    public ResponseEntity<ResponseWrapper> updateUser(@RequestBody UserDTO userDTO) throws TicketNGProjectException, AccessDeniedException {
        UserDTO updatedUser = userService.update(userDTO);
        return ResponseEntity.ok(new ResponseWrapper("Certain user is updated successfully",updatedUser));
    }

    //admin should be able to delete certain user
    @DeleteMapping("/{username}")
    @Operation(summary = "Delete certain user by username")
    @PreAuthorize("hasAuthority('Admin')")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while deleting certain user !!!")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("username") String username) throws TicketNGProjectException {
        userService.delete(username);
        return ResponseEntity.ok(new ResponseWrapper("Certain user is deleted successfully"));
    }

    //Admin & Manager should be able to retrieve users based on their roles
    @GetMapping("/role")
    @Operation(summary = "Retrieve users based on their roles")
    @PreAuthorize("hasAnyAuthority('Admin','Manager')")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving users, based on their roles !!!")
    public ResponseEntity<ResponseWrapper> readByRole(@RequestParam String role) {
        List<UserDTO> userList = userService.listAllByRole(role);
        return ResponseEntity.ok(new ResponseWrapper("Users are retrieved successfully based on the roles",userList));
    }

    // ****************** methods ******************

    private MailDTO createEmail(UserDTO userDTO){

        User user = mapperUtil.convert(userDTO,new User());
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationToken.setIsDeleted(false);

        ConfirmationToken createdConfirmationToken = confirmationTokenService.save(confirmationToken);

        return MailDTO
                .builder()
                .emailTo(user.getUserName())
                .token(createdConfirmationToken.getToken())
                .subject("Confirm Registration")
                .message("To confirm your account, please click here: ")
                .url(BASE_URL + "/confirmation?token=")
                .build();
    }

    private void sendEmail(MailDTO mailDTO){

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(mailDTO.getEmailTo());
        mailMessage.setSubject(mailDTO.getSubject());
        mailMessage.setText(mailDTO.getMessage() + mailDTO.getUrl() + mailDTO.getToken());

        confirmationTokenService.sendEmail(mailMessage);
    }
}
