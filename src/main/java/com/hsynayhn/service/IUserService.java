package com.hsynayhn.service;

import com.hsynayhn.dto.DtoUser;
import com.hsynayhn.dto.DtoUserChangeUsername;
import com.hsynayhn.entity.User;
import com.hsynayhn.dto.DtoUserChangePassword;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {
    public List<DtoUser> getAllUser();
    public DtoUser getUserById(Long id);
}
