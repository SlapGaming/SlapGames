package nl.stoux.SlapGames.Commands.Exceptions;

import nl.stoux.SlapGames.Exceptions.BaseException;

/**
 * Created by Stoux on 05/02/2015.
 */
public class CommonException extends BaseException {

    /** You don't have the permission to do this */
    public static CommonException NO_PERMISSION = new CommonException("You don't have permission to do this!");

    /** You need to be in-game to do this */
    public static CommonException PLAYERS_ONLY = new CommonException("You need to be in-game to do this!");


    private CommonException(String message) {
        super(message);
    }
}
