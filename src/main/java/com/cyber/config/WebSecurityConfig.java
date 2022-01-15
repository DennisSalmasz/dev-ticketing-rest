package com.cyber.config;

import com.cyber.filter.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //we will use [@perAuthorize] to give authorization to certain roles in Controller
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityFilter securityFilter;

    //this class is for authentication - this is to enable security

    @Override
    @Bean // this bean is for API authentication - no form !!
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //public endpoints
    private static final String[] permittedUrls = {
            "/authenticate",
            "/confirmation",
            "/api/p1**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**"
    };

    //to avoid cyberattack, disable csrf - so that angular & react work on different ports
    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(permittedUrls) //rest endpoints are authenticated
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); //stateless - do not keep any cookies !!

        //this line runs SecurityFilter before each API call
        http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
