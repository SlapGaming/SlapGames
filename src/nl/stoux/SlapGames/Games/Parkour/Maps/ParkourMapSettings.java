package nl.stoux.SlapGames.Games.Parkour.Maps;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.SlapGames.Settings.BaseSettings;
import nl.stoux.SlapGames.Settings.Errors.SettingError;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapGames.Storage.YamlFile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by Stoux on 24/01/2015.
 */
public class ParkourMapSettings extends BaseSettings {

    //General info
    /** The ID. This shouldn't be changed */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Integer> id;
    /** The name */
    @Getter(AccessLevel.PUBLIC)
    private Setting<String> name;
    /** The author */
    @Getter(AccessLevel.PUBLIC)
    private Setting<String> author;

    //Continue settings
    /** Allow players to restart at a checkpoint */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Boolean> allowRestartOnCheckpoint;
    /** Allow players to continue their progress after leaving the map */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Boolean> storeCheckpointProgress;
    /** The button that continues a previous run */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Vector> continueButton;
    /** The button that resets a previous run */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Vector> resetButton;

    //Regions
    /** The start region */
    @Getter(AccessLevel.PUBLIC)
    private Setting<ProtectedRegion> startRegion;
    /** The end region */
    @Getter(AccessLevel.PUBLIC)
    private Setting<ProtectedRegion> endRegion;

    /** All checkpoints on this map */
    @Getter private List<Checkpoint> checkpoints;


    //Death settings
    /** Below this Y-height the player will die */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Integer> deathHeight;

    /** List of regions where the player will die if they go into that region */
    @Getter(AccessLevel.PUBLIC)
    private List<Setting<ProtectedRegion>> deathRegions;

    public ParkourMapSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        super.initializeSettings();

        SettingGroup g = SettingGroup.PARKOUR_MAPS_INFO;
        id = load("id", Integer.class, "Map ID", "The ID of the map. Don't change after being set!", g);
        name = load("name", String.class, "Name", "The map name", g);
        author = load("author", String.class, "Author", "The author(s) of this map", g);

        g = SettingGroup.PARKOUR_MAPS_CONTINUE;
        allowRestartOnCheckpoint = load("allowrestartoncheckpoint", false, "Allow restart on CP", "Allow the player to continue the map on their previous checkpoint after dying", g, true);
        storeCheckpointProgress = load("storecheckpointprogress", false, "Store CP progress", "The player can continue the map at their last checkpoint after leaving the map", g, true);
        Material[] buttons = new Material[]{Material.STONE_BUTTON, Material.WOOD_BUTTON};
        continueButton = loadBlock("continuebutton", "Continue Button", "The button that allows the player to continue the run after leaving", g, buttons);
        resetButton = loadBlock("resetbutton", "Reset Button", "The button that resets the player's progress", g, buttons);

        g = SettingGroup.PARKOUR_MAPS_CHECKPOINTS;
        startRegion = loadRegion("startregion", "Start Region", "The starting line", g);
        endRegion = loadRegion("endregion", "End Region", "The finish line", g);

        //Find checkpoints
        String cpBase = "checkpoints";
        checkpoints = new ArrayList<>();
        if (config.contains(cpBase)) {
            ConfigurationSection section = config.getConfigurationSection(cpBase);
            section.getKeys(false).forEach(key -> checkpoints.add(initializeCheckpoint("checkpoints", key)));
        }

        g = SettingGroup.PARKOUR_MAPS_DEATH;
        deathHeight = load("deathheight", 0, "Death Height", "The player dies if they go under this Y-Height", g, true);

        //Find death regions
        String dRegions = "deathregions";
        deathRegions = new ArrayList<>();
        if (config.contains(dRegions)) {
            ConfigurationSection section = config.getConfigurationSection(dRegions);
            section.getKeys(false).forEach(key -> loadRegion(dRegions + "." + key, "Death Region", "The player will die if they hit this region", SettingGroup.PARKOUR_MAPS_DEATH));
        }
    }

    @Override
    protected void checkExceptions() {
        super.checkExceptions();

        //General settings are required
        checkSet(id, name, author, startRegion, endRegion);
        //If players are allowed to restart on checkpoint, the buttons need to be set
        checkAllSet(allowRestartOnCheckpoint, resetButton);
        checkAllSet(storeCheckpointProgress, continueButton);

        //Check checkpoints
        boolean allValidCheckpoints = checkpoints.stream().filter(c -> !c.isValid()).count() == 0;
        if (allValidCheckpoints) {
            //Check order
            Collections.sort(checkpoints);
            for (int i = 1; i <= checkpoints.size(); i++) {
                Integer nr = checkpoints.get(i - 1).getNumber().getValue();
                if (i != nr) {
                    //The order is incorrect
                    errors.add(new SettingError("The checkpoint order is wrong! Expected: " + i + ", Found: " + nr));
                    notSetup();
                    break;
                }
            }
        } else {
            errors.add(new SettingError("A checkpoint was incorrectly setup", true));
            notSetup();
        }

        //Check DeathRegions
        long invalidDeathRegions = deathRegions.stream().filter(d -> d.getValue() == null).count();
        if (invalidDeathRegions > 0) {
            errors.add(new SettingError("Found " + invalidDeathRegions + " invalid DeathRegions", false));
        }

        //Checkpoints are being stored while you can't restart on them
        if (!allowRestartOnCheckpoint.getValue() && storeCheckpointProgress.getValue()) {
            errors.add(new SettingError("Checkpoint progress is being stored while the player can't continue on checkpoints", false));
        }
        //Can restart on checkpoint, but there are no checkpoints
        if (allowRestartOnCheckpoint.getValue() && checkpoints.isEmpty()) {
            errors.add(new SettingError("Players can restart on checkpoints, but there are no checkpoints.", false));
        }
    }

    public class Checkpoint implements Comparable<Checkpoint> {

        /** The checkpoint number */
        @Getter private Setting<Integer> number;

        /** The restart location when the player dies */
        @Getter private Setting<Location> restartLocation;

        /** The region that triggers this checkpoint */
        @Getter private Setting<ProtectedRegion> checkpointRegion;

        @Override
        public int compareTo(Checkpoint o) {
            //TODO Order is untested
            return Integer.compare(number.getValue(), o.getNumber().getValue());
        }

        /** Check if the checkpoint is valid */
        private boolean isValid() {
            return (number.getValue() != null && restartLocation.getValue() != null && checkpointRegion.getValue() != null);
        }
    }

    /**
     * Create a checkpoint
     * @param base The base key (eg. checkpoints)
     * @param key The key
     * @return The checkpoint
     */
    private Checkpoint initializeCheckpoint(String base, String key) {
        String baseKey = base + "." + key + ".";
        SettingGroup g = SettingGroup.PARKOUR_MAPS_CHECKPOINTS;

        //Create the checkpoint
        Checkpoint cp = new Checkpoint();
        cp.number = load(baseKey + "nr", Integer.class, "Number", "The checkpoint number (starts @ 1)", g);
        cp.restartLocation = load(baseKey + "restart", Location.class, "Restart Location", "The player will spawn here after dying", g);
        cp.checkpointRegion = loadRegion(baseKey + "region", "Region", "The region that triggers the checkpoint", g);

        //Return the cp
        return cp;
    }

}
