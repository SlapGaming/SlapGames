package nl.stoux.SlapGames.Games.Spleef.Models;

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
@Table("sg_spleef_games")
public class SpleefGameData {

    @Column(value = "id", autoIncrementID = true)
    private int gameID;

    @Column("winner_id")
    private int winningUserID;

    @Column("nr_of_players")
    private int nrOfPlayers;

    @Column("start_time")
    private long startTime;

    @Column("finish_time")
    private long finishTime;

    public SpleefGameData(int nrOfPlayers) {
        this.nrOfPlayers = nrOfPlayers;
        this.startTime = System.currentTimeMillis();
    }

}
