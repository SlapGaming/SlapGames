package nl.stoux.SlapGames.Commands.General;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;
import nl.stoux.SlapGames.Commands.Base.BaseCommand;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Players.GamePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 11/02/2015.
 */
@Cmd(
        command = @CmdTrain(value = "game", arguments = "leave"),
        description = "Leave a mini-game",
        permission = "player",
        playerOnly = true,
        inGameOnly = true
)
@Redirect(
        commands = {@CmdTrain("leave"), @CmdTrain("leavegame"), @CmdTrain("gleave")},
        keepExtraArguments = false
)
public class GameLeaveCommand extends BaseCommand {

    public GameLeaveCommand(CommandSender sender, Command command, String usedAlias, String[] args) {
        super(sender, command, usedAlias, args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handle() throws BaseException {
        //Get the player & leave the game
        GamePlayer gp = getGamePlayer();
        gp.getGame().playerQuits(gp);
    }
}
