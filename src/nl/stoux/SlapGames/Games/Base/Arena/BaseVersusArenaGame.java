package nl.stoux.SlapGames.Games.Base.Arena;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.stoux.SlapGames.Games.Base.BaseEventHandler;
import nl.stoux.SlapGames.Games.Base.GameState;
import nl.stoux.SlapGames.Games.Base.Versus.BaseVersusGame;
import nl.stoux.SlapGames.Games.Base.Versus.BaseVersusGameSettings;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.Storage.YamlFile;
import nl.stoux.SlapGames.Util.Log;
import nl.stoux.SlapGames.Util.PlayerUtil;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Stoux on 22/02/2015.
 */
public abstract class BaseVersusArenaGame<Handler extends BaseEventHandler, GP extends GamePlayer, GameSettings extends BaseVersusGameSettings, Arena extends BaseArena, ArenaState extends BaseArenaState<Arena, ? , ?>> extends BaseVersusGame<Handler, GP, GameSettings> {

    /** All arenas, including disabled ones */
    protected HashSet<Arena> allArenas;
    /** The enabled arenas */
    protected ArrayList<Arena> enabledArenas;

    /** The current arena */
    protected ArenaState currentArena;


    public BaseVersusArenaGame(GameType gameType, int startingCountdownSeconds) {
        super(gameType, startingCountdownSeconds);

        //Create the sets
        allArenas = new HashSet<>();
        enabledArenas = new ArrayList<>();

        //Load the arenas
        loadArenas();

        //Check state
        if (!settings.getSetup().getValue()) {
            gameState = GameState.SETUP;
        } else if (!settings.getEnabled().getValue()) {
            gameState = GameState.DISABLED;
        } else {
            if (enabledArenas.isEmpty()) {
                Log.warn("[" + gameType.getPresentableName() + "] No arenas available");
                gameState = GameState.DISABLED;
            } else {
                gameState = GameState.ENABLED;
            }
        }
    }

    //<editor-fold desc="Setup methods">
    /** Load all arenas */
    private void loadArenas() {
        //Wipe current collections
        allArenas.clear();
        enabledArenas.clear();

        //Loop through files
        for (File file : Util.getGameFolder(gameType).listFiles()) {
            String name = file.getName();
            //Only looking for YML files
            if (!name.endsWith(".yml") || name.equalsIgnoreCase("config.yml")) {
                continue;
            }

            //Create arena from the file
            YamlFile arenaFile = Util.getYamlFile(gameType, name.substring(0, name.length() - 4));
            Arena arena = createArena(arenaFile);

            //Add to collections
            allArenas.add(arena);
            if (arena.getSettings().getEnabled().getValue()) {
                enabledArenas.add(arena);
                arena.saveArena();
            }
        }
    }

    /**
     * Create an Arena from a YamlFile
     * @param file The file
     * @return The Arena
     */
    protected abstract Arena createArena(YamlFile file);
    //</editor-fold>


    @Override
    protected void playerWon(GP player) {
        //Set the winner
        currentArena.playerWon(PlayerUtil.getUserID(player));

        //Get player IDs
        HashSet<Integer> playerIDs = new HashSet<>();
        forEachPlayer(p -> playerIDs.add(PlayerUtil.getUserID(p)), PlayerState.PLAYING);

        //Insert into DB
        currentArena.insertInDatabase(playerIDs);
    }

    @Override
    protected void setupVersusGame() {
        //Select the arena
        currentArena = selectArena();
        currentArena.setupArena(countPlayers(PlayerState.PLAYING));
    }

    /**
     * Select an Arena to play on
     * @return the arena
     */
    protected abstract ArenaState selectArena();

    @Override
    protected void stopVersusGame() {
        super.stopVersusGame();
        //Restore the arena
        currentArena.getArena().restoreArena();
        currentArena = null;
    }

    @Override
    public Location getLobby(GP player) {
        if (currentArena != null) {
            Location loc = currentArena.getArena().getSettings().getLobby().getValue();
            if (loc != null) {
                return loc;
            }
        }
        return super.getLobby(player);
    }

    @Override
    public Location getSpectatorLocation(GP player) {
        if (currentArena != null && currentArena.hasSpectators()) {
            return currentArena.getSpectatorLocation();
        }
        return super.getLobby(player);
    }

    @Override
    public ProtectedRegion getOutOfBounds(GP player) {
        if (currentArena != null) {
            return currentArena.getArena().getSettings().getOutOfBoundsRegion().getValue();
        }
        return super.getOutOfBounds(player);
    }

    @Override
    public ProtectedRegion getSpectatorOutOfBounds(GP player) {
        if (currentArena != null) {
            return currentArena.getArena().getSettings().getSpecOutOfBoundsRegion().getValue();
        }
        return super.getSpectatorOutOfBounds(player);
    }
}
