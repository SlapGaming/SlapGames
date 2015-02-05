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
@Table(name = "sg_spleef_games")
public class SpleefGame {

    @Column(name = "winner_id")
    private int winningUserID;
    @Column(name = "nr_of_players")
    private int nrOfPlayers;

    @Column(name = "start_time")
    private long startTime;

    @Column(name = "finish_time")
    private long finishTime;

    public SpleefGame(int nrOfPlayers) {
        this.nrOfPlayers = nrOfPlayers;
        this.startTime = System.currentTimeMillis();
    }

}
