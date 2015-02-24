package nl.stoux.SlapGames.Commands.General;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;
import nl.stoux.SlapGames.Commands.Annotations.Usage;
import nl.stoux.SlapGames.Commands.Base.BaseCommand;
import nl.stoux.SlapGames.Commands.Model.ArgumentType;
import nl.stoux.SlapGames.Exceptions.BaseException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 11/02/2015.
 */
@Cmd(
        command = @CmdTrain(value = "game", arguments = "spectate"),
        usage = @Usage(ArgumentType.GAME_TYPE),
        description = "Spectate a mini-game",
        permission = "player",
        playerOnly = true
)
@Redirect(
        commands = @CmdTrain("spectategame")
)
public class GameSpectateCommand extends BaseCommand {

    public GameSpectateCommand(CommandSender sender, Command command, String usedAlias, String[] args) {
        super(sender, command, usedAlias, args);
    }

    @Override
    public void handle() throws BaseException {
        GameJoinCommand.switchGame(this, true);
    }
}
