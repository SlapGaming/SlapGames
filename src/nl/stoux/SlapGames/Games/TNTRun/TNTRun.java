package nl.stoux.SlapGames.Games.TNTRun;

import nl.stoux.SlapGames.Games.Base.BaseGame;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.entity.Player;

/**
 * Created by Stoux on 26/01/2015.
 */
public class TNTRun extends BaseGame<TNTRunEventHandler, TNTRunPlayer, TNTRunSettings> {

    public TNTRun(GameType gameType) {
        super(gameType);
    }

    //<editor-fold desc="Create functions">
    @Override
    protected TNTRunEventHandler createHandler() {
        return new TNTRunEventHandler();
    }

    @Override
    protected TNTRunSettings createSettings() {
        return new TNTRunSettings(Util.getYamlFile(gameType, "config"));
    }

    @Override
    protected TNTRunPlayer createGamePlayer(Player player) {
        return new TNTRunPlayer(player, this);
    }
    //</editor-fold>

    @Override
    protected void newPlayerJoins(TNTRunPlayer player) {

    }

    @Override
    protected void newPlayerSpectates(TNTRunPlayer player) {

    }

    @Override
    protected void playerLeaves(TNTRunPlayer player) {

    }

    @Override
    protected void spectatorLeaves(TNTRunPlayer player) {

    }
}
