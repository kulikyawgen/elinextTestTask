package by.kulik.exceptions;

public class ConstructorNotFoundException extends RuntimeException{
    public ConstructorNotFoundException(String message) {
        super(message);
    }
}
