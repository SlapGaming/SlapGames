package nl.stoux.SlapGames.Games.Parkour.Commands.Redirects;

import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;

/**
 * Created by Stoux on 11/02/2015.
 */
@Redirect(
        commands = {@CmdTrain(value = "parkour", arguments = "spectate"), @CmdTrain(value = "spectateparkour")},
        redirectToCommand = @CmdTrain(value = "game", arguments = {"spectate", "parkour"}),
        keepExtraArguments = false
)
public interface ParkourSpectateCommand {}
