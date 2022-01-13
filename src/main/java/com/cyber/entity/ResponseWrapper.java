package com.cyber.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ResponseWrapper {

    private boolean success;
    private String message;
    private Integer code;
    private Object data;

    public ResponseWrapper(String message, Object data) {
        this.message = message;
        this.data = data;
        this.code= HttpStatus.OK.value();
        this.success=true;
    }

    public ResponseWrapper(String message) {
        this.message = message;
        this.code=HttpStatus.OK.value();
        this.success=true;
    }
}
