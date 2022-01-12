package com.cyber.converter;

import com.cyber.dto.RoleDTO;
import com.cyber.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding // this makes conversion automatically - no need conversion ,method,
public class RoleDtoConverter implements Converter<String,RoleDTO> { // convert from String to RoleDTO

    @Autowired
    RoleService roleService;

    @Override
    public RoleDTO convert(String source) {
        Long id = Long.parseLong(source);
        return roleService.findById(id);
    }
}
