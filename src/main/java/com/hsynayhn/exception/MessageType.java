package com.hsynayhn.exception;

import lombok.Getter;

@Getter
public enum MessageType {

    NO_RECORD_EXIST("1001", "No record exists"),
    USERNAME_CANNOT_BE_EMPTY("1002", "Username cannot be empty"),
    FISRTNAME_CANNOT_BE_EMPTY("1003", "Firstname cannot be empty"),
    LASTNAME_CANNOT_BE_EMPTY("1004", "Lastname cannot be empty"),
    TCKN_CANNOT_BE_EMPTY("1005", "TCKN cannot be empty"),
    EMAIL_CANNOT_BE_EMPTY("1006", "Email cannot be empty"),
    PASSWORD_CANNOT_BE_EMPTY("1007", "Password cannot be empty"),
    TCKN_ALREADY_EXISTS("1008", "TCKN already exists"),
    EMAIL_ALREADY_EXISTS("1009", "Email already exists"),
    TCKN_11_DIGIT("1010", "TCKN must be exactly 11 digits and only contain numbers."),
    TOKEN_INVALID("1011", "Invalid token"),
    AUTHENTICATION_FAILED("1012", "Authentication failed" ),
    PASSWORD_INVALID("1013", "Invalid password"),
    PASSWORD_SAME_AS_OLD("1014", "New password cannot be the same as the old password"),
    TOKEN_CANNOT_BE_EMPTY("1015", "Token cannot be empty"),
    INVALID_EMAIL_FORMAT("2004", "Invalid email format");;



    private String code;
    private String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
