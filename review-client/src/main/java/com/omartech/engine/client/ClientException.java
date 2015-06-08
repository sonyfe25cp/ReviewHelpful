package com.omartech.engine.client;


public class ClientException extends Exception {
    public ClientException(String message) {
        super(message);
    }

    public ClientException(Throwable cause) {
        super(cause);
    }
}
