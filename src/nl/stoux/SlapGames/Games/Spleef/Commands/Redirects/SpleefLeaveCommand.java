package nl.stoux.SlapGames.Games.Spleef.Commands.Redirects;

import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;

/**
 * Created by Stoux on 11/02/2015.
 */
@Redirect(
        commands = @CmdTrain(value = "spleef", arguments = "leave"),
        redirectToCommand = @CmdTrain(value = "game", arguments = "leave"),
        keepExtraArguments = false
)
public interface SpleefLeaveCommand {}
