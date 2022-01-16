package com.cyber.service;

import com.cyber.dto.RoleDTO;
import com.cyber.exception.TicketNGProjectException;

import java.util.List;

public interface RoleService {

    List<RoleDTO> listAllRoles();
    RoleDTO findById(Long id) throws TicketNGProjectException;
}
