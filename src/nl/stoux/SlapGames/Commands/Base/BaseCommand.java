package nl.stoux.SlapGames.Commands.Base;

import lombok.AllArgsConstructor;
import nl.stoux.SlapGames.Commands.Exceptions.UsageException;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Util.PlayerUtil;
import nl.stoux.SlapGames.Util.PlayerUtil.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 05/02/2015.
 */
@AllArgsConstructor
public abstract class BaseCommand {

    /** The sender who executed this command */
    protected CommandSender sender;

    /** The actual command */
    protected Command command;

    /** The used alias */
    protected String usedAlias;

    /** The used arguments */
    protected String args[];

    /** Handle the command */
    public abstract void handle() throws BaseException;


    /*
     * Functions for the subclasses
     */

    protected void msg(String message) {

    }

    protected void hMsg(String message) {

    }

    protected void badMsg(String message) {
        PlayerUtil.badMsg(sender, message);
    }

    /**
     * Check if the correct number of arguments is given
     * @param neededArgs the number of needed arguments
     * @throws UsageException if incorrect number
     */
    protected void checkUsage(int neededArgs) throws UsageException {
        if (args.length != neededArgs) throw new UsageException();
    }

    /**
     * Check if the minimum number of arguments are given
     * @param minNeededArgs The number of arguments
     * @throws UsageException if not enough arguments
     */
    protected void checkMinUsage(int minNeededArgs) throws UsageException {
        if (args.length < minNeededArgs) throw new UsageException();
    }



}
