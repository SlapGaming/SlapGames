package nl.stoux.SlapGames.Games.Parkour.Commands.Redirects;

import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;

/**
 * Created by Stoux on 11/02/2015.
 */
@Redirect(
        commands = @CmdTrain(value = "parkour", arguments = "leave"),
        redirectToCommand = @CmdTrain(value = "game", arguments = "leave"),
        keepExtraArguments = false
)
public interface ParkourLeaveCommand {}
