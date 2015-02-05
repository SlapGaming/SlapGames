package nl.stoux.SlapGames.Games.Spleef;

import nl.stoux.SlapGames.Games.Base.BaseGame;
import nl.stoux.SlapGames.Games.Base.GameState;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Games.Spleef.Arenas.SpleefArena;
import nl.stoux.SlapGames.Games.Spleef.Arenas.SpleefArenaState;
import nl.stoux.SlapGames.Games.Spleef.Models.SpleefGame;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.Storage.YamlFile;
import nl.stoux.SlapGames.Util.Log;
import nl.stoux.SlapGames.Util.Schedule;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Stoux on 22/01/2015.
 */
public class Spleef extends BaseGame<SpleefEventHandler, GamePlayer<Spleef>, SpleefSettings> {

    /** All arenas */
    private HashSet<SpleefArena> arenas;
    /** The enabled arenas */
    private List<SpleefArena> enabledArenas;

    /** The arena currently being played */
    private SpleefArenaState currentArena;

    /** The ID of the countdown task */
    private Integer startDelayTaskID;
    /** The ID of the starting task */
    private Integer startingTaskID;




    public Spleef() {
        super(GameType.SPLEEF);

        //Create the arena sets
        arenas = new HashSet<>();
        enabledArenas = new ArrayList<>();

        //Load maps
        loadMaps();

        //Check state
        if (!settings.getSetup().getValue()) {
            gameState = GameState.SETUP;
        } else if (!settings.getEnabled().getValue() || enabledArenas.isEmpty()) {
            gameState = GameState.DISABLED;
        } else {
            //Check if any arenas are enabled
            if (enabledArenas.isEmpty()) {
                Log.warn("[Spleef] No arenas available");
                gameState = GameState.DISABLED;
            } else {
                gameState = GameState.ENABLED;
            }
        }
        Log.info("[Spleef] Status: " + gameState.toString());
    }

    @Override
    protected SpleefEventHandler createHandler() {
        return new SpleefEventHandler();
    }

    @Override
    protected SpleefSettings createSettings() {
        return new SpleefSettings(Util.getYamlFile(gameType, "config"));
    }

    /** Load the maps */
    private void loadMaps() {
        //Wipe current arenas
        arenas.clear();
        enabledArenas.clear();

        //Get arenas
        for (File file : Util.getGameFolder(gameType).listFiles()) {
            String name = file.getName();
            //Find YML files
            if (!name.endsWith(".yml") || name.equals("config.yml")) {
                continue;
            }

            //Create arena from the file
            YamlFile arenaFile = Util.getYamlFile(gameType, name.substring(0, name.length() - 4));
            SpleefArena arena = new SpleefArena(arenaFile);
            arenas.add(arena);

            //Check if the arena is enabled
            if (arena.getSettings().getEnabled().getValue()) {
                enabledArenas.add(arena);
                arena.saveFloor();
            }
        }
    }

    @Override
    protected GamePlayer<Spleef> createGamePlayer(Player player) {
        return new GamePlayer<>(player, this);
    }

    //<editor-fold desc="Join/Leave methods">
    @Override
    protected void newPlayerJoins(GamePlayer<Spleef> player) {
        switch (gameState) {
            case STARTING: case PLAYING:
                player.setPlayerState(PlayerState.LOBBY);
                setSpectating(player);
                break;

            case FINISHED:
                setPlayerInLobby(player);
                break;

            case LOBBY:
                setPlayerInLobby(player);
                if (shouldStartGame()) {
                    countdown();
                } else {
                    int currentPlayers = getPlayers(PlayerState.LOBBY).size();
                    int stillNeeded = settings.getMinimumPlayers() - currentPlayers;
                    hMessagePlayers("You need " + stillNeeded + " more player" + (stillNeeded == 1 ? " " : "s ") + "to start a game!");
                }
                break;
        }
    }

    @Override
    protected void newPlayerSpectates(GamePlayer<Spleef> player) {
        player.setPlayerState(PlayerState.SPECTATOR);
        switch (gameState) {
            case STARTING: case PLAYING:
                setSpectating(player);
                break;

            case FINISHED: case LOBBY:
                player.teleport(getLobby(player));
                break;
        }
    }

    @Override
    protected void playerLeaves(GamePlayer<Spleef> player) {

    }

    @Override
    protected void spectatorLeaves(GamePlayer<Spleef> player) {

    }
    //</editor-fold>


    /**
     * A player has died (game over)
     * @param player the player
     */
    public void playerDies(GamePlayer<Spleef> player) {
        player.setPlayerState(PlayerState.GAME_OVER);

        //Check if a player has won
        int playersLeft = countPlayers(PlayerState.PLAYING);
        if (playersLeft <= 0) {
            //Something went wrong
            hMessagePlayers("No players are left.... huh.");
            stopArena(true);
        } else if (playersLeft == 1) {
            //Only one player left, that means there is a winner
            GamePlayer<Spleef> winner = players.stream().filter(gp -> gp.getPlayerState() == PlayerState.PLAYING).findFirst().get();

            //Update data
            SpleefGame spleefGame = currentArena.getSpleefGame();
            spleefGame.setFinishTime(System.currentTimeMillis());
            spleefGame.setWinningUserID(Util.getUserID(winner.getPlayer()));
            //=> Save the win
            Schedule.insertASync(spleefGame);

            //Broadcast the win
            hMessagePlayers(winner.getPlayername() + " has won!");
            stopArena(true);
        } else {
            //Still players left
            setSpectating(player);
        }
    }


    /**
     * Check if a game should startArena
     * @return should startArena
     */
    public boolean shouldStartGame() {
        return getPlayers(PlayerState.GAME_OVER, PlayerState.LOBBY, PlayerState.PLAYING).size() >= settings.getMinimumPlayers();
    }

    /** Start a countdown to startArena the game */
    public void countdown() {
        //Start the countdown task
        startDelayTaskID = Schedule.runLater(new Runnable() {
            @Override
            public void run() {
                //Check if still able to run
                if (!shouldStartGame()) {
                    startDelayTaskID = null;
                    hMessagePlayers("There aren't enough players to start the game!");
                } else {
                    startArena();
                }
            }
        }, 20 * settings.getSecondsBetweenGames());

        //Notify players of the game starting
        broadcast("Spleef is starting in " + settings.getSecondsBetweenGames() + " seconds!"); //TODO Parse with minutes + seconds
    }

    /** Stop the countdown */
    private void stopCountdown() {
        if (startDelayTaskID != null) {
            Schedule.getScheduler().cancelTask(startDelayTaskID);
            startDelayTaskID = null;
        }
    }

    /** Start the game */
    private void startArena() {
        //Get an arena
        SpleefArena selectedArena = enabledArenas.get(Util.getRandom().nextInt(enabledArenas.size()));
        selectedArena.setRandomFloor();
        currentArena = new SpleefArenaState(selectedArena);

        //Set player state
        setPlayersToState(PlayerState.LOBBY, PlayerState.PLAYING);

        //Start the startArena task
        startingTaskID = Schedule.runTimer(new Runnable() {

            private int time = 2;

            @Override
            public void run() {
                hMessagePlayers("Starting in " + time + "...");
                switch (time) {
                    case 2:
                        //Teleport the players
                        currentArena.teleport(getPlayers(PlayerState.PLAYING));
                        forEachPlayer(gp -> setSpectating(gp), PlayerState.SPECTATOR);
                        hMessagePlayers("Get ready...");
                        break;

                    case 1:
                        //TODO Enable specials (potions, etc)
                        hMessagePlayers("Set...");
                        break;

                    case 0:
                        //Cancel the task
                        Schedule.getScheduler().cancelTask(startingTaskID);
                        startingTaskID = null;

                        //Check if enough players
                        int playersLeft = countPlayers(PlayerState.PLAYING);
                        if (playersLeft <= 1) {
                            hMessagePlayers("Oh oh.. There are no longer enough players!");
                            gameState = GameState.FINISHED;
                            stopArena(true);
                            break;
                        } else {
                            hMessagePlayers("Spleef!");
                            gameState = GameState.PLAYING;

                            //Create the SpleefGame
                            currentArena.setSpleefGame(new SpleefGame(playersLeft));
                        }
                        break;
                }
                time--;
            }
        }, 20, 20);

        //Broadcast the startArena
        hMessagePlayers("Spleef is starting!");
    }

    /** Stop the arena */
    private void stopArena(boolean checkForStart) {
        for (GamePlayer<Spleef> player : getPlayers(PlayerState.LOBBY, PlayerState.PLAYING, PlayerState.GAME_OVER, PlayerState.SPECTATOR)) {
            //Set the new PlayerState
            if (player.getPlayerState() != PlayerState.SPECTATOR) {
                player.setPlayerState(PlayerState.LOBBY);
            }

            //No one is spectating anymore
            player.setSpectating(false);

            //Teleport the player back to the lobby
            player.teleport(getLobby(player));
            player.resetPlayer();
        }

        //Restore the arena
        currentArena.getArena().restoreFloor();
        currentArena = null;

        //Check if it should start again
        gameState = GameState.LOBBY;
        if (checkForStart) {
            if (shouldStartGame()) {
                countdown();
            }
        }
    }


    @Override
    public Location getSpectatorLocation(GamePlayer<Spleef> player) {
        if (currentArena != null) {
            return currentArena.getSpectatorLocation();
        }
        return null;
    }

    //<editor-fold desc="Events">
    /**
     * A player who is playing while the arena is running clicks a block
     * @param player The player
     * @param clickedBlock The block
     */
    public void onTouchedBlock(GamePlayer<Spleef> player, Block clickedBlock) {
        if (currentArena.containsBlock(clickedBlock)) {
            //Remove the block
            clickedBlock.setType(Material.AIR);
        }
    }

    /**
     * A player moves
     * @param player The player
     * @param to To location
     */
    public void onPlayerMove(GamePlayer<Spleef> player, Location to) {
        if (Util.containsLocation(currentArena.getDeathRegion(), to)) {
            //Player dies
            playerDies(player);
        }
    }
    //</editor-fold>

}
