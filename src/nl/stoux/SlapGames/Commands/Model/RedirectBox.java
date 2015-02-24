package nl.stoux.SlapGames.Commands.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;

/**
 * Created by Stoux on 12/02/2015.
 */
@Getter
@AllArgsConstructor
public class RedirectBox {

    /** The command that it originates from */
    private CmdTrain fromCommand;

    /** The command train that it should redirect to */
    private CmdTrain toCommand;

    /** Arguments should be kept on redirect */
    private boolean keepArguments;

}
