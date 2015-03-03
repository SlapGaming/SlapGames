package nl.stoux.SlapGames.Games.Base.Arena;

import lombok.Getter;
import nl.stoux.SlapGames.Util.Log;
import nl.stoux.SlapGames.Util.Schedule;
import nl.stoux.SlapPlayers.SQL.DAO.Dao;
import nl.stoux.SlapPlayers.SQL.DAO.DaoControl;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Stoux on 22/02/2015.
 */
public abstract class BaseArenaState<Arena extends BaseArena, GameData, GameDataPlayer> {

    /** The chosen Arena */
    @Getter protected Arena arena;

    /** The GameData */
    protected GameData gameData;

    public BaseArenaState(Arena arena) {
        this.arena = arena;
        gameData = createGameData();
    }

    /**
     * Setup the arena
     * @param forPlayers max number of players
     */
    public abstract void setupArena(int forPlayers);

    /**
     * The game has started
     * @param nrOfPlayers with this number of players
     */
    public abstract void gameStarts(int nrOfPlayers);

    /**
     * A player has won the game
     * @param playerID The player ID
     */
    public abstract void playerWon(int playerID);

    /**
     * Check if this map supports spectators
     * @return has spectators
     */
    public boolean hasSpectators() {
        return arena.getSettings().getSpectators().getValue();
    }

    /**
     * Get the spectator location
     * @return the location
     */
    public Location getSpectatorLocation() {
        return arena.getSettings().getSpectatorLocation().getValue();
    }

    /**
     * Create the GameData object
     * @return The object
     */
    protected abstract GameData createGameData();

    /**
     * Create a GameDataPlayer object
     * @param data The GameData
     * @param playerID The ID of the player
     * @return THe object
     */
    protected abstract GameDataPlayer createGameDataPlayer(GameData data, int playerID);

    /**
     * Insert the GameData into the DB
     */
    public void insertInDatabase(final HashSet<Integer> playerIDs) {
        //Get the GameData
        final GameData gd = gameData;

        //Go into async
        Schedule.runAsync(() -> {
            //Create the GameData DAO
            Dao<GameData> dataDAO = (Dao<GameData>) DaoControl.createDAO(gd.getClass());
            try {
                dataDAO.insert(gd);
            } catch (SQLException e) {
                Log.severe("[SQL] Failed to insert " + gd.getClass().getName() + ": " + e.getMessage());
                return;
            } finally {
                dataDAO.destroy();
            }

            //Create players
            List<GameDataPlayer> playerData = playerIDs.stream().map(id -> createGameDataPlayer(gd, id)).collect(Collectors.toList());

            //Insert the players
            Dao<GameDataPlayer> playerDAO = (Dao<GameDataPlayer>) DaoControl.createDAO(playerData.get(0).getClass());
            for (GameDataPlayer player : playerData) {
                try {
                    playerDAO.insert(player);
                } catch (SQLException e) {
                    Log.severe("[SQL] Failed to insert " + gd.getClass().getName() + ": " + e.getMessage());
                }
            }
            playerDAO.destroy();
        });
    }


}
