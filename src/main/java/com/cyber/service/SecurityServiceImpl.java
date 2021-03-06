package com.cyber.service;

import com.cyber.dto.UserDTO;
import com.cyber.entity.User;
import com.cyber.util.MapperUtil;
import com.cyber.service.SecurityService;
import com.cyber.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService {

    private UserService userService;
    private MapperUtil mapperUtil;

    public SecurityServiceImpl(UserService userService, MapperUtil mapperUtil) {
        this.userService = userService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDTO userDTO = null;
        try {
            userDTO = userService.findByUserName(username);
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        }

        //if no such user in db, this will prevent login page crash!!
        if(userDTO == null) {
            throw new UsernameNotFoundException("This username does not exist !!");
        }
        //return User to Spring in its structure [username, password, authorities]
        return new org.springframework.security.core.userdetails.User(userDTO.getId().toString(),
                userDTO.getPassWord(), listAuthorities(userDTO));
    }

    @Override
    public User loadUser(String param) throws AccessDeniedException {
        UserDTO user = userService.findByUserName(param);
        return mapperUtil.convert(user,new User());
    }

    private Collection<? extends GrantedAuthority> listAuthorities(UserDTO userDTO){
        List<GrantedAuthority> authorityList = new ArrayList<>();

        GrantedAuthority authority = new SimpleGrantedAuthority(userDTO.getRole().getDescription());
        authorityList.add(authority);
        return authorityList;
    }
}
