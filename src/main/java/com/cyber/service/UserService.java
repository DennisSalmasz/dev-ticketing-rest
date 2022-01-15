package com.cyber.service;

import com.cyber.dto.UserDTO;
import com.cyber.entity.User;
import com.cyber.exception.TicketNGProjectException;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface UserService {

    List<UserDTO> listAllUsers();
    UserDTO findByUserName(String username) throws AccessDeniedException;
    UserDTO save(UserDTO dto) throws TicketNGProjectException;
    UserDTO update(UserDTO dto);
    void delete(String username) throws TicketNGProjectException;
    void deleteByUserName(String username);
    List<UserDTO> listAllByRole(String role);
    Boolean checkIfUserCanBeDeleted(User user);
    UserDTO confirm(User user);
}
