package nl.stoux.SlapGames.Games.Parkour.Commands.Redirects;

import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;

/**
 * Created by Stoux on 11/02/2015.
 */
@Redirect(
        commands = {@CmdTrain(value = "parkour", arguments = "join"), @CmdTrain(value = "joinparkour")},
        redirectToCommand = @CmdTrain(value = "game", arguments = {"join", "parkour"}),
        keepExtraArguments = false
)
public interface ParkourJoinCommand {}
