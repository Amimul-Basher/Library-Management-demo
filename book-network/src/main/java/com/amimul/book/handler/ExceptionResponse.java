package com.amimul.book.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//This annotation says to include only non empty attributes
@JsonInclude(JsonInclude.Include.NON_EMPTY)
//It's Wrapper class of the exceptions
public class ExceptionResponse {
    private Integer businessErrorCode;
    private String businessErrorDescription;
    private String errorMessage;
    private Set<String> validationErrors;
    private Map<String, String> errors;
}
