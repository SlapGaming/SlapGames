package nl.stoux.SlapGames.Games.Parkour.Maps;

import lombok.Getter;
import nl.stoux.SlapGames.Games.Parkour.Models.ParkourMapAccess;
import nl.stoux.SlapGames.Games.Parkour.Models.ParkourRun;
import nl.stoux.SlapGames.Games.Parkour.Models.StoredParkourRun;
import nl.stoux.SlapGames.Games.Parkour.Parkour;
import nl.stoux.SlapGames.Games.Parkour.ParkourPlayer;
import nl.stoux.SlapGames.Storage.YamlFile;
import nl.stoux.SlapGames.Util.Schedule;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Location;

/**
 * Created by Stoux on 24/01/2015.
 */
public class ParkourMap {

    /** Parkour */
    private Parkour game;

    /** The settings for this map */
    @Getter private ParkourMapSettings settings;

    /** The ParkourMapAccess that contains quick access to objects/values */
    @Getter private ParkourMapAccess parkourMapAccess;

    public ParkourMap(Parkour game, ParkourMapSettings settings) {
        this.game = game;
        this.settings = settings;
        refreshMapAccess();
    }

    /** Refresh the ParkourMapAccess object */
    public void refreshMapAccess() {
        parkourMapAccess = new ParkourMapAccess(this);
    }

    /**
     * A player leaves the game
     * @param gp The player
     */
    public void playerLeaves(ParkourPlayer gp) {
        //Check if running & if the map supports continue on rejoin
        if (gp.isRunning() && settings.getStoreCheckpointProgress().getValue()) {
            //Store the progress
            StoredParkourRun run = gp.getRun().toStoredParkourRun();
            Schedule.insertASync(run);
        }
    }

    /**
     * A player dies
     * This can be either hitting a death region or actually dead
     * @param gp The player
     */
    public void playerDies(ParkourPlayer gp) {
        //Ignore if not busy with a run
        if (gp.getRun() == null) {
            return;
        }

        //Check if actually dead or just 'dead'
        if (gp.getPlayer().isDead()) {
            //Reset run if needed and let the respawn event handle the rest
            if (!isOnCheckpoint(gp)) {
                gp.setRun(null);
            }
        } else {
            if (isOnCheckpoint(gp)) {
                game.hMessagePlayer(gp, "You have been teleported to checkpoint #" + gp.getRun().getLastCheckpoint() + "!");
            } else {
                gp.setRun(null);
                game.hMessagePlayer(gp, "You have been teleported to the start!");
            }
            gp.teleport(getRespawnLocation(gp));
        }

    }

    /**
     * Get the respawn location for a player
     * @param gp The player
     * @return the location
     */
    public Location getRespawnLocation(ParkourPlayer gp) {
        if (gp.getRun() == null || !isOnCheckpoint(gp)) {
            return settings.getLobby().getValue();
        } else {
            return parkourMapAccess.getCheckpoint(gp.getRun().getLastCheckpoint()).getRestartLocation().getValue();
        }
    }

    /**
     * A player crosses the start line/region
     * @param gp The player
     */
    public void playerCrossesStart(ParkourPlayer gp) {
        //Check if already running
        if (gp.isRunning()) {
            return;
        }

        //Start a new run
        ParkourRun run = new ParkourRun(Util.getUserID(gp), settings.getId().getValue());
        gp.setRun(run);
        gp.setStoredRun(null);
        game.hMessagePlayer(gp, "You started a new run!");
    }

    /**
     * A player crosses the finish line/region
     * @param gp The player
     */
    public void playerCrossesFinish(ParkourPlayer gp) {
        //Check if running
        if (!gp.isRunning()) {
            return;
        }

        //Finish the run
        ParkourRun run = gp.getRun();
        gp.setRun(null);
        run.finished();
        game.hMessagePlayers(gp.getPlayername() + " finished the run with a time of " + run.getTookTime()); //TODO Format time

        //Store the run
        Schedule.insertASync(run);
    }

    /**
     * A player passes a checkpoint
     * @param gp The player
     * @param checkpoint The checkpoint
     */
    public void playerPassesCheckpoint(ParkourPlayer gp, int checkpoint) {
        //Check if running & if the map supports checkpoints
        if (!gp.isRunning() || !settings.getAllowRestartOnCheckpoint().getValue()) {
            return;
        }

        //Check if the correct checkpoint was passed
        int lastPassed = gp.getRun().getLastCheckpoint();
        if (checkpoint != lastPassed + 1) {
            return;
        }

        //Set checkpoint
        ParkourRun run = gp.getRun();
        run.setLastCheckpoint(checkpoint);
        game.hMessagePlayer(gp, "Passed checkpoint #" + checkpoint + " (Time: " + run.getCurrentTookTime() + ")"); //TODO Format time
    }


    /**
     * Check if the player is on a checkpoint
     * Also checks if the map supports restarting on checkpoints
     * @param player The player
     * @return is on a checkpoint
     */
    private boolean isOnCheckpoint(ParkourPlayer player) {
        return settings.getAllowRestartOnCheckpoint().getValue() && player.getRun().getLastCheckpoint() > 0;
    }






}
