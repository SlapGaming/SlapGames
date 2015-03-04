package nl.stoux.SlapGames.Settings;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created by Stoux on 23/01/2015.
 */
public enum SettingGroup {

    //General
    GENERAL("General"),
    LOCATIONS("Locations/Regions"),
    SPECTATORS("Spectators"),
    VERSUS("Versus"),
    ARENAS("Arenas"),
    ARENA("Arena"),

    //Sonic
    SONIC("Sonic"),
    SONIC_CHECKPOINTS("Checkpoints"),
    SONIC_JUMPS("Jumps"),

    //TNTRun
    TNTRUN_FLOORS("Floors"),


    //Parkour
    PARKOUR_PADS("Teleport Pads"),
    PARKOUR_MAPS("Maps"),

    //=> Parkour Map
    PARKOUR_MAP_INFO("Info"),
    PARKOUR_MAP_CONTINUE("Continue"),
    PARKOUR_MAP_CHECKPOINTS("Checkpoints"),
    PARKOUR_MAP_DEATH("Death");



    ;

    /** The presentable name of the group */
    @Getter(AccessLevel.PUBLIC)
    private String presentableName;

    SettingGroup(String presentableName) {
        this.presentableName = presentableName;
    }
}
