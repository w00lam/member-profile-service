package com.woolam.memberprofileservice.common.exception;

import com.woolam.memberprofileservice.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error(errorCode.getMessage(), e);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.failure(errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        log.error("요청 값 검증 실패 : {}", errors);

        return ResponseEntity.badRequest().body(ApiResponse.failure("입력값이 올바르지 않습니다.", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("예기치 못한 서버 오류가 발생했습니다.", e);

        return ResponseEntity.internalServerError().body(ApiResponse.failure("예기치 못한 서버 오류가 발생했습니다."));
    }
}
