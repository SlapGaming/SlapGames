package nl.stoux.SlapGames.Games.Spleef;

import nl.stoux.SlapGames.Games.Base.BaseGameSettings;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapGames.Storage.YamlFile;

/**
 * Created by Stoux on 22/01/2015.
 */
public class SpleefSettings extends BaseGameSettings {

    /** The minimum number of players needed to start a game */
    private Setting<Integer> minimumPlayers;

    /** The number of seconds between games */
    private Setting<Integer> secondsBetweenGames;

    public SpleefSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        super.initializeSettings();
        SettingGroup g = SettingGroup.SPLEEF;
        minimumPlayers = load("minimumplayers", 2, "Minimum Players", "The mimimum amount of players to start a game", g, true);
        secondsBetweenGames = load("secondsbetweengames", 20, "Seconds between games", "The number of seconds before a game starts.", g, true);
    }

    /**
     * Get minimum number of players to play
     * @return number of players
     */
    public Integer getMinimumPlayers() {
        return minimumPlayers.getValue();
    }

    /**
     * Get the number of seconds before a new game should start
     * @return the seconds
     */
    public Integer getSecondsBetweenGames() {
        return secondsBetweenGames.getValue();
    }
}
