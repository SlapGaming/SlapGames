package nl.stoux.SlapGames.Settings;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Storage.YamlFile;
import org.bukkit.Location;

/**
 * Created by Stoux on 24/01/2015.
 */
public abstract class BaseSettings extends SetupSettings {

    /** The lobby of the game */
    @Getter(AccessLevel.PUBLIC)
    protected Setting<Location> lobby;

    /** The name of the out of bounds region */
    @Getter(AccessLevel.PUBLIC)
    protected Setting<ProtectedRegion> outOfBoundsRegion;

    /** Supports spectators */
    @Getter(AccessLevel.PUBLIC)
    protected Setting<Boolean> spectators;
    /** The spectator spawn location */
    @Getter(AccessLevel.PUBLIC)
    protected Setting<Location> spectatorLocation;
    /** The Spectator out of bounds region */
    @Getter(AccessLevel.PUBLIC)
    protected Setting<ProtectedRegion> specOutOfBoundsRegion;
    /** The spectators are allowed to go into GameMode.SPECTATOR */
    @Getter(AccessLevel.PUBLIC)
    protected Setting<Boolean> spectatorGameMode;
    /** The GameMode.SPECTATOR spectators are allowed to no clip through blocks */
    @Getter protected Setting<Boolean> spectatorGameModeNoClip;

    public BaseSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        SettingGroup g = SettingGroup.BASE;
        setup = load("setup", false, "Setup", "The game has been setup", g, true);
        enabled = load("enabled", false, "Enabled", "The game should be enabled", g, true);

        //Get the Lobby location
        lobby = load("lobby", Location.class, "Lobby", "The location of the lobby", g);
        outOfBoundsRegion = loadRegion("outofboundsregion", "Out of bounds region", "The name of the region for the out of bounds area", g);

        //Check if spectators are enabled
        spectators = load("spectators", false, "Allow Spectators", "This game supports spectators", g, true);
        specOutOfBoundsRegion = loadRegion("spectatorsoutofboundsregion", "Spectator out of bounds region", "The name of the region for the spectators out of bounds area", g);
        spectatorLocation = load("spectatorlocation", Location.class, "Spectator Spawn", "The spectator spawn location", g);
        spectatorGameMode = load("spectatorgamemode", false, "Spectator Gamemode", "Spectators will be forced into Spectator GameMode", g, true);
        spectatorGameModeNoClip = load("spectatorgamemodenoclip", false, "Spectator No Clip", "GameMode spectators are allowed to 'no clip' through walls", g, true);
    }

    @Override
    protected void checkExceptions() {
        //Lobby is always needed
        checkSet(lobby);

        //If spectators are allowed
        if (spectators.getValue()) {
            //Spectator spawn is needed
            checkAllSet(spectators, spectatorLocation);
            //If the players are allowed to into GameMode.SPECTATOR they needs an out of bounds region
            checkOneOfThemSet(spectatorGameMode, specOutOfBoundsRegion, outOfBoundsRegion);
        }
    }
}
