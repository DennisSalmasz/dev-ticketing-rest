package com.cyber.implementation;

import com.cyber.entity.User;
import com.cyber.entity.common.UserPrincipal;
import com.cyber.repository.UserRepository;
import com.cyber.service.SecurityService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    private UserRepository userRepository;

    public SecurityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(username);
        //if no such user in db, this will prevent login page crash!!
        if(user == null) {
            throw new UsernameNotFoundException("This username does not exist !!");
        }
        return new UserPrincipal(user);
    }
}
