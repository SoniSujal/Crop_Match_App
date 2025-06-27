package com.cropMatch.exception;

import com.cropMatch.dto.responseDTO.ApiResponse;
import com.cropMatch.dto.responseDTO.ErrorResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponses> validationHandler(MethodArgumentNotValidException ev){

        Map<String,String> errors = new LinkedHashMap<>();
        ev.getBindingResult().getFieldErrors().forEach(error ->errors.put(error.getField(),error.getDefaultMessage()));

        ErrorResponses dto = new ErrorResponses();
        dto.setTimestamp(LocalDateTime.now());
        dto.setStatus(HttpStatus.BAD_REQUEST.value());
        dto.setError("Validation Failed!");
        dto.setMessage("Input Validation failed for one or more fields");
        dto.setErrors(errors);

        return new ResponseEntity<>(dto,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("Each image must be less than 1MB"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponses> handleAccessDenied(AccessDeniedException ed){
        return buildResponse(HttpStatus.FORBIDDEN,"Access Denied","You dont have Persmission to access this Page",null);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponses> handleOtherExceptions(Exception ex){
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Server Error","Something Went Wrong",null);
    }

    @ExceptionHandler(InvalidJWTException.class)
    public ResponseEntity<ErrorResponses> handleInvalidJwt(InvalidJWTException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED,"Invalid Token",ex.getMessage(),null);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponses> handleTokenExpired(TokenExpiredException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED,"Token Expired",ex.getMessage(),null);
    }

    //method to build ErrorResponse
    private ResponseEntity<ErrorResponses> buildResponse(HttpStatus status, String error, String message, Map<String, String> errors) {

        ErrorResponses response = new ErrorResponses(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                errors
        );
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(CropNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCropNotFound(CropNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "redirect", true,
                        "message", ex.getMessage()
                ));
    }
}