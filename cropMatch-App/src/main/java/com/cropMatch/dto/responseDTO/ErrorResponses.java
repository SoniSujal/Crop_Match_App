package com.cropMatch.dto.responseDTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponses {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private Map<String,String> errors;
}
