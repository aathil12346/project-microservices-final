package com.bankingapp.transaction_services.security;

import com.bankingapp.transaction_services.dto.UserSecurityInfo;
import com.bankingapp.transaction_services.service.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ApiClient apiClient;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserSecurityInfo userSecurityInfo = apiClient.getUser(username);

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userSecurityInfo.getRole());

        return new org.springframework.security.core.userdetails.User(userSecurityInfo.getEmail(),
                userSecurityInfo.getPassword(), Collections.singleton(grantedAuthority));
    }
}
