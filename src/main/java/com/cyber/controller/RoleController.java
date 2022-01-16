package com.cyber.controller;

import com.cyber.annotation.DefaultExceptionMessage;
import com.cyber.dto.RoleDTO;
import com.cyber.dto.TaskDTO;
import com.cyber.entity.ResponseWrapper;
import com.cyber.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
@Tag(name = "Role Controller",description = "Role API")
public class RoleController {

    @Autowired
    private RoleService roleService;

    //retrieve roles
    @GetMapping
    @Operation(summary = "Retrieve all roles")
    @DefaultExceptionMessage(defaultMessage = "Sth went wrong while retrieving roles !!!")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> readAll()  {
        List<RoleDTO> listRoleDTO = roleService.listAllRoles();
        return ResponseEntity.ok(new ResponseWrapper("Roles are retrieved successfully",listRoleDTO));
    }

}
