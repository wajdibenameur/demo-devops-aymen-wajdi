package com.iteam.dto;

import lombok.*;

import java.time.LocalDateTime;
@Data
@ToString
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;


}
