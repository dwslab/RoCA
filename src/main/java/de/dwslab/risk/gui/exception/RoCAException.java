package de.dwslab.risk.gui.exception;

public class RoCAException extends RuntimeException {

    /** */
    private static final long serialVersionUID = -6069816604710951328L;

    public RoCAException(String message) {
        super(message);
    }

    public RoCAException(String message, Throwable cause) {
        super(message, cause);
    }

}
