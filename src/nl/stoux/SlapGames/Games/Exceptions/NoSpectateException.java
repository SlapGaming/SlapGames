package nl.stoux.SlapGames.Games.Exceptions;

import nl.stoux.SlapGames.Exceptions.BaseException;

/**
 * Created by Stoux on 23/01/2015.
 */
public class NoSpectateException extends BaseException {

    public NoSpectateException() {
        super("This game doesn't support spectating!");
    }
}
