package nl.stoux.SlapGames.Games.Sonic.Models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.stoux.SlapPlayers.SQL.Annotations.Column;
import nl.stoux.SlapPlayers.SQL.Annotations.Table;

/**
 * Created by Stoux on 23/01/2015.
 */
@NoArgsConstructor
@Table("sg_sonic_leaderboard")
public class SonicRun {

    /** The user ID in the DB */
    @Getter(AccessLevel.PUBLIC)
    @Column("user_id")
    private int userID;

    /** The timestamp of starting */
    @Getter(AccessLevel.PUBLIC)
    private long startTimestamp;

    /** The timestamp of finishing */
    @Getter(AccessLevel.PUBLIC)
    @Column("finish_timestamp")
    private long finishTimestamp;

    /** The total time it took to finish the run */
    @Getter(AccessLevel.PUBLIC)
    @Column("finish")
    private int totalTime;

    @Getter(AccessLevel.PUBLIC)
    private int lastCheckpoint = 0;
    //The checkpoints
    @Column private Integer cp1;
    @Column private Integer cp2;
    @Column private Integer cp3;
    @Column private Integer cp4;
    @Column private Integer cp5;

    //The jumps
    @Getter(AccessLevel.PUBLIC)
    private int lastJump = 0;
    @Column private Integer j1;
    @Column private Integer j2;
    @Column private Integer j3;
    @Column private Integer j4;
    @Column private Integer j5;

    public SonicRun(int userID) {
        this.userID = userID;
    }

    /**
     * Set a checkpoint time
     * @param checkpoint the checkpoint
     */
    public void passedCheckpoint(int checkpoint) {
        int timePassed = (int) (System.currentTimeMillis() - startTimestamp);
        switch (checkpoint){
            case 1: cp1 = timePassed; break;
            case 2: cp2 = timePassed; break;
            case 3: cp3 = timePassed; break;
            case 4: cp4 = timePassed; break;
            case 5: cp5 = timePassed; break;
        }
        lastCheckpoint = checkpoint;
    }

    /**
     * Set the jump time
     * @param jump The jump
     */
    public void passedJump(int jump) {
        int timePassed = (int) (System.currentTimeMillis() - startTimestamp);
        switch (jump){
            case 1: j1 = timePassed; break;
            case 2: j2 = timePassed; break;
            case 3: j3 = timePassed; break;
            case 4: j4 = timePassed; break;
            case 5: j5 = timePassed; break;
        }
        lastJump = jump;
    }

    /** The run is finished */
    public void finished(){
        finishTimestamp = System.currentTimeMillis();
        totalTime = (int) (finishTimestamp - startTimestamp);
    }

}
