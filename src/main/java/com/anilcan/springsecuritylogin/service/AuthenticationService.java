package com.anilcan.springsecuritylogin.service;

import com.anilcan.springsecuritylogin.entity.User;
import com.anilcan.springsecuritylogin.repository.UserRepository;
import com.anilcan.springsecuritylogin.request.AuthenticationRequest;
import com.anilcan.springsecuritylogin.request.RegisterRequest;
import com.anilcan.springsecuritylogin.response.AuthenticationResponse;
import com.anilcan.springsecuritylogin.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                       .userName(registerRequest.userName())
                       .password(passwordEncoder.encode(registerRequest.password()))
                       .role(Role.USER)
                       .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.userName(),
                                                                                   authenticationRequest.password()));
        var user = userRepository.getUserByUserName(authenticationRequest.userName()).orElseThrow();//CUSTOMIZED USER NOT FOUND
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

}
