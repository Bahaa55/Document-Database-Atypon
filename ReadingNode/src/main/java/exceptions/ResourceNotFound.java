package exceptions;



//@ResponseStatus(value= HttpStatus.NOT_FOUND, reason = "No such Document")
public class ResourceNotFound extends RuntimeException {
    public ResourceNotFound(String message) {
        super(message);
    }
}
