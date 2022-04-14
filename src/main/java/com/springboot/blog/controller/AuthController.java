package com.springboot.blog.controller;

import com.springboot.blog.model.Role;
import com.springboot.blog.model.User;
import com.springboot.blog.payload.JwtAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.SignUpDto;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    private ResponseEntity<JwtAuthResponse> authenticateUser(@RequestBody LoginDto loginDto){

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(),loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //GET TOKEN FROM TOKEN PROVIDER
        String token = tokenProvider.generateToken(authentication);
        System.out.println("TOKEN"+token);

        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    @PostMapping("/signup")
    private ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){
        //check if username exists in db
        if(userRepository.existsByUsername(signUpDto.getUsername())){
            return new ResponseEntity<>("Username already exists",HttpStatus.BAD_REQUEST);
        }

        //check if email already exists in db
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>("Email already registered",HttpStatus.BAD_REQUEST);
        }

        //create user object
        User user = new User();
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        Role roles = roleRepository.findByName("ROLE_ADMIN").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User successfully registered",HttpStatus.OK);
    }
}
