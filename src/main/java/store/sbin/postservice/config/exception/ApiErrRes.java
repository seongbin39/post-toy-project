package store.sbin.postservice.config.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiErrRes {
    String message;
    String error;
    String path;
    int status;

}
