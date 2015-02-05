package nl.stoux.SlapGames.Games.Parkour.Models;

import lombok.*;
import nl.stoux.SlapPlayers.SQL.Annotations.Column;
import nl.stoux.SlapPlayers.SQL.Annotations.Table;

/**
 * Created by Stoux on 25/01/2015.
 */
@Table(name = "sg_parkour_runs")
@NoArgsConstructor
public class ParkourRun extends StoredParkourRun {

    /** The relative start time */
    private long relativeStartTime;

    /** The timestamp that they finished this run */
    @Column(name = "finish_timestamp")
    @Getter private Long finishTimestamp = null;

    public ParkourRun(int userID, int mapID) {
        this.userID = userID;
        this.mapID = mapID;
        nrOfSessions = 1;
        relativeStartTime = startTimestamp = System.currentTimeMillis();
    }

    /** The player finished */
    public void finished() {
        this.finishTimestamp = System.currentTimeMillis();
        this.tookTime = finishTimestamp - relativeStartTime;
    }

    /**
     * Check if this run is finished
     * @return is finished
     */
    public boolean isFinished() {
        return finishTimestamp != null;
    }

    /**
     * Calculate how long the player has been running
     * @return the current took time
     */
    public long getCurrentTookTime() {
        return System.currentTimeMillis() - relativeStartTime;
    }

    /**
     * Change the ParkourRun into a StoredParkourRun
     * @return the stored run
     */
    public StoredParkourRun toStoredParkourRun() {
        return new StoredParkourRun(userID, mapID, startTimestamp, getCurrentTookTime(), lastCheckpoint, nrOfSessions);
    }

    /**
     * Continue a StoredParkourRun
     * @param storedRun The stored Parkour run
     * @return The new ParkourRun
     */
    public static ParkourRun continueStoredParkourRun(StoredParkourRun storedRun) {
        ParkourRun run = new ParkourRun(storedRun.getUserID(), storedRun.getMapID());
        run.startTimestamp = storedRun.getStartTimestamp();
        run.relativeStartTime = System.currentTimeMillis() - storedRun.tookTime;
        run.lastCheckpoint = storedRun.getLastCheckpoint();
        run.nrOfSessions = storedRun.getNrOfSessions() + 1;
        return run;
    }

}
