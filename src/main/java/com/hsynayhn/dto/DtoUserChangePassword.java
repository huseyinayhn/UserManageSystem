package com.hsynayhn.dto;

import lombok.Data;

@Data
public class DtoUserChangePassword {

    private String oldPassword;
    private String newPassword;

}
