package com.amimul.book.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BusinessErrorCode {

    NO_CODE(0, "No Code", NOT_IMPLEMENTED),
    INCORRECT_CURRENT_PASSWORD(300, "Incorrect Current Password", BAD_REQUEST),
    NEW_PASSWORD_DOES_NOT_MATCH(301, "New Password Does Not Match", BAD_REQUEST),
    ACCOUNT_DISABLED(302, "Account Disabled", FORBIDDEN),
    ACCOUNT_LOCKED(303, "Account Locked", FORBIDDEN),
    BAD_CREDENTIALS(304, "Bad Credentials", UNAUTHORIZED)
    ;
    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCode(int code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
