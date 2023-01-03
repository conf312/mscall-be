package com.admin.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class RestMessage {

    private HttpStatus status;
    private List<String> errors;
    private Object data;

    public RestMessage(HttpStatus status, Object data) {
        this.status = status;
        this.data = data;
    }

    public RestMessage(HttpStatus status, Object data, String error) {
        this.status = status;
        this.data = data;
        this.errors = Arrays.asList(error);
    }
}
