package nl.stoux.SlapGames.Games.Base.Versus;

import nl.stoux.SlapGames.Games.Base.BaseEventHandler;
import nl.stoux.SlapGames.Games.Base.BaseGame;
import nl.stoux.SlapGames.Games.Base.GameState;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.Util.Schedule;

/**
 * Created by Stoux on 22/02/2015.
 */
public abstract class BaseVersusGame<Handler extends BaseEventHandler, GP extends GamePlayer, GameSettings extends BaseVersusGameSettings> extends BaseGame<Handler, GP, GameSettings> {

    /** The number of seconds the game counts down while in STARTING gamestate */
    private int startingCountdownSeconds;

    /** The ID of the countdown task */
    private Integer countdownTaskID;
    /** The ID of the starting task */
    private Integer startingTaskID;

    public BaseVersusGame(GameType gameType, int startingCountdownSeconds) {
        super(gameType);
        this.startingCountdownSeconds = startingCountdownSeconds;
    }

    //<editor-fold desc="Starting methods">
    @Override
    protected void newPlayerJoins(GP player) {
        switch (gameState) {
            case PLAYING: case STARTING:
                //Game is currently playing | TODO: What if Spectating is not allowed?
                player.setPlayerState(PlayerState.LOBBY);
                setSpectating(player);
                break;

            case FINISHED:
                //Game just finished
                setPlayerInLobby(player);
                break;

            case LOBBY:
                //Players are in the lobby
                setPlayerInLobby(player);
                if (countdownTaskID != null) {
                    //Already counting down
                } else if (shouldStartGame()) {
                    startCountdown(true);
                } else {
                    int currentPlayers = getPlayers(PlayerState.LOBBY).size();
                    int stillNeeded = settings.getMinimumPlayers().getValue() - currentPlayers;
                    hMessagePlayers("There " + (stillNeeded == 1 ? "is" : "are") + " " + stillNeeded + " more player" + (stillNeeded == 1 ? " " : "s ") + "needed to start a game!");
                }
                break;
        }
    }

    @Override
    protected void newPlayerSpectates(GP player) {
        switch (gameState) {
            case PLAYING: case STARTING:
                player.setPlayerState(PlayerState.SPECTATOR);
                setSpectating(player);
                break;

            default:
                setPlayerInLobby(player, PlayerState.SPECTATOR);
                break;
        }
    }

    @Override
    protected void spectatorLeaves(GP player) {

    }

    /**
     * Check if a game should startArena
     * @return should startArena
     */
    public boolean shouldStartGame() {
        return getPlayers(PlayerState.GAME_OVER, PlayerState.LOBBY, PlayerState.PLAYING).size() >= settings.getMinimumPlayers().getValue();
    }

    /** Start a countdown to start a new VersusGame */
    public void startCountdown(boolean first) {
        //Check howmany seconds till start
        int secondsTillStart = settings.getSecondsBetweenGames().getValue();
        Integer startDelay = settings.getStartDelay().getValue();
        if (first && startDelay != null) {
            secondsTillStart = startDelay;
        }

        //Start the countdown
        countdownTaskID = Schedule.runLater(() -> {
            //Check if still enough players available
            if (shouldStartGame()) {
                //Start the game
                startVersusGame();
            } else {
                hMessagePlayers("Oh oh.. There are no longer enough players!");
            }
            countdownTaskID = null;
        }, secondsTillStart);

        //Notify players of the game starting
        broadcast(gameType.getPresentableName() + " is starting in " + secondsTillStart + " second" + (secondsTillStart == 1 ? "" : "s") + "!"); //TODO Parse with minutes + seconds
    }

    /** Setup a new versus game */
    protected abstract void setupVersusGame();

    /** Start a new versus game */
    private void startVersusGame() {
        //Start the game
        gameState = GameState.STARTING;
        setPlayersToState(PlayerState.LOBBY, PlayerState.PLAYING);
        setupVersusGame();

        //Start the delay
        startingTaskID = Schedule.runTimer(new Runnable() {
            private int time = startingCountdownSeconds;
            @Override
            public void run() {
                if (time == 0) {
                    //Cancel the task
                    Schedule.getScheduler().cancelTask(startingTaskID);
                    startingTaskID = null;

                    //Check if enough players
                    int playersLeft = countPlayers(PlayerState.PLAYING);
                    if (playersLeft <= 1) {
                        hMessagePlayers("Oh oh.. There are no longer enough players!");
                        gameState = GameState.FINISHED;
                        stopVersusGame();
                    } else {
                        //Start the game
                        gameState = GameState.PLAYING;
                        startingIn(time);

                    }
                } else {
                    startingIn(time);
                }
                time--;
            }
        }, 0, 20);
    }

    /**
     * The number of seconds left till the game starts
     * @param secondsLeft
     */
    protected abstract void startingIn(int secondsLeft);

    /**
     * Force start a game
     * @return force started
     */
    public boolean forceStart() {
        if (gameState == GameState.LOBBY) {
            if (countdownTaskID != null) {
                Schedule.getScheduler().cancelTask(countdownTaskID);
                countdownTaskID = null;
            }

            if (countPlayers(PlayerState.LOBBY) > 1) {
                startVersusGame();
                return true;
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Ending methods">
    @Override
    protected void playerLeaves(GP player) {
        //Check if the game ended if the player was playing
        if (player.getPlayerState() == PlayerState.PLAYING) {
            player.setPlayerState(PlayerState.LEAVING);
            checkGameEnded();
        }
    }

    /**
     * A player has lost the game
     * @param player the player
     */
    public void playerLost(GP player) {
        //Set state
        player.setPlayerState(PlayerState.GAME_OVER);

        //Check if a player has won
        if (!checkGameEnded()) {
            //Still playing, set into spectating
            setSpectating(player);
        }
    }

    /**
     * Check if the game ended
     * @return ended
     */
    private boolean checkGameEnded() {
        //Check howmany players are still playing
        int playersLeft = countPlayers(PlayerState.PLAYING);
        if (playersLeft <= 0) {
            //Something went wrong
            hMessagePlayers("No players are left... huh.");
            stopVersusGame();
        } else if (playersLeft == 1) {
            //A player has won
            GP winner = players.stream().filter(gp -> gp.getPlayerState() == PlayerState.PLAYING).findFirst().get();
            playerWon(winner);

            //Stop the game
            hMessagePlayers(winner.getPlayername() + " has won!");
            stopVersusGame();
        } else {
            //Still people playing
            return false;
        }

        //Schedule restart task
        Schedule.runLater(() -> {
            if (countdownTaskID != null && gameState == GameState.LOBBY) {
                if (shouldStartGame()) {
                    startCountdown(false);
                }
            }
        }, 20);

        //Game ended
        return true;
    }

    /**
     * A player has won the game
     * DO NOT stop the game in this method
     * DO NOT broadcast the winner
     * @param player the player
     */
    protected abstract void playerWon(GP player);

    /** Stop the current versus game */
    protected void stopVersusGame() {
        for (GP player : getPlayers(PlayerState.LOBBY, PlayerState.PLAYING, PlayerState.GAME_OVER, PlayerState.SPECTATOR)) {
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
    }
    //</editor-fold>

}
