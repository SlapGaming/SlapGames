package nl.stoux.SlapGames.Commands.Exceptions;

import lombok.Getter;
import nl.stoux.SlapGames.Exceptions.BaseException;

/**
 * Created by Stoux on 05/02/2015.
 */
public class UsageException extends BaseException {

    public UsageException() {
        super("Incorrect usage!");
    }

    @Getter
    private BaseException wrappedException;

    public UsageException(BaseException wrappedException) {
        this.wrappedException = wrappedException;
    }

}
