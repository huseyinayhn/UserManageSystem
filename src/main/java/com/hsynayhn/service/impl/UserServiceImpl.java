package com.hsynayhn.service.impl;

import com.hsynayhn.dto.DtoUser;
import com.hsynayhn.dto.DtoUserChangePassword;
import com.hsynayhn.dto.DtoUserChangeUsername;
import com.hsynayhn.entity.User;
import com.hsynayhn.repository.UserRepository;
import com.hsynayhn.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<DtoUser> getAllUser() {

        List<DtoUser> dtoList = new ArrayList<>();

        List<User> users = userRepository.findAll();
        for (User user : users) {
            DtoUser dto = new DtoUser();
            BeanUtils.copyProperties(user, dto);
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public DtoUser getUserById(Long id) {

        DtoUser dtoUser = new DtoUser();

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return null;
        }

        BeanUtils.copyProperties(user.get(), dtoUser);

        return dtoUser;
    }
}
