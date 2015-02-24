package nl.stoux.SlapGames.Games.Base.Arena;

import lombok.Getter;
import nl.stoux.SlapGames.Settings.BaseSettings;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapGames.Storage.YamlFile;

/**
 * Created by Stoux on 21/02/2015.
 */
public abstract class BaseArenaSettings extends BaseSettings {

    /** The value of the arena */
    @Getter protected Setting<String> name;
    /** The builders of the map */
    @Getter protected Setting<String> buildBy;

    public BaseArenaSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        super.initializeSettings();
        SettingGroup g = SettingGroup.BASE; //TODO replace with BASE_ARENA
        //Load the value
        name = load("name", String.class, "Name", "The name of the arena", g);
        buildBy = load("buildby", String.class, "Build by", "The people who build this arena", g);
    }

    @Override
    protected void checkExceptions() {
        super.checkExceptions();
        checkSet(name, buildBy);
    }

}
