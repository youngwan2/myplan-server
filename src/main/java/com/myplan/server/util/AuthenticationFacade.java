package com.myplan.server.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {

    public String getCurrentUsername(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(username == null || username.isBlank()){
            throw new UsernameNotFoundException("인증된 유저이름을 찾을 수 없습니다.");
        }
        return username;
    }


}
