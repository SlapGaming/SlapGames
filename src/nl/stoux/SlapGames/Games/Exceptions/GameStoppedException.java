package nl.stoux.SlapGames.Games.Exceptions;

import nl.stoux.SlapGames.Exceptions.BaseException;

/**
 * Created by Stoux on 22/01/2015.
 */
public class GameStoppedException extends BaseException {

    public GameStoppedException() {
        super("The game is currently stopped! Try again later.");
    }
}
