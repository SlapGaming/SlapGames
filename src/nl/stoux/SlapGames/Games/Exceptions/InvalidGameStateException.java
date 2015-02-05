package nl.stoux.SlapGames.Games.Exceptions;

import nl.stoux.SlapGames.Exceptions.BaseException;

/**
 * Created by Stoux on 22/01/2015.
 */
public class InvalidGameStateException extends BaseException {

    public InvalidGameStateException() {
        super("The game is not in the correct state to do this.");
    }
}
