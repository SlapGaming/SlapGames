package nl.stoux.SlapGames.Commands.General;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;
import nl.stoux.SlapGames.Commands.Base.BaseCommand;
import nl.stoux.SlapGames.Exceptions.BaseException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 04/02/2015.
 */
@Cmd(
        command = @CmdTrain("game"),
        description = "Shows a list of all the games and the players",
        permission = "gameinfo"
)
@Redirect(
        commands = {@CmdTrain("games"), @CmdTrain("slapgames"), @CmdTrain("stouxgames")}
)
public class GameCommand extends BaseCommand {

    public GameCommand(CommandSender sender, Command command, String usedAlias, String[] args) {
        super(sender, command, usedAlias, args);
    }

    @Override
    public void handle() throws BaseException {

    }

}
