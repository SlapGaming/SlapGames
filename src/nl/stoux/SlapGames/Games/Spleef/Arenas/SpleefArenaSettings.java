package nl.stoux.SlapGames.Games.Spleef.Arenas;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Games.Base.Arena.BaseArenaSettings;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapGames.Settings.SetupSettings;
import nl.stoux.SlapGames.Storage.YamlFile;
import org.bukkit.Location;

/**
 * Created by Stoux on 22/01/2015.
 */
public class SpleefArenaSettings extends BaseArenaSettings {

    /** The Region containing all the blocks that will change. The players will spawn on top of these blocks */
    @Getter(AccessLevel.PUBLIC)
    private Setting<ProtectedRegion> region;
    /** The death region. When the player enters this region they will die */
    @Getter(AccessLevel.PUBLIC)
    private Setting<ProtectedRegion> deathRegion;

    public SpleefArenaSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        super.initializeSettings();
        SettingGroup g = SettingGroup.SPLEEF_ARENA;
        //Get the regions
        region = loadRegion("region", "Blocks Region", "The region that contains all the blocks the players will walk on", g);
        deathRegion = loadRegion("deathregion", "Death Region", "If a player enters this region they will be gameover", g);
    }

    @Override
    protected void checkExceptions() {
        super.checkExceptions();
        checkSet(region, deathRegion);
    }
}
