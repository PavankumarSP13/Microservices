package com.pavan.auth_service.service;

import com.pavan.auth_service.model.UserCredentials;
import com.pavan.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class customUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserCredentials userCredentials = userRepository.findByUsername(username);
        if(userCredentials ==null){
            throw new UsernameNotFoundException("User not found with name: "+userCredentials.getUsername());
        }
        return new UserPrincipal(userCredentials);
    }

    public String saveUser(UserCredentials userCredentials) {
        userCredentials.setPassword(bCryptPasswordEncoder.encode(userCredentials.getPassword()));
        System.out.println("Password: "+ userCredentials.getPassword());
        userRepository.save(userCredentials);
        return "User added successfully";
    }
}
