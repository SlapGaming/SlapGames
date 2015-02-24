package nl.stoux.SlapGames.Commands.Base;

import lombok.Setter;
import nl.stoux.SlapGames.Commands.Exceptions.UsageException;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Games.GameControl;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Util.PlayerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Stoux on 05/02/2015.
 */
public abstract class BaseCommand {

    /** The sender who executed this command */
    protected CommandSender sender;

    /** The actual command */
    protected Command command;

    /** The used alias */
    protected String usedAlias;

    /** The used arguments */
    protected String[] args;

    /** The arguments cast to a certain type */
    @Setter private Object[] castArguments;

    public BaseCommand(CommandSender sender, Command command, String usedAlias, String[] args) {
        this.sender = sender;
        this.command = command;
        this.usedAlias = usedAlias;
        this.args = args;
    }

    /** Handle the command */
    public abstract void handle() throws BaseException;


    /**
     * Cast the CommandSender to player
     * @return The player
     */
    public Player getPlayer() {
        return (Player) sender;
    }

    /**
     * Get the GamePlayer
     * Warning: Casts the CommandSender to a player
     * @return The GamePlayer or null
     */
    public GamePlayer getGamePlayer() {
        return GameControl.getGamePlayer(getPlayer());
    }

    /**
     * Message the CommandSender
     * @param message the message
     */
    protected void msg(String message) {
        sender.sendMessage(message);
    }

    /**
     * Message the CommandSender with a [Header] prepended before the message
     * @param message The message
     */
    protected void hMsg(String message) {
        //TODO
    }

    /**
     * Send a 'Bad Message' in red to the CommandSender
     * @param message The message
     */
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

    /**
     * Get an argument cast to a certain type
     * WARNING: This is only avaialable
     * @param index The index of the argument
     * @param typeOfArgument
     * @param <Arg> The class of the argument
     * @return The argument or null
     */
    public <Arg extends Object> Arg getArgument(int index, Class<Arg> typeOfArgument) {
        try {
            return (Arg) castArguments[index];
        } catch (Exception e) {
            return null;
        }
    }


}
