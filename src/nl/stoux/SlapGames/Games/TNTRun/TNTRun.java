package nl.stoux.SlapGames.Games.TNTRun;

import nl.stoux.SlapGames.Games.Base.Arena.BaseVersusArenaGame;
import nl.stoux.SlapGames.Games.Base.BaseGame;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Games.TNTRun.Arenas.TNTRunArena;
import nl.stoux.SlapGames.Games.TNTRun.Arenas.TNTRunArenaState;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.Storage.YamlFile;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Stoux on 26/01/2015.
 */
public class TNTRun extends BaseVersusArenaGame<TNTRunEventHandler, TNTRunPlayer, TNTRunSettings, TNTRunArena, TNTRunArenaState> {

    public TNTRun() {
        super(GameType.TNT_RUN, 3);
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

    @Override
    protected TNTRunArena createArena(YamlFile file) {
        return new TNTRunArena(file);
    }
    //</editor-fold>


    @Override
    protected void startingIn(int secondsLeft) {
        switch (secondsLeft) {
            case 3:
                //Teleport the players
                currentArena.teleport(getPlayers(PlayerState.PLAYING));
                forEachPlayer(gp -> setSpectating(gp), PlayerState.SPECTATOR);
                break;

            case 2:
                hMessagePlayers("Get ready...");
                break;

            case 1:
                //TODO Enable specials (potions, etc)
                hMessagePlayers("Set...");
                break;

            case 0:
                //Create the SpleefGame
                currentArena.gameStarts(countPlayers(PlayerState.PLAYING));
                hMessagePlayers("Run!");
                break;
        }
    }

    @Override
    protected TNTRunArenaState selectArena() {
        TNTRunArena selectedArena = enabledArenas.get(Util.getRandom().nextInt(enabledArenas.size())); //TODO Replace with vote based system
        return new TNTRunArenaState(selectedArena);
    }




}
