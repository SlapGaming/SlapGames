package nl.stoux.SlapGames.Commands.Base;

import nl.stoux.SlapGames.Games.Base.BaseGame;
import nl.stoux.SlapGames.Games.GameControl;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Players.GamePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 05/02/2015.
 *
 * A command class that functions as a Base for game bound games.
 *
 */
public abstract class BaseGameCommand<BG extends BaseGame, GP extends GamePlayer<BG>> extends BaseCommand {

    protected GameType gameType;
    protected BG game;

    public BaseGameCommand(CommandSender sender, Command command, String usedAlias, String[] args, GameType gameType) {
        super(sender, command, usedAlias, args);
        this.gameType = gameType;
        game = null;
        game = (BG) GameControl.getGame(gameType);
    }

    /**
     * Get the player who is in this game
     * @return the player
     */
    public GP getThisGamePlayer() {
        return (GP) getGamePlayer();
    }

}
