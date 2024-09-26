package com.amimul.book.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    //Which will be our jwt token
    private String token;
}
