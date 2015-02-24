package nl.stoux.SlapGames.Settings;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created by Stoux on 23/01/2015.
 */
public enum SettingGroup {

    BASE("General"),
    BASE_ARENA("Arena"),

    SPLEEF("Spleef"),
    SPLEEF_ARENA("Arena"),
    SPLEEF_ARENA_SPECTATORS("Regions"),

    PARKOUR("Parkour"),
    PARKOUR_PADS("Parkour Pads"),
    PARKOUR_MAPS_INFO("Map Info"),
    PARKOUR_MAPS_CONTINUE("Continue"),
    PARKOUR_MAPS_CHECKPOINTS("Checkpoints"),
    PARKOUR_MAPS_DEATH("Game over"),

    SONIC("Sonic"),
    SONIC_CHECKPOINTS("Sonic Checkpoints"),
    SONIC_JUMPS("Sonic Jumps");

    /** The presentable name of the group */
    @Getter(AccessLevel.PUBLIC)
    private String presentableName;

    SettingGroup(String presentableName) {
        this.presentableName = presentableName;
    }
}
