package nl.stoux.SlapGames.Games.Parkour.Commands;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;
import nl.stoux.SlapGames.Commands.Base.BaseGameCommand;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Games.Parkour.Maps.ParkourMap;
import nl.stoux.SlapGames.Games.Parkour.Parkour;
import nl.stoux.SlapGames.Games.Parkour.ParkourPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 22/02/2015.
 */
@Cmd(
        command = @CmdTrain(value = "parkour", arguments = {"mapinfo"}),
        description = "Get the info about the current map",
        permission = "parkour.mapinfo",
        gameMode = GameType.PARKOUR,
        inGameOnly = true
)
@Redirect(
        commands = {@CmdTrain(value = "parkour", arguments = {"map", "info"}), @CmdTrain("mapinfo")},
        keepExtraArguments = false
)
public class ParkourMapInfoCommand extends BaseGameCommand<Parkour, ParkourPlayer> {

    public ParkourMapInfoCommand(CommandSender sender, Command command, String usedAlias, String[] args, GameType gameType) {
        super(sender, command, usedAlias, args, gameType);
    }

    @Override
    public void handle() throws BaseException {
        ParkourPlayer pp = getThisGamePlayer();
        pp.checkMap();

        //Get the map
        ParkourMap map = pp.getMap();

        //Send info
        hMsg("Map info");
        //TODO
    }
}
