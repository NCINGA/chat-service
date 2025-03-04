package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.document.Token;
import com.ncinga.chatservice.document.User;
import com.ncinga.chatservice.dto.AuthenticateDto;
import com.ncinga.chatservice.dto.MongoUserDto;
import com.ncinga.chatservice.dto.SuccessAuthenticateDto;
import com.ncinga.chatservice.exception.UserNotFoundException;
import com.ncinga.chatservice.repository.RoleRepository;
import com.ncinga.chatservice.repository.TokenRepository;
import com.ncinga.chatservice.repository.UserRepository;
import com.ncinga.chatservice.service.JwtService;
import com.ncinga.chatservice.service.UserService;
import com.ncinga.chatservice.utilities.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder;

    private final TokenRepository tokenRepository;


//    private final MyUserDetailsService myUserDetailsService;

    @Override
    public SuccessAuthenticateDto login(AuthenticateDto authenticateDto) throws Exception {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    authenticateDto.getUsername(),
                    authenticateDto.getPassword()
            );

            Authentication authenticate = this.authenticationManager.authenticate(authenticationToken);

            if (!authenticate.isAuthenticated()) {
                throw new Exception("Invalid username or password");
            }

            log.info("SUCCESS!!");

            // Generate tokens
            Map<String, Object> extraClaims = new HashMap<>();
            List<String> authorities = authenticate.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            extraClaims.put("roles", authorities);

            String accessToken = this.jwtService.generateToken(authenticate, extraClaims);
            String refreshToken = this.jwtService.generateRefreshToken(authenticate);

            this.saveRefreshToken(refreshToken);

//            Optional<User> byUsername = userRepository.findByUsername(authenticateDto.getUsername());
//            log.info("{}", byUsername);
//            boolean hasMatched = encoder.matches(authenticateDto.getPassword(), byUsername.get().getPassword());
//            log.info("{}", hasMatched);

            return new SuccessAuthenticateDto(accessToken, refreshToken);

        } catch (BadCredentialsException ex){
            log.error("Invalid username or password");
            throw new Exception("Invalid username or password");
        } catch (Exception ex) {
            log.error("{}", ex.getMessage());
            throw new Exception(ex.getMessage());
        }
        /*
        Map<String, Object> extraClaims = new HashMap<>();
        List<String> authorities = authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        extraClaims.put("roles", authorities);

        String accessToken = this.jwtService.generateToken(authenticate, extraClaims);
        String refreshToken = this.jwtService.generateRefreshToken(authenticate);

         */
    }

    private void saveRefreshToken(String refreshToken) {

        String jti = jwtService.extractJti(refreshToken);
        Date expireDate = jwtService.extractExpiration(refreshToken);

        Token token = Token.builder()
                .jti(jti)
                .expireDate(expireDate)
                .build();

        tokenRepository.save(token);

    }

    @Override
    public Mono<Boolean> doesUserExist(String username) {

        return Mono.fromSupplier(() -> userRepository.findByUsername(username).isPresent());

    }

    @Override
    public Mono<Map<String, Object>> register(MongoUserDto user) {
        Map<String, Object> result = new HashMap<>();

        return doesUserExist(user.getUsername()).flatMap(isFound -> {
            if (!isFound) {
                user.setPassword(encoder.encode(user.getPassword()));
                User userEntity = user.toEntity(User.class);

                return Mono.fromSupplier(() -> userRepository.save(userEntity))
                        .map(savedUser -> {
                            result.put("status", ResponseCode.USER_CREATE_SUCCESS);
                            result.put("responseCode", ResponseCode.USER_CREATE_SUCCESS.ordinal());
                            return result;
                        });
            } else {
                // Instead of throwing an error, return a meaningful response
                result.put("status", ResponseCode.USER_ALREADY_EXISTS);
                result.put("responseCode", ResponseCode.USER_ALREADY_EXISTS.ordinal());
                return Mono.just(result);
            }
        }).onErrorResume(e -> {
            // Handle unexpected errors
            result.put("status", ResponseCode.USER_CREATE_FAILED);
            result.put("responseCode", ResponseCode.USER_CREATE_FAILED.ordinal());
            return Mono.just(result);
        });
    }


    @Override
    public Object findByRole(String email, String password, String userRole) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return "Incorrect credentials";
                    }
                    if (!user.getRole().equalsIgnoreCase(userRole)) {
                        return "Incorrect credentials";
                    }
                    return user;
                })
                .orElse("Incorrect credentials");
    }

    @Override
    public Mono<Map<String, Object>> updateUser(String id, MongoUserDto user) {
        Map<String, Object> result = new HashMap<>();

        return Mono.justOrEmpty(userRepository.findById(id))
                .flatMap(existingUser -> {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setPassword(encoder.encode(user.getPassword()));
                    existingUser.setRole(user.getRole());

                    return Mono.fromSupplier(() -> userRepository.save(existingUser))
                            .map(savedUser -> {
                                result.put("status", ResponseCode.USER_UPDATE_SUCCESS);
                                result.put("responseCode", ResponseCode.USER_UPDATE_SUCCESS.ordinal());
                                return (result);
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    result.put("status", ResponseCode.USER_NOT_FOUND);
                    result.put("responseCode", ResponseCode.USER_NOT_FOUND.ordinal());
                    return Mono.just(result);
                }))
                .onErrorResume(e -> {
                    result.put("status", ResponseCode.USER_UPDATE_FAILED);
                    result.put("responseCode", ResponseCode.USER_UPDATE_FAILED.ordinal());
                    return Mono.just(result);
                });
    }


    @Override
    public String deleteMongoUser(String id) {

        try {
            if (!userRepository.findById(id).isPresent()) {
                throw new UserNotFoundException(ResponseCode.USER_NOT_FOUND);
            }

            userRepository.deleteById(id);

            // Verify if the user is successfully deleted
            if (userRepository.findById(id).isPresent()) {
                throw new RuntimeException(ResponseCode.USER_DELETE_FAILED.getMessage());
            }

            return ResponseCode.USER_DELETE_SUCCESS.getMessage();
        }catch (UserNotFoundException e) {

            return e.getMessage();

        } catch (Exception e) {

            return "An unexpected error occurred" + e.getMessage();
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public void logout(String token) throws Exception {
        String jti = this.jwtService.extractJti(token);
        Optional<Token> optionalToken = tokenRepository.findByJti(jti);

        if (optionalToken.isPresent()) {
            Token storedToken = optionalToken.get();
            storedToken.revoke();
            tokenRepository.save(storedToken);
        } else {
            throw new Exception("Invalid token or token already revoked.");
        }
    }

}

