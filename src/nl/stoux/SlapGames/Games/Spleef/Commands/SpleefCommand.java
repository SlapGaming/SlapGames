package nl.stoux.SlapGames.Games.Spleef.Commands;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Base.BaseGameCommand;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Games.Spleef.Spleef;
import nl.stoux.SlapGames.Players.GamePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 11/02/2015.
 */
@Cmd(
        command = @CmdTrain("spleef"),
        description = "The Spleef mini-game command",
        permission = "spleef",
        gameMode = GameType.SPLEEF,
        buildTabUsageMessage = true
)
public class SpleefCommand extends BaseGameCommand<Spleef, GamePlayer<Spleef>> {

    public SpleefCommand(CommandSender sender, Command command, String usedAlias, String[] args, GameType gameType) {
        super(sender, command, usedAlias, args, gameType);
    }

    @Override
    public void handle() throws BaseException {
        //TODO
    }

}
