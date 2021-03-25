/**
 * Copyright 2020, Eötvös Loránd University.
 * All rights reserved.
 */
package p4query.broker;

public class LocalGremlinServerException extends Exception {
    private static final long serialVersionUID = 1L;

    public LocalGremlinServerException(String message) {
        super(message);
    }

    public LocalGremlinServerException(Throwable cause) {
        super(cause);
    }
    
}
