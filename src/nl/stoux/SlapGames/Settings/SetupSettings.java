package nl.stoux.SlapGames.Settings;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Settings.Errors.IfThisThenThatError;
import nl.stoux.SlapGames.Settings.Errors.MissingSettingError;
import nl.stoux.SlapGames.Settings.Errors.SettingError;
import nl.stoux.SlapGames.Storage.YamlFile;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Stoux on 22/01/2015.
 */
public abstract class SetupSettings extends Settings {

    /** This thing is fully setup */
    @Getter(AccessLevel.PUBLIC)
    protected Setting<Boolean> setup;
    /** This thing should be enabled */
    @Getter(AccessLevel.PUBLIC)
    protected Setting<Boolean> enabled;

    /** List with errors */
    protected List<SettingError> errors;

    public SetupSettings(YamlFile yamlFile) {
        super(yamlFile);
        errors = new ArrayList<>();
        checkExceptions();
    }

    @Override
    public void reloadSettings() {
        super.reloadSettings();
        errors = new ArrayList<>();
        checkExceptions();
    }

    /**
     * The settings are not complete or there is something wrong.
     * Sets enabled & setup to false. Saves the config.
     * The settings must have been initialized!
     */
    public void notSetup() {
        if (setup.getValue() || enabled.getValue()) {
            setup.setValue(false);
            enabled.setValue(false);
            set(setup, enabled);
        }
    }

    /**
     * Load a ProtectedRegion setting from the config that can be null
     * @param key The key in the config file
     * @param name The presentable name
     * @param description The description
     * @param group The SettingGroup
     * @return The setting or null
     */
    protected Setting<ProtectedRegion> loadRegion(String key, String name, String description, SettingGroup group) {
        //Load the region as String
        Setting<String> regionname = load(key, String.class, name, description, group);

        //Find the region
        ProtectedRegion region = (regionname.getValue() == null ? null : Util.getRegion(regionname.getValue()));
        return new Setting<>(region, key, name, description);
    }

    /**
     * Load a block setting.
     * @param key The key
     * @param name The name
     * @param description The description
     * @param group The SettingGroup
     * @param possibleTypes The possible types that block should be
     * @return the vector location of the block or null (if no location is given or incorrect type)
     */
    public Setting<Vector> loadBlock(String key, String name, String description, SettingGroup group, Material... possibleTypes) {
        //Get the vector
        Setting<Vector> vector = load(key, Vector.class, name, description, group);

        //Check if actually a button
        if (vector.getValue() != null) {
            if (!Util.isType(vector.getValue(), possibleTypes)) {
                vector.setValue(null);
            }
        }

        //Return the vector
        return vector;
    }

    /** Check if there are any errors/errors with the current setup */
    protected abstract void checkExceptions();

    /**
     * Check if a setting is set
     * If not, it will add a MissingSettingException to the exception list
     * @param settings The setting(s)
     * @return all set
     */
    protected boolean checkSet(Setting... settings) {
        boolean allSet = true;
        for (Setting setting : settings) {
            if (setting.getValue() == null) {
                errors.add(new MissingSettingError(setting));
                notSetup();
                allSet = false;
            }
        }
        return allSet;
    }

    /**
     * Check if a setting is set (and not False).
     * If that's the case, check if the requiredSettings are set.
     * If the requiredSettings are not set, it will add a IfThisThenThatException
     * @param baseSetting The base setting to be checked
     * @param requiredSettings The required settings if the base setting is set
     */
    protected void checkAllSet(Setting baseSetting, Setting... requiredSettings) {
        if (hasValue(baseSetting)) {
            //Find all settings without a value
            Setting[] nullSettings = (Setting[]) Arrays.stream(requiredSettings).filter(s -> s.getValue() == null).toArray();
            if (nullSettings.length > 0) {
                errors.add(IfThisThenThatError.ifThisThanThese(baseSetting, nullSettings));
                notSetup();
            }
        }
    }

    /**
     * Check if a setting is set (and not False).
     * If that's the case, check if one of the possible settings is set.
     * If none are set, it will add a IfThisThenThatException
     * @param baseSetting The base setting to be checked
     * @param possibleSettings The optional settings where (at least) one of them is required
     */
    protected void checkOneOfThemSet(Setting baseSetting, Setting... possibleSettings) {
        if (hasValue(baseSetting)) {
            //Find anys etting that has a value
            long withValue = Arrays.stream(possibleSettings).filter(s -> s.getValue() != null).count();
            if (withValue == 0) {
                //No settings with a value
                errors.add(IfThisThenThatError.ifThisThenOneOfThose(baseSetting, possibleSettings));
                notSetup();
            }

        }
    }

    /**
     * Check if a setting is not null & not false
     * @param setting The setting
     * @return has a value
     */
    private boolean hasValue(Setting setting) {
        return (setting.getValue() != null && !Boolean.FALSE.equals(setting.getValue()));
    }

}
