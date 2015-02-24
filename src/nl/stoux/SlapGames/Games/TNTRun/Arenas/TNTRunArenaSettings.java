package nl.stoux.SlapGames.Games.TNTRun.Arenas;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Games.Base.Arena.BaseArenaSettings;
import nl.stoux.SlapGames.Settings.Errors.SettingError;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapGames.Storage.YamlFile;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Stoux on 26/01/2015.
 */
public class TNTRunArenaSettings extends BaseArenaSettings {

    /** All destroyable floors */
    @Getter private List<Setting<ProtectedRegion>> floors;

    /** The death region. When the player enters this region they will die */
    @Getter private Setting<ProtectedRegion> deathRegion;

    /** Allow players to double jump */
    @Getter private Setting<Boolean> doubleJump;

    /** The max number of double jumps */
    @Getter private Setting<Integer> maxDoubleJumps;

    /** The power of the double jump */
    @Getter private Setting<Integer> doubleJumpPower;

    public TNTRunArenaSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        super.initializeSettings();
        SettingGroup g = SettingGroup.BASE_ARENA;
        deathRegion = loadRegion("deathregion", "Death Region", "If a player enters this region they will be gameover", g);

        //Get the floors
        floors = new ArrayList<>();
        ConfigurationSection floorsSection = yamlFile.getConfig().getConfigurationSection("floors");
        if (floorsSection != null) {
            Set<String> keys = floorsSection.getKeys(false);
            if (keys != null) {
                keys.forEach(k ->  {
                    floors.add(loadRegion("floors." + k, "Floor", "A floor that will dissapear when ran over", g));
                });
            }
        }

        //Double jump
        doubleJump = load("doublejump", false, "Double Jump", "Allow players to double jump", g, true);
        maxDoubleJumps = load("maxdoublejumps", Integer.class, "Max Double Jumps", "The number of double jumps the player can do", g);
        doubleJumpPower = load ("doublejumppower", Integer.class, "Double Jump Power", "The power multiplier of the double jump (0-10)", g);
    }

    @Override
    protected void checkExceptions() {
        super.checkExceptions();
        checkSet(deathRegion);

        //Check the floors
        int validFloors = 0;
        for (Setting<ProtectedRegion> floor : floors) {
            if (floor.getValue() == null) {
                errors.add(new SettingError("Invalid Floor Region, key: " + floor.getKey(), false));
            } else {
                validFloors++;
            }
        }

        //Check if any valid floors
        if (validFloors == 0) {
            errors.add(new SettingError("No valid floors"));
        }

        //Check double jump
        if (doubleJump.getValue()) {
            checkSet(doubleJumpPower, maxDoubleJumps);

            //Check power
            Integer power = doubleJumpPower.getValue();
            if (power != null) {
                if (power < 0 || power > 10) {
                    errors.add(new SettingError("Double Jump Power needs to be between 0 and 10", true));
                }
            }
        }
    }
}
