package nl.stoux.SlapGames.Settings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Stoux on 22/01/2015.
 */
public class Setting<T> {

    /** The key in the config */
    @Getter(AccessLevel.PUBLIC)
    private String key;
    /** The value of the Setting */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private T value;

    /** The presentable name */
    @Getter(AccessLevel.PUBLIC)
    private String name;
    /** A short description about the Setting */
    @Getter(AccessLevel.PUBLIC)
    private String description;

    /** The group of settings this setting belongs in */
    @Getter(AccessLevel.PUBLIC)
    private SettingGroup settingGroup;

    public Setting(T value, String key, String name, String description) {
        this.value = value;
        this.key = key;
        this.name = name;
        this.description = description;
        this.settingGroup = SettingGroup.BASE;
    }

    public Setting(T value, String key, String name, String description, SettingGroup settingGroup) {
        this.value = value;
        this.key = key;
        this.name = name;
        this.description = description;
        this.settingGroup = settingGroup;
    }
}
