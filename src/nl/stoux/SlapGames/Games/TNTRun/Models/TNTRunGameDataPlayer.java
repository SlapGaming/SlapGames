package nl.stoux.SlapGames.Games.TNTRun.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.stoux.SlapPlayers.SQL.Annotations.Column;
import nl.stoux.SlapPlayers.SQL.Annotations.Table;

/**
 * Created by Stoux on 22/02/2015.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("sg_tntrun_games_played")
public class TNTRunGameDataPlayer {

    @Column(value = "user_id")
    private int userID;

    @Column("game_id")
    private int gameID;

}
