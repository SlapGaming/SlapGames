package nl.stoux.SlapGames.Commands.Exceptions;

import nl.stoux.SlapGames.Exceptions.BaseException;

/**
 * Created by Stoux on 05/02/2015.
 */
public class UsageException extends BaseException {

    public UsageException() {
        super("Incorrect usage!");
    }
}
