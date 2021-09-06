package de.htwsaar.vs.rmiMessengerShared;

public class ServerInterfaceException extends Exception {
    public ServerInterfaceException() {
    }

    public ServerInterfaceException(String message) {
        super(message);
    }

    public ServerInterfaceException(Throwable throwable) {
        super(throwable);
    }

    public ServerInterfaceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
