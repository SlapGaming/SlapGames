package nl.stoux.SlapGames.Commands.General;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;
import nl.stoux.SlapGames.Commands.Base.BaseCommand;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 11/02/2015.
 */
@Cmd(
        command = @CmdTrain("hub"),
        description = "Go (back) to the minigames hub",
        permission = "hub",
        playerOnly = true
)
@Redirect(
        commands = @CmdTrain("server")
)
public class HubCommand extends BaseCommand {

    public HubCommand(CommandSender sender, Command command, String usedAlias, String[] args) {
        super(sender, command, usedAlias, args);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handle() throws BaseException {
        //Check if in a game
        GamePlayer gp = getGamePlayer();
        if (gp != null) {
            gp.getGame().playerQuits(gp);
        } else {
            Util.toHub(getPlayer());
        }
    }
}
