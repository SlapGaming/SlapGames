package nl.stoux.SlapGames.Util;

import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Usage;
import nl.stoux.SlapGames.Commands.Model.ArgumentType;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapPlayers.Util.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Stoux on 12/02/2015.
 */
public class CommandUtil {

    /**
     * Parse a String into an int
     * @param arg The argument
     * @return the int
     * @throws BaseException if not an int
     */
    public static int parseInt(String arg) throws BaseException {
        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            throw new BaseException("'" + arg + "' isn't a number!");
        }
    }

    /**
     * Parse a String into a positive int
     * @param arg The argument
     * @return the positive int
     * @throws BaseException if not an int or not positive
     */
    public static int parsePositiveInt(String arg) throws BaseException {
        int i = parseInt(arg);
        if (i < 1) {
            throw new BaseException("'" + arg + "' needs to be atleast 1!");
        }
        return i;
    }

    /**
     * Parse a String into a GameType
     * @param arg The argument
     * @return The GameType
     * @throws BaseException if not a GameType
     */
    public static GameType parseGameType(String arg) throws BaseException {
        GameType gt = GameType.parseGameType(arg);
        if (gt == null) {
            throw new BaseException("'" + arg + "' is not a GameType!");
        }
        return gt;
    }


    /**
     * Get an online player by their value
     * @param playername The value
     * @return The player
     * @throws BaseException if no player found
     */
    public static Player getOnlinePlayer(String playername) throws BaseException {
        Player p = Bukkit.getPlayer(playername);
        if (p == null) {
            throw new BaseException("There is no player online with the name '" + playername + "'!");
        }
        return p;
    }

    /**
     * Create a Usage string from a Command and the Usage
     * @param command The command
     * @param usage The usage
     * @return The string
     */
    public static String createUsageString(CmdTrain command, Usage usage) {
        //Default usage command
        String s = "Usage: /" + command.value() + SUtil.combineToString(command.arguments(), "", v -> " " + v);

        //Get vars
        ArgumentType[] types = usage.value();
        String[] names = usage.typeNames();

        //Add other arguments
        for (int i = 0; i < types.length; i++) {
            ArgumentType type = types[i];

            //Get the value
            String name;
            if (names.length >= i && names[i] != null) {
                name = names[i];
            } else {
                name = type.getPresentableName();
            }
            name = " <" + name;

            //Check if the this is the last argument
            boolean addTwice = (types.length == i + 1 && usage.repeatLastArguments());
            if (addTwice) {
                s += name + " 1>" + name + " 2..>";
            } else {
                s += name + ">";
            }
        }

        //Add optional
        if (usage.allowOtherArguments()) {
            s += " ...";
        }

        //return the string
        return s;
    }


}
