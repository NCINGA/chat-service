package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.document.User;
import com.ncinga.chatservice.dto.AuthenticateDto;
import com.ncinga.chatservice.dto.SuccessAuthenticateDto;
import com.ncinga.chatservice.repository.RoleRepository;
import com.ncinga.chatservice.repository.UserRepository;
import com.ncinga.chatservice.service.JwtService;
import com.ncinga.chatservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder;

    @Override
    public SuccessAuthenticateDto login(AuthenticateDto authenticateDto) throws Exception {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    authenticateDto.getUsername(),
                    authenticateDto.getPassword()
            );

            Authentication authenticate = this.authenticationManager.authenticate(authenticationToken);

            if (authenticate.isAuthenticated()) {
                log.info("SUCCESS!!");
            } else {
                log.info("FAILED");
            }

            return new SuccessAuthenticateDto(null, null);

        } catch (Exception ex) {
            log.error("{}", ex.getMessage());
            throw new Exception(ex);
        }
    }

    @Override
    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

//    @Override
//    public boolean isAdmin(AuthenticateDto authenticateDto) {
//        if (authenticateDto == null || authenticateDto.getRoles() == null) {
//            return false;
//        }
//        return authenticateDto.getRoles().stream()
//                .anyMatch(role -> "admin".equalsIgnoreCase(role));
//    }
}
