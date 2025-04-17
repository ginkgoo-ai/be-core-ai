package com.ginkgooai.core.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalArgumentException(IllegalStateException ex) {
        log.error("IllegalArgumentException: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.getMessage());
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("https://api.ginkgoocoreidentity.com/errors/" + "bad_request"));

        if (Objects.requireNonNull(problemDetail.getDetail()).contains("Address is required when radius is provided")){
            return "Address is required when radius is provided, please tell me your address";
        }
        return "it seems a little problem, please try again later";
    }
}
