package nl.stoux.SlapGames.Games;

import nl.stoux.SlapGames.Games.Base.BaseGame;
import nl.stoux.SlapGames.Players.GamePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Stoux on 22/01/2015.
 */
public class GameControl {

    /** The instance */
    private static GameControl instance;

    /** Map with all games */
    private HashMap<GameType, BaseGame> games;

    /** Map with all players */
    private HashMap<Player, GamePlayer> players;


    public GameControl() {
        this.games = new HashMap<>();
        this.players = new HashMap<>();
        GameControl.instance = this;
    }

    /**
     * Get a game by it's type
     * @param type The type
     * @return The game or null
     */
    public static BaseGame getGame(GameType type) {
        return instance.games.get(type);
    }

    /**
     * Register a game with control
     * @param game the game
     */
    public static void registerGame(BaseGame game) {
        instance.games.put(game.getGameType(), game);
    }


    /**
     * Get the GamePlayer
     * @param player the player
     * @return The GamePlayer or null
     */
    public static GamePlayer getGamePlayer(Player player) {
        return instance.players.get(player);
    }

    /**
     * Store a GamePlayer
     * @param player The player
     */
    public static void storeGamePlayer(GamePlayer player) {
        instance.players.put(player.getPlayer(), player);
    }

    /**
     * Remove a GamePlayer
     * @param player the player
     */
    public static void removeGamePlayer(GamePlayer player) {
        removeGamePlayer(player.getPlayer());
    }

    /**
     * Remove a GamePlayer
     * @param player the player
     */
    public static void removeGamePlayer(Player player) {
        instance.players.remove(player);
    }



}
