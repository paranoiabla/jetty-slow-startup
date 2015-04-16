/*
 * nemesis Platform - NExt-generation Multichannel E-commerce SYStem
 *
 * Copyright (c) 2010 - 2013 nemesis
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of nemesis
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with nemesis.
 */
package com.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;

import javax.annotation.Resource;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "com.nemesis.console.backend", "com.nemesis.platform.util" })
@EnableGlobalMethodSecurity(prePostEnabled = true, mode = AdviceMode.PROXY)
public class BackendConsoleConfig extends WebSecurityConfigurerAdapter {

    @Resource(name = "defaultAuthenticationFailureHandler")
    private AuthenticationFailureHandler defaultAuthenticationFailureHandler;

    @Resource(name = "defaultAccessDeniedHandler")
    private AccessDeniedHandler defaultAccessDeniedHandler;

    @Autowired
    private ObjectPostProcessor<Object> opp;

    // @formatter:off
    
	@Bean(name = { "defaultAuthenticationFailureHandler", "authenticationFailureHandler" })
    protected AuthenticationFailureHandler defaultAuthenticationFailureHandler() {
        Map<String, String> exceptionMappings = new HashMap<>();
        exceptionMappings.put(InternalAuthenticationServiceException.class.getCanonicalName(), "/login?error=servererror");
        exceptionMappings.put(BadCredentialsException.class.getCanonicalName(), "/login?error=authfailed");
        exceptionMappings.put(CredentialsExpiredException.class.getCanonicalName(), "/login?error=credentialsExpired");
        exceptionMappings.put(LockedException.class.getCanonicalName(), "/login?error=locked");
        exceptionMappings.put(DisabledException.class.getCanonicalName(), "/login?error=disabled");
        exceptionMappings.put(AccessDeniedException.class.getCanonicalName(), "/login?error=denied");

        final ExceptionMappingAuthenticationFailureHandler result = new ExceptionMappingAuthenticationFailureHandler();
        result.setExceptionMappings(exceptionMappings);
        result.setDefaultFailureUrl("/login?error=default");
        return result;
    }

        @Bean(name = { "defaultAccessDeniedHandler", "accessDeniedHandler" })
    protected AccessDeniedHandler defaultAccessDeniedHandler() {
        final AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
        accessDeniedHandler.setErrorPage("/login?error=denied");

        return accessDeniedHandler;
    }


    @Bean(name = { "defaultRestAuthenticationProvider", "restAuthenticationProvider" })
    public AuthenticationProvider defaultRestAuthenticationProvider() throws NamingException {
        return null;
    }

    @Bean(name = { "defaultAuthenticationManager", "authenticationManager" })
    public AuthenticationManager defaultAuthenticationManager() throws Exception {
        return new AuthenticationManagerBuilder(opp).authenticationProvider(defaultRestAuthenticationProvider()).build();
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/*");
    }
    
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/resources/img/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/**").hasRole("EMPLOYEEGROUP")
                .and()
            .formLogin()
                .loginProcessingUrl("/j_spring_security_check")
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/console")
                .failureHandler(defaultAuthenticationFailureHandler)
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/j_spring_security_logout")
                .logoutSuccessUrl("/login")
                .permitAll()
                .and()
            .exceptionHandling()
                .accessDeniedHandler(defaultAccessDeniedHandler);
    }
    
    // @formatter:on
}
