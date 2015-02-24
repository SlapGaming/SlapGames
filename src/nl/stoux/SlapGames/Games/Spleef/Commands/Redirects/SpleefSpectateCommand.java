package nl.stoux.SlapGames.Games.Spleef.Commands.Redirects;

import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;

/**
 * Created by Stoux on 11/02/2015.
 */
@Redirect(
        commands = {@CmdTrain(value = "spleef", arguments = "spectate"), @CmdTrain(value = "spectatespleef")},
        redirectToCommand = @CmdTrain(value = "game", arguments = {"spectate", "spleef"}),
        keepExtraArguments = false
)
public interface SpleefSpectateCommand {}
