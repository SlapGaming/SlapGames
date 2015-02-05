package nl.stoux.SlapGames.Games.Sonic;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Games.Base.BaseGameSettings;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapGames.Storage.YamlFile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.function.Function;

/**
 * Created by Stoux on 23/01/2015.
 */
public class SonicSettings extends BaseGameSettings {

    /** The spawn location of the racetrack */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Location> racetrack;
    /** The spawn location for the tutorial */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Location> tutorial;

    /** The button that teleports the player to the racetrack */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Vector> spawnToRaceButton;
    /** The button that teleports the player to the tutorial */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Vector> spawnToTutorialButton;
    /** The button that teleports the player to the racetrack */
    @Getter(AccessLevel.PUBLIC)
    private Setting<Vector> tutorialToRaceButton;

    /** The start & finish line region */
    @Getter(AccessLevel.PUBLIC)
    private Setting<ProtectedRegion> startFinishLine;
    /** The checkpoints; 5 in total */
    @Getter(AccessLevel.PUBLIC)
    private Setting<ProtectedRegion>[] checkpoints;
    /** The jumps; 5 in total */
    @Getter(AccessLevel.PUBLIC)
    private Setting<ProtectedRegion>[] jumps;

    public SonicSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        super.initializeSettings();

        SettingGroup g = SettingGroup.SONIC;
        //Get locations
        racetrack = load("racetrack", Location.class, "Racetrack Spawn", "The spawn location for the racetrack", g);
        tutorial = load("tutorial", Location.class, "Tutorial Spawn", "The spawn location for the tutorial", g);

        //Buttons
        Material[] buttons = new Material[]{Material.STONE_BUTTON, Material.WOOD_BUTTON};
        spawnToRaceButton = loadBlock("spawntorace", "Spawn to race", "The location of the spawn to race button", g, buttons);
        spawnToTutorialButton = loadBlock("spawntotutorial", "Spawn to tutorial", "The location of the spawn to tutorial button", g, buttons);
        tutorialToRaceButton = loadBlock("tutorialtorace", "Tutorial to race", "The location of the tutorial to race button", g, buttons);

        //Get regions
        startFinishLine = loadRegion("startfinishline", "Start/Finish line", "The region of the start & finish line", g);
        checkpoints = loadRegions("checkpoint", SettingGroup.SONIC_CHECKPOINTS, i -> "Checkpoint " + i, i -> "The region of checkpoint " + i);
        jumps = loadRegions("jump", SettingGroup.SONIC_JUMPS, i -> "Jump " + i, i -> "The region for jump " + i);
    }

    /**
     * Load multiple regions by their base key
     * @param baseKey The base key
     * @param nameFunction The function that will create the name
     * @param descriptionFunction The function that will create the description
     * @return the array with region settings
     */
    @SuppressWarnings("unchecked")
    private Setting<ProtectedRegion>[] loadRegions(String baseKey, SettingGroup group, Function<Integer, String> nameFunction, Function<Integer, String> descriptionFunction) {
        Setting<ProtectedRegion>[] regions = new Setting[5];
        for (int i = 1; i <= 5; i++) {
            //Load the region
            regions[i - 1] = loadRegion(baseKey + i, nameFunction.apply(i), descriptionFunction.apply(i), group);
        }
        return regions;
    }

    @Override
    protected void checkExceptions() {
        super.checkExceptions();
        checkSet(racetrack, tutorial, spawnToRaceButton, spawnToTutorialButton, tutorialToRaceButton, startFinishLine);
        checkSet(checkpoints);
        checkSet(jumps);
    }
}
