package nl.stoux.SlapGames.Commands;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Base.BaseCommand;
import nl.stoux.SlapGames.Exceptions.BaseException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 04/02/2015.
 */
@Cmd(
        command = "games",
        permission = "games",
        description = "Shows a list of all the games and the players"
)
public class GamesCommand extends BaseCommand {

    public GamesCommand(CommandSender sender, Command command, String usedAlias, String[] args) {
        super(sender, command, usedAlias, args);
    }

    @Override
    public void handle() throws BaseException {
        badMsg("Fgt");
    }

}
