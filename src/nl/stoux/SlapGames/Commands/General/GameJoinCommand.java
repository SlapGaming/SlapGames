package nl.stoux.SlapGames.Commands.General;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;
import nl.stoux.SlapGames.Commands.Annotations.Usage;
import nl.stoux.SlapGames.Commands.Base.BaseCommand;
import nl.stoux.SlapGames.Commands.Model.ArgumentType;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Games.Base.BaseGame;
import nl.stoux.SlapGames.Games.GameControl;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Players.PlayerState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 11/02/2015.
 */
@Cmd(
        command = @CmdTrain(value = "game", arguments = "join"),
        usage = @Usage(ArgumentType.GAME_TYPE),
        description = "Join a mini-game",
        permission = "player",
        playerOnly = true
)
@Redirect(
        commands = @CmdTrain("joingame"),
        keepExtraArguments = true
)
public class GameJoinCommand extends BaseCommand {

    public GameJoinCommand(CommandSender sender, Command command, String usedAlias, String[] args) {
        super(sender, command, usedAlias, args);
    }

    @Override
    public void handle() throws BaseException {
        switchGame(this, false);
    }

    /**
     * Switch/Join game
     * @param command The command
     * @param asSpectator Join as spectator
     * @throws BaseException
     */
    public static void switchGame(BaseCommand command, boolean asSpectator) throws BaseException{
        //Get the GameType
        GameType gameType = command.getArgument(0, GameType.class);

        //Check if in a game
        GamePlayer gp = command.getGamePlayer();
        if (gp != null) {
            //Already in a game
            if (gp.getGame().getGameType() == gameType) {
                //Check if switch mode
                boolean switchMode;
                if (asSpectator) {
                    switchMode = (gp.getPlayerState() != PlayerState.SPECTATOR);
                } else {
                    switchMode = (gp.getPlayerState() == PlayerState.SPECTATOR);
                }
                if (switchMode) {
                    //TODO Switch mode
                } else {
                    throw new BaseException("You are already in that game.");
                }
            } else {
                //TODO Maybe auto switch?
                throw new BaseException("You're already in a different game!");
            }
            return;
        }

        //Join the game
        BaseGame game = GameControl.getGame(gameType);
        game.playerJoins(command.getPlayer(), asSpectator);
    }


}
