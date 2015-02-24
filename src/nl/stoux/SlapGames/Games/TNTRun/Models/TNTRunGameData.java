package nl.stoux.SlapGames.Games.TNTRun.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.stoux.SlapPlayers.SQL.Annotations.Column;
import nl.stoux.SlapPlayers.SQL.Annotations.Table;

/**
 * Created by Stoux on 23/01/2015.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "sg_tntrun_games")
public class TNTRunGameData {

    @Column(value = "id", autoIncrementID = true)
    private int gameID;

    @Column(value = "winner_id")
    private int winningUserID;

    @Column(value = "nr_of_players")
    private int nrOfPlayers;

    @Column(value = "start_time")
    private long startTime;

    @Column(value = "finish_time")
    private long finishTime;

    public TNTRunGameData(int nrOfPlayers) {
        this.nrOfPlayers = nrOfPlayers;
        this.startTime = System.currentTimeMillis();
    }

}
