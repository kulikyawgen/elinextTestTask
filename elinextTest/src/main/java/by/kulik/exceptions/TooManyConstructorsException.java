package by.kulik.exceptions;

public class TooManyConstructorsException extends RuntimeException{
    public TooManyConstructorsException(String message) {
        super(message);
    }
}
