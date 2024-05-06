package store.sbin.postservice.config.exception;

public class FileStorageException extends RuntimeException{
    public FileStorageException(String msg) {
        super(msg);
    }

    public FileStorageException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public FileStorageException(String msg, Object... args) {
        super(String.format(msg, args));
    }
}
