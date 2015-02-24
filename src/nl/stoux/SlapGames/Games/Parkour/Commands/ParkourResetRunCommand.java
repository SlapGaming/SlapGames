package nl.stoux.SlapGames.Games.Parkour.Commands;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;
import nl.stoux.SlapGames.Commands.Base.BaseGameCommand;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Games.Parkour.Parkour;
import nl.stoux.SlapGames.Games.Parkour.ParkourPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 22/02/2015.
 */
@Cmd(
        command = @CmdTrain(value = "parkour", arguments = {"resetrun"}),
        description = "Reset the current run",
        permission = "parkour.resetrun",
        gameMode = GameType.PARKOUR,
        inGameOnly = true
)
@Redirect(
        commands = {@CmdTrain("resetrun"), @CmdTrain(value = "parkour", arguments = {"reset", "run"}), @CmdTrain("restartrun")},
        keepExtraArguments = false
)
public class ParkourResetRunCommand extends BaseGameCommand<Parkour, ParkourPlayer> {

    public ParkourResetRunCommand(CommandSender sender, Command command, String usedAlias, String[] args, GameType gameType) {
        super(sender, command, usedAlias, args, gameType);
    }

    @Override
    public void handle() throws BaseException {
        getThisGamePlayer().restartRun();
    }

}