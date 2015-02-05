package nl.stoux.SlapGames.Commands.Base;

import nl.stoux.SlapGames.Games.Base.BaseGame;
import nl.stoux.SlapGames.Games.GameType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Created by Stoux on 05/02/2015.
 *
 * A command class that functions as a Base for game bound games.
 *
 */
public abstract class BaseGameCommand<BG extends BaseGame> extends BaseCommand {

    protected GameType gameType;
    protected BG game;

    public BaseGameCommand(CommandSender sender, Command command, String usedAlias, String[] args, GameType gameType) {
        super(sender, command, usedAlias, args);
        this.gameType = gameType;
        game = null;
        //TODO Get game from the GameController
    }


}
