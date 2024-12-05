package com.hsynayhn.controller.impl;

import com.hsynayhn.controller.IRestUserController;
import com.hsynayhn.dto.DtoUser;
import com.hsynayhn.entity.User;
import com.hsynayhn.service.IUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class RestUserControllerImpl implements IRestUserController {

    private final IUserService userService;

    public RestUserControllerImpl(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    @Override
    public List<DtoUser> getAllUser() {
        System.out.println("getAllUser çalışıyor");
        return userService.getAllUser();
    }

    @GetMapping("/list/{id}")
    @Override
    public DtoUser getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
