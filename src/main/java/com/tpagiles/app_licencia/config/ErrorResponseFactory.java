package com.tpagiles.app_licencia.config;

import com.tpagiles.app_licencia.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;

@Component
public class ErrorResponseFactory {
    public ErrorResponse build(HttpStatus status, String msg) {
        return new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                msg
        );
    }
}
