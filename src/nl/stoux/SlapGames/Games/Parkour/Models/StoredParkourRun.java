package nl.stoux.SlapGames.Games.Parkour.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.stoux.SlapPlayers.SQL.Annotations.Column;
import nl.stoux.SlapPlayers.SQL.Annotations.Table;

/**
 * Created by Stoux on 25/01/2015.
 */
@Table(name = "sg_parkour_stored_runs")
@NoArgsConstructor
@AllArgsConstructor
public class StoredParkourRun {

    /** The player that did this run */
    @Column(name = "user_id")
    @Getter protected int userID;

    /** The ID of the map */
    @Column(name = "map_id")
    @Getter protected int mapID;

    /** The timestamp that they started this run */
    @Column(name = "start_timestamp")
    @Getter protected long startTimestamp;

    /** The time it took the player to finish the map */
    @Column(name = "took_time")
    @Getter @Setter protected long tookTime;

    /** The last checkpoint the player passed */
    @Getter @Setter protected int lastCheckpoint;

    /** The number of sessions. Each time the player leaves, rejoins & continues = +1 session */
    @Column(name = "nr_of_sessions")
    @Getter @Setter protected int nrOfSessions;

}
