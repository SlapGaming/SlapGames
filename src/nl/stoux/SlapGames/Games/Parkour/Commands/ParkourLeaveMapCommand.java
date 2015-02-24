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
        command = @CmdTrain(value = "parkour", arguments = "leavemap"),
        description = "Leave the map",
        permission = "parkour.leavemap",
        gameMode = GameType.PARKOUR,
        inGameOnly = true
)
@Redirect(
        commands = {@CmdTrain("leavemap"), @CmdTrain(value = "parkour", arguments = {"leave", "map"}), @CmdTrain(value = "parkour", arguments = {"map", "leave"})},
        keepExtraArguments = false
)
public class ParkourLeaveMapCommand extends BaseGameCommand<Parkour, ParkourPlayer> {

    public ParkourLeaveMapCommand(CommandSender sender, Command command, String usedAlias, String[] args, GameType gameType) {
        super(sender, command, usedAlias, args, gameType);
    }

    @Override
    public void handle() throws BaseException {
        ParkourPlayer player = getThisGamePlayer();
        player.checkMap();

        //Leave the map
        player.getMap().playerLeaves(player);
        player.resetPlayer();
        player.teleport(game.getLobby(player));
        game.hMessagePlayer(player, "Left the map!");
    }
}
