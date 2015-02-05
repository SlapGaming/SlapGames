package nl.stoux.SlapGames.Games.Parkour.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.stoux.SlapPlayers.SQL.Annotations.Column;
import nl.stoux.SlapPlayers.SQL.Annotations.Table;

/**
 * Created by Stoux on 25/01/2015.
 */
@Table(name = "sg_parkour_fails")
@NoArgsConstructor
@AllArgsConstructor
public class SavedParkourMap {

    /** The ID of the map, as specified in the file. DO NOT CHANGE THIS ONCE SET */
    @Column(name = "map_id")
    @Getter private int mapID;

    /** The filename, DO NOT CHANGE THIS ONCE SET */
    @Column(name = "filename")
    @Getter private String filename;

}
