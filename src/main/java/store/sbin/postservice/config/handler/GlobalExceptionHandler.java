package store.sbin.postservice.config.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import store.sbin.postservice.config.exception.ApiErrRes;
import store.sbin.postservice.config.exception.FileStorageException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrRes> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiErrRes res = ApiErrRes.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiErrRes> handleFileStorageException(FileStorageException e) {
        ApiErrRes res = ApiErrRes.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

}
