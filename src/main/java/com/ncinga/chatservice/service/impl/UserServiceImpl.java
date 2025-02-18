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


//    private final MyUserDetailsService myUserDetailsService;

    @Override
    public SuccessAuthenticateDto login(AuthenticateDto authenticateDto) throws Exception {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    authenticateDto.getUsername(),
                    authenticateDto.getPassword()
            );

            Authentication authenticate = this.authenticationManager.authenticate(authenticationToken);

            if (authenticate.isAuthenticated()) {
                log.info("SUCCEWSS!!");
            } else {
                log.info("FAILD");
            }

//            Optional<User> byUsername = userRepository.findByUsername(authenticateDto.getUsername());
//            log.info("{}", byUsername);
//            boolean hasMatched = encoder.matches(authenticateDto.getPassword(), byUsername.get().getPassword());
//            log.info("{}", hasMatched);

            return new SuccessAuthenticateDto(null, null);

        } catch (Exception ex) {
            log.error("{}", ex.getMessage());
            throw new Exception(ex);
        }
        /*
        Map<String, Object> extraClaims = new HashMap<>();
        List<String> authorities = authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        extraClaims.put("roles", authorities);

        String accessToken = this.jwtService.generateToken(authenticate, extraClaims);
        String refreshToken = this.jwtService.generateRefreshToken(authenticate);

         */
    }

    @Override
    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public boolean findByRole(String email, String password, String userRole) {
        return userRepository.getUserByEmailAndPasswordAndRole(email , userRole)
                .map(user -> encoder.matches(password, user.getPassword()))
                .orElse(false);
    }
}
