package nl.stoux.SlapGames.Games.Base;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Games.Exceptions.GameStoppedException;
import nl.stoux.SlapGames.Games.Exceptions.InvalidGameStateException;
import nl.stoux.SlapGames.Games.Exceptions.NoSpectateException;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.SlapGames;
import nl.stoux.SlapGames.Util.Util;
import nl.stoux.SlapPlayers.Util.SUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Stoux on 12/12/2014.
 */
public abstract class BaseGame<Handler extends BaseEventHandler, GP extends GamePlayer, GameSettings extends BaseGameSettings> {

    /** The SlapGames instance */
    protected final SlapGames slapGames = SlapGames.getInstance();

    /** The EventHandler for this game */
    @Getter(AccessLevel.PUBLIC)
    protected Handler eventHandler;

    /** The type of game */
    @Getter(AccessLevel.PUBLIC)
    protected GameType gameType;

    /** The current state of the game */
    @Getter(AccessLevel.PUBLIC)
    protected GameState gameState;

    /** The YAML file that belongs to this game */
    @Getter(AccessLevel.PUBLIC)
    protected GameSettings settings;

    /** A HashSet containing all players in this game */
    protected HashSet<GP> players;

    protected BaseGame(GameType gameType) {
        this.gameType = gameType;
        this.settings = createSettings();
        this.eventHandler = createHandler();

        //Set the Game in the EventHandler
        eventHandler.setGame(this);

        //Determine the state
        if (!settings.getSetup().getValue()) {
            gameState = GameState.SETUP;
        } else {
            gameState = (settings.getEnabled().getValue() ? GameState.ENABLED : GameState.DISABLED);
        }

        //Create the set with players
        players = new HashSet<>();
    }

    /**
     * Create a EventHandler
     * @return the handler
     */
    protected abstract Handler createHandler();

    /**
     * Create the GameSettings
     * @return the settings
     */
    protected abstract GameSettings createSettings();


    /** Stop the game */
    public void stop(){
        kickAllPlayers();
        gameState = GameState.STOPPED;
    }

    //<editor-fold desc="Game Join/Leave methods">
    /**
     * A player joins the game
     * @param player The player joining the game
     * @throws InvalidGameStateException if the game is in a state that cant be joined
     * @throws GameStoppedException if the game is currently stopped
     * @throws NoSpectateException if the game doesn't support spectators
     */
    public void playerJoins(Player player, boolean spectate) throws InvalidGameStateException, GameStoppedException, NoSpectateException {
        //Check the state
        switch (gameState) {
            case ENABLED:
            case DISABLED:
            case SETUP:
            case BROKEN:
                throw new InvalidGameStateException();
            case STOPPED:
                throw new GameStoppedException();
        }

        //Check if spectate is supported
        if (spectate && !settings.getSpectators().getValue()) {
            throw new NoSpectateException();
        }

        //Create the player
        GP gamePlayer = createGamePlayer(player);
        gamePlayer.resetPlayer();

        //Add the player to the lists
        players.add(gamePlayer);
        //TODO Add player to playerControl


        //Let the player join the game
        if (spectate) {
            newPlayerSpectates(gamePlayer);
            hMessagePlayers(gamePlayer.getPlayername() + " is now spectating the game!");
        } else {
            newPlayerJoins(gamePlayer);
            hMessagePlayers(gamePlayer.getPlayername() + " has joined the game!");
        }
    }

    /**
     * A player leaves the game
     * @param gamePlayer The player
     */
    public void playerQuits(GP gamePlayer) {
        //Leave the game
        switch (gamePlayer.getPlayerState()) {
            //The player is spectating
            case SPECTATOR:
                spectatorLeaves(gamePlayer);
                break;

            //The player is playing (or dead)
            default:
                playerLeaves(gamePlayer);
                break;
        }

        //Broadcast the leave
        hMessagePlayers(gamePlayer.getPlayername() + " has left the game!");

        //Reset the player
        gamePlayer.resetPlayer();

        //Remove the player from the lists
        players.remove(gamePlayer);
        //TODO Remove player from playerControl

        //Teleport the player back to the spawn of the world
        Util.toHub(gamePlayer);
    }

    /**
     * Create a new <? extends GamePlayer>
     * @param player The player
     * @return The GamePlayer
     */
    protected abstract GP createGamePlayer(Player player);

    /**
     * A new player joins the game
     * @param player The player
     */
    protected abstract void newPlayerJoins(GP player);

    /**
     * A new player starts spectating
     * @param player The player
     */
    protected abstract void newPlayerSpectates(GP player);

    /**
     * A player leaves the game
     * @param player The player
     */
    protected abstract void playerLeaves(GP player);

    /**
     * A player who was spectating leaves
     * @param player The player
     */
    protected abstract void spectatorLeaves(GP player);

    //</editor-fold>

    /**
     * Message a player with the Prefix attached
     * @param player The player
     * @param message The message
     */
    public void hMessagePlayer(GP player, String message) {
        player.sendMessage(settings.getColoredPrefix() + message);
    }

    /**
     * Send all players of this game a message
     * @param messages the message(s)
     */
    public void messagePlayers(String... messages) {
        players.forEach(p -> p.sendMessage(messages));
    }

    /**
     * Send all players of this game a message with the Game prefix attached
     * @param messages The message(s)
     */
    public void hMessagePlayers(String... messages) {
        //Add the prefix
        for (int i = 0; i < messages.length; i++) {
            messages[i] = settings.getColoredPrefix() + messages[i];
        }

        //Send the message
        messagePlayers(messages);
    }

    /**
     * Broadcast a message to the whole server with the prefix
     * @param message The message
     */
    public void broadcast(String message) {
        slapGames.getServer().broadcastMessage(settings.getColoredPrefix() + message);
    }

    /**
     * Get all players with a certain state
     * @param states one or more states
     */
    protected Collection<GP> getPlayers(PlayerState... states) {
        return players.stream()
                .filter(gp -> SUtil.contains(states, gp.getPlayerState()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Set the players of a certain state to a new state
     * @param currentState The current state the players have
     * @param newState The new state
     */
    protected void setPlayersToState(PlayerState currentState, PlayerState newState) {
        players.stream().filter(gp -> gp.getPlayerState() == currentState).forEach(gp -> gp.setPlayerState(newState));
    }

    /**
     * Count the players with a certain state
     * @param states the states
     * @return the number of players
     */
    protected int countPlayers(PlayerState... states) {
        return (int) players.stream().filter(gp -> SUtil.contains(states, gp.getPlayerState())).count();
    }

    /**
     * Execute a function for each player with a certain state
     * @param states The state
     * @param function The function
     */
    protected void forEachPlayer(Consumer<GP> function, PlayerState... states) {
        players.stream().filter(gp -> SUtil.contains(states, gp.getPlayerState())).forEach(function);
    }

    /** Kick all players from the game */
    protected void kickAllPlayers() {
        players.forEach(gp -> playerQuits(gp));
    }

    /**
     * Set the playerstate to LOBBY and teleport them into the lobby
     * @param player the player
     */
    protected void setPlayerInLobby(GP player) {
        player.setPlayerState(PlayerState.LOBBY);
        player.getPlayer().teleport(getLobby(player));
    }

    /**
     * Teleport the player to the lobby (using #getLobby(player)) and set a playerstate
     * @param player The player
     * @param playerState The new playerstate
     */
    protected void setPlayerInLobby(GP player, PlayerState playerState) {
        player.setPlayerState(playerState);
        player.getPlayer().teleport(getLobby(player));
    }

    /**
     * Set a player into spectator mode if allowed
     * Teleports the player to Spectator location
     * @param player the player
     */
    public void setSpectating(GP player) {
        if (canGoIntoSpectatorGameMode(player)) {
            player.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        player.setSpectating(true);
        player.teleport(getSpectatorLocation(player));
    }

    /**
     * Get the lobby location for a player
     * @param player The player
     * @return the lobby
     */
    public Location getLobby(GP player){
        return settings.getLobby().getValue();
    }


    /**
     * Get the spectator location for a player
     * @param player The player
     * @return the spectator location (or null)
     */
    public Location getSpectatorLocation(GP player) {
        return settings.getSpectatorLocation().getValue();
    }

    /**
     * Get the out of bounds region for this game for a specific player
     * @param player The player
     * @return the region (or null)
     */
    public ProtectedRegion getOutOfBounds(GP player) {
        return settings.getOutOfBoundsRegion().getValue();
    }

    /**
     * Get the spectator out of bounds region for this game for a specific player
     * @param player The player
     * @return the region (or null)
     */
    public ProtectedRegion getSpectatorOutOfBounds(GP player) {
        return settings.getSpecOutOfBoundsRegion().getValue();
    }

    /**
     * The player can go into Spectator GameMode
     * @param player The player
     * @return can go
     */
    public boolean canGoIntoSpectatorGameMode(GP player) {
        return settings.getSpectatorGameMode().getValue();
    }

    /**
     * The player can, while in GameMode.SPECTATOR, no clip through walls
     * @param player The player
     * @return is allowed
     */
    public boolean isAllowedToNoClip(GP player) {
        return settings.getSpectatorGameModeNoClip().getValue();
    }

}
