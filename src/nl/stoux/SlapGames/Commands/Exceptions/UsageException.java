package nl.stoux.SlapGames.Commands.Exceptions;

import lombok.Getter;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapPlayers.Util.SUtil;
import org.bukkit.ChatColor;

/**
 * Created by Stoux on 05/02/2015.
 */
public class UsageException extends BaseException {

    public UsageException(String... messages) {
        super(toString(messages));
    }

    private static String toString(String... messages) {
        if (messages.length == 0) {
            return "Incorrect usage!";
        } else {
            return SUtil.combineToString(messages, "\n", s -> ChatColor.RED + s);
        }
    }


    @Getter
    private BaseException wrappedException;

    public UsageException(BaseException wrappedException) {
        this.wrappedException = wrappedException;
    }

}
