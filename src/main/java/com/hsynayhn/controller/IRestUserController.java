package com.hsynayhn.controller;

import com.hsynayhn.dto.DtoUser;
import com.hsynayhn.dto.DtoUserChangeUsername;
import com.hsynayhn.entity.User;
import com.hsynayhn.dto.DtoUserChangePassword;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface IRestUserController {
    public List<DtoUser> getAllUser();
    public DtoUser getUserById(Long id);
}
