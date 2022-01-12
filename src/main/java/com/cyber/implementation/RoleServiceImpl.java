package com.cyber.implementation;

import com.cyber.dto.RoleDTO;
import com.cyber.entity.Role;
import com.cyber.mapper.MapperUtil;
import com.cyber.repository.RoleRepository;
import com.cyber.service.RoleService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;
    private MapperUtil mapperUtil;

    public RoleServiceImpl(@Lazy RoleRepository roleRepository, MapperUtil mapperUtil) {
        this.roleRepository = roleRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<RoleDTO> listAllRoles() {

        List<Role> list = roleRepository.findAll();
        return list.stream().map(obj -> {return mapperUtil.convert(obj,new RoleDTO());}).collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(Long id) {
        Role role = roleRepository.findById(id).get();
        return mapperUtil.convert(role,new RoleDTO());
    }
}
