package nl.stoux.SlapGames.Games.Parkour.Commands;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Base.BaseGameCommand;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Games.Parkour.Parkour;
import nl.stoux.SlapGames.Games.Parkour.ParkourPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 23/02/2015.
 */
@Cmd(
        command = @CmdTrain(value = "parkour", arguments = "maps"),
        description = "Show all parkour maps",
        permission = "parkour.maps",
        gameMode = GameType.PARKOUR
)
public class ParkourMapsCommand extends BaseGameCommand<Parkour, ParkourPlayer> {

    public ParkourMapsCommand(CommandSender sender, Command command, String usedAlias, String[] args, GameType gameType) {
        super(sender, command, usedAlias, args, gameType);
    }

    @Override
    public void handle() throws BaseException {
        //TODO
    }
}
