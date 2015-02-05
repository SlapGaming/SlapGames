package nl.stoux.SlapGames.Games.Spleef.Arenas;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapGames.Settings.SetupSettings;
import nl.stoux.SlapGames.Storage.YamlFile;
import org.bukkit.Location;

/**
 * Created by Stoux on 22/01/2015.
 */
public class SpleefArenaSettings extends SetupSettings {

    /** The name of the arena */
    @Getter(AccessLevel.PUBLIC)
    private Setting<String> name;
    /** The builders of the map */
    @Getter(AccessLevel.PUBLIC)
    private Setting<String> buildBy;

    /** The Region containing all the blocks that will change. The players will spawn on top of these blocks */
    @Getter(AccessLevel.PUBLIC)
    private Setting<ProtectedRegion> region;
    /** The death region. When the player enters this region they will die */
    @Getter(AccessLevel.PUBLIC)
    private Setting<ProtectedRegion> deathRegion;

    /** This map supports spectators */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Boolean> spectators;
    /** The Spectator Location */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Location> spectatorLocation;
    /** Players are allowed to go into Spectator GameMode */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Boolean> spectatorGameMode;
    /** The Spectator out of bounds region. If this is not set they will not be able to fly */
    @Getter(AccessLevel.PUBLIC)
    private Setting<ProtectedRegion> spectatorRegion;

    public SpleefArenaSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        SettingGroup g = SettingGroup.SPLEEF_ARENA;
        setup = load("setup", false, "Setup", "The arena is fully setup", g, true);
        enabled = load("enabled", false, "Enabled", "The arena should be used", g, true);

        //Load the name
        name = load("name", String.class, "Name", "The name of the arena", g);
        buildBy = load("buildby", String.class, "Build by", "The people who build this arena", g);

        //Get the regions
        region = loadRegion("region", "Blocks Region", "The region that contains all the blocks the players will walk on", g);
        deathRegion = loadRegion("deathregion", "Death Region", "If a player enters this region they will be gameover", g);

        //Spectator settings
        SettingGroup s = SettingGroup.SPLEEF_ARENA_SPECTATORS;
        spectators = load("spectators", false, "Spectators", "The arena supports spectators", s, true);
        spectatorLocation = load("spectatorlocation", Location.class, "Spectator Location", "The spawn location for spectators", s);

        //=> GameMode.SPECTATOR
        spectatorGameMode = load("spectatorgamemode", false, "Spectator GameMode", "Spectating players will be put into Spectator GameMode", s, true);
        spectatorRegion = loadRegion("spectatorregion", "Spectator Region", "The out of bounds region for spectators", s);
    }

    @Override
    protected void checkExceptions() {
        checkSet(name, buildBy, region, deathRegion);
        if (spectators.getValue()) {
            //Spectator spawn is needed
            checkAllSet(spectators, spectatorLocation);
            //If spectators can go into GameMode.SPECTATOR an out of bounds region is needed
            checkAllSet(spectatorGameMode, spectatorRegion);
        }
    }
}
