package com.certificaciones.backend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private List<String> errors;
}
