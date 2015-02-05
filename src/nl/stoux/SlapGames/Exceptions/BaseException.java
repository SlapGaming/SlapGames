package nl.stoux.SlapGames.Exceptions;

/**
 * Created by Stoux on 22/01/2015.
 */
public class BaseException extends Exception {

    public BaseException() {
        super("Something went wrong!");
    }

    public BaseException(String message) {
        super(message);
    }
}
