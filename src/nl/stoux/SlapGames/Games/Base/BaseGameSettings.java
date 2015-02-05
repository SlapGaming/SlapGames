package nl.stoux.SlapGames.Games.Base;

import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Settings.BaseSettings;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapGames.Storage.YamlFile;
import org.bukkit.ChatColor;

/**
 * Created by Stoux on 22/01/2015.
 */
public class BaseGameSettings extends BaseSettings {

    /** The prefix that will be broadcasted for this game */
    @Getter(AccessLevel.PUBLIC)
    protected Setting<String> prefix;
    /** The prefix colored */
    @Getter(AccessLevel.PUBLIC)
    protected String coloredPrefix;

    public BaseGameSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        super.initializeSettings();

        //Get the prefix
        prefix = load("prefix", String.class, "Prefix", "The prefix shown before broadcasts of this game", SettingGroup.BASE);
        coloredPrefix = (prefix.getValue() == null ? "" :  ChatColor.translateAlternateColorCodes('&', prefix.getValue()));
    }

    @Override
    protected void checkExceptions() {
        super.checkExceptions();
        checkSet(prefix);
    }
}
