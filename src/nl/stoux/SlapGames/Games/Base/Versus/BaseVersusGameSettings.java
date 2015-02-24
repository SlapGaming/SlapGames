package nl.stoux.SlapGames.Games.Base.Versus;

import lombok.Getter;
import nl.stoux.SlapGames.Games.Base.BaseGameSettings;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapGames.Storage.YamlFile;

/**
 * Created by Stoux on 22/02/2015.
 */
public class BaseVersusGameSettings extends BaseGameSettings {

    /** The minimum number of players needed to start a game */
    @Getter private Setting<Integer> minimumPlayers;

    /** The delay before starting the first game */
    @Getter private Setting<Integer> startDelay;

    /** The number of seconds between a game */
    @Getter private Setting<Integer> secondsBetweenGames;

    public BaseVersusGameSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        super.initializeSettings();
        SettingGroup g = SettingGroup.BASE;
        minimumPlayers = load("minimumplayers", 2, "Minimum Players", "The mimimum amount of players to start a game", g, true);
        startDelay = load("startdelay", 30, "Start Delay", "The number of seconds before the first game starts", g, true);
        secondsBetweenGames = load("secondsbetweengames", 20, "Seconds between games", "The number of seconds before another game starts", g, true);
    }

}
