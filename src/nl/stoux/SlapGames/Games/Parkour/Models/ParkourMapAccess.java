package nl.stoux.SlapGames.Games.Parkour.Models;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import nl.stoux.SlapGames.Games.Parkour.Maps.ParkourMap;
import nl.stoux.SlapGames.Games.Parkour.Maps.ParkourMapSettings;
import nl.stoux.SlapGames.Games.Parkour.ParkourPlayer;
import nl.stoux.SlapGames.Settings.Setting;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by Stoux on 26/01/2015.
 *
 * A class that contains objects and methods for quick access to certain info about a map.
 * This includes regions as well as basic settings for that map commonly used.
 *
 * This model needs to be 'refreshed' when the settings are updated.
 *
 */
public class ParkourMapAccess {

    /** The death height of the map */
    @Getter private int deathHeight;

    /**
     * A HashMap containing the name of the region to the action that should be taken
     * K: Region ID
     * V: A function that does a certain action based on the region
     */
    private HashMap<String, Consumer<ParkourPlayer>> regionToActions;

    /**
     * A HashMap containing the checkpoints by their number
     * K: The number
     * V: The checkpoint
     */
    private HashMap<Integer, ParkourMapSettings.Checkpoint> nrToCheckpoint;

    public ParkourMapAccess(final ParkourMap map) {
        regionToActions = new HashMap<>();
        nrToCheckpoint = new HashMap<>();
        ParkourMapSettings settings = map.getSettings();

        //The death value
        this.deathHeight = settings.getDeathHeight().getValue();

        //Death actions
        Consumer<ParkourPlayer> deathAction = map::playerDies;
        for (Setting<ProtectedRegion> protectedRegionSetting : settings.getDeathRegions()) {
            regionToActions.put(protectedRegionSetting.getValue().getId(), deathAction);
        }

        //Start & finish
        regionToActions.put(settings.getStartRegion().getValue().getId(), map::playerCrossesStart);
        regionToActions.put(settings.getEndRegion().getValue().getId(), map::playerCrossesFinish);

        //Checkpoints
        for (ParkourMapSettings.Checkpoint checkpoint : settings.getCheckpoints()) {
            final int cp = checkpoint.getNumber().getValue();
            regionToActions.put(
                    checkpoint.getCheckpointRegion().getValue().getId(),
                    pp -> map.playerPassesCheckpoint(pp, cp)
            );

            nrToCheckpoint.put(cp, checkpoint);
        }
    }

    /**
     * Get a Consumer action by it's region
     * @param region The region
     * @return The action or null
     */
    public Consumer<ParkourPlayer> getActionByRegion(String region) {
        return regionToActions.get(region.toLowerCase());
    }

    /**
     * Get a checkpoint by it's number
     * @param checkpointNumber The number
     * @return The checkpoint or null
     */
    public ParkourMapSettings.Checkpoint getCheckpoint(int checkpointNumber) {
        return nrToCheckpoint.get(checkpointNumber);
    }





}
