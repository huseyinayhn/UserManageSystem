package com.hsynayhn.service.impl;

import com.hsynayhn.dto.DtoUser;
import com.hsynayhn.dto.DtoUserChangePassword;
import com.hsynayhn.entity.User;
import com.hsynayhn.exception.BaseException;
import com.hsynayhn.exception.ErrorMessage;
import com.hsynayhn.exception.MessageType;
import com.hsynayhn.repository.UserRepository;
import com.hsynayhn.security.AuthRequest;
import com.hsynayhn.security.AuthResponse;
import com.hsynayhn.security.JwtService;
import com.hsynayhn.service.IAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final AuthenticationProvider authenticationProvider;

    private final JwtService jwtService;

    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider, JwtService jwtService, EmailService emailService, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public DtoUser register(AuthRequest authRequest) {
        validateAuthRequest(authRequest, true);

        if (userRepository.existsByTckn(authRequest.getTckn())) {
            throw new BaseException(new ErrorMessage(MessageType.TCKN_ALREADY_EXISTS, null));
        }

        if (userRepository.existsByEmail(authRequest.getEmail())) {
            throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, null));
        }

        User user = new User();
        BeanUtils.copyProperties(authRequest, user);
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        User savedUser = userRepository.save(user);

        DtoUser dtoUser = new DtoUser();
        BeanUtils.copyProperties(savedUser, dtoUser);
        return dtoUser;
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        validateAuthRequest(authRequest, false);

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
            authenticationProvider.authenticate(authenticationToken);

            Optional<User> optionalUser = userRepository.findByUsername(authRequest.getUsername());
            if (optionalUser.isEmpty()) {
                throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, null));
            }

            User user = optionalUser.get();
            return createAuthResponse(user);
        } catch (Exception e) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, null));
        }
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        validateAuthRequest(authRequest, false);

        Optional<User> optionalUser = userRepository.findByUsername(authRequest.getUsername());
        if (optionalUser.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, null));
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
            authenticationProvider.authenticate(authenticationToken);

            User user = optionalUser.get();
            return createAuthResponse(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, null));
        }
    }

    @Override
    public boolean changePassword(String username, DtoUserChangePassword dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, null)));

        validatePasswords(dto, user.getPassword());

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return true;
    }

    @Override
    public DtoUser updateUser(String token, User user) {
        if (token == null || token.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.TOKEN_CANNOT_BE_EMPTY, null));
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.getUsernameByToken(token);

        if (username == null || username.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.USERNAME_CANNOT_BE_EMPTY, null));
        }


        User dbUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, null)));

        dbUser = validateUpdate(dbUser, user);

        userRepository.save(dbUser);

        return mapUserToDto(dbUser);
    }

    @Override
    public User getUser(String param, String token) {

        if (isTokenBlacklisted(token)){
            throw new BaseException(new ErrorMessage(MessageType.TOKEN_INVALID, null));
        }

        if (param == null) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, null));
        }
        try {
            UUID userId = UUID.fromString(param);
            Optional<User> optionalUser = userRepository.findById(userId);
            return optionalUser.get();
        } catch (IllegalArgumentException e) {
            Optional<User> optionalUser = userRepository.findByUsername(param);
            return optionalUser.get();
        }
    }

    private void validateAuthRequest(AuthRequest authRequest, boolean isRegistration) {
        if (authRequest.getUsername() == null || authRequest.getUsername().isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.USERNAME_CANNOT_BE_EMPTY, null));
        }

        if (authRequest.getPassword() == null || authRequest.getPassword().isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PASSWORD_CANNOT_BE_EMPTY, null));
        }

        if (isRegistration) {
            if (authRequest.getFirstName() == null || authRequest.getFirstName().isEmpty()) {
                throw new BaseException(new ErrorMessage(MessageType.FISRTNAME_CANNOT_BE_EMPTY, null));
            }

            if (authRequest.getLastName() == null || authRequest.getLastName().isEmpty()) {
                throw new BaseException(new ErrorMessage(MessageType.LASTNAME_CANNOT_BE_EMPTY, null));
            }

            if (authRequest.getTckn() == null || authRequest.getTckn().isEmpty() || !authRequest.getTckn().matches("\\d{11}")) {
                throw new BaseException(new ErrorMessage(MessageType.TCKN_11_DIGIT, null));
            }

            if (authRequest.getEmail() == null || authRequest.getEmail().isEmpty()) {
                throw new BaseException(new ErrorMessage(MessageType.EMAIL_CANNOT_BE_EMPTY, null));
            }
        }
    }

    private void validatePasswords(DtoUserChangePassword dto, String currentPassword) {
        if (!passwordEncoder.matches(dto.getOldPassword(), currentPassword)) {
            throw new BaseException(new ErrorMessage(MessageType.PASSWORD_INVALID, null));
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new BaseException(new ErrorMessage(MessageType.PASSWORD_CANNOT_BE_EMPTY, null));
        }

        if (dto.getNewPassword().equals(dto.getOldPassword())) {
            throw new BaseException(new ErrorMessage(MessageType.PASSWORD_SAME_AS_OLD, null));

        }
    }

    private User validateUpdate(User currentUser, User user) {
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            currentUser.setUsername(user.getUsername());
        }
        if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
            currentUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            currentUser.setLastName(user.getLastName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            if (!isValidEmail(user.getEmail())) {
                throw new BaseException(new ErrorMessage(MessageType.INVALID_EMAIL_FORMAT, null));
            }
            currentUser.setEmail(user.getEmail());
        }
        return currentUser;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private AuthResponse createAuthResponse(User user) {
        AuthResponse authResponse = new AuthResponse();

        authResponse.setUsername(user.getUsername());
        authResponse.setFirstName(user.getFirstName());
        authResponse.setLastName(user.getLastName());
        authResponse.setEmail(user.getEmail());

        String token = jwtService.generateToken(user);
        authResponse.setToken(token);

        return authResponse;
    }

    private DtoUser mapUserToDto(User user) {
        DtoUser dtoUser = new DtoUser();
        dtoUser.setId(user.getId());
        dtoUser.setUsername(user.getUsername());
        dtoUser.setFirstName(user.getFirstName());
        dtoUser.setLastName(user.getLastName());
        dtoUser.setEmail(user.getEmail());
        return dtoUser;
    }

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Override
    public void addToBlacklist(String token) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "true");
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }

}
