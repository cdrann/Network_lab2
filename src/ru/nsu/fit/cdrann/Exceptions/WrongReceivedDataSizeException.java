package ru.nsu.fit.cdrann.Exceptions;

public class WrongReceivedDataSizeException extends Exception {
    public WrongReceivedDataSizeException(String message) {
        super(message);
    }
}