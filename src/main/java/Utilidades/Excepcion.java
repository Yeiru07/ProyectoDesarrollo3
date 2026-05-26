package Utilidades;


public class Excepcion extends Exception{

    public Excepcion() {
    }

    public Excepcion(String message) {
        super(message);
    }

    public Excepcion(String message, Throwable cause) {
        super(message, cause);
    }

    public Excepcion(Throwable cause) {
        super(cause);
    }

    public Excepcion(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
