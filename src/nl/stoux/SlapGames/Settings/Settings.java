package nl.stoux.SlapGames.Settings;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.stoux.SlapGames.Storage.YamlFile;
import nl.stoux.SlapGames.Util.Log;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Stoux on 22/01/2015.
 */
public abstract class Settings {

    /** The YAML file */
    protected YamlFile yamlFile;
    /** The config in the YAML file */
    protected FileConfiguration config;

    public Settings(YamlFile yamlFile) {
        this.yamlFile = yamlFile;
        config = yamlFile.getConfig();
        initializeSettings();
    }

    /** Reload the settings */
    public void reloadSettings() {
        //Reload the file
        yamlFile.reloadConfig();
        config = yamlFile.getConfig();

        //Wipe the current values
        forSettingFields(f -> setFieldObject(f, null));

        //Initialize the settings again
        initializeSettings();
    }

    /**
     * Get all settings
     * @return The settings
     */
    public List<Setting<?>> getAllSettings() {
        //Get all settings
        List<Setting<?>> settings = new ArrayList<>();
        forSettingFields(f -> settings.add((Setting) getFieldValue(f)));

        //Clear possible null values
        return settings.stream().filter(s -> s != null).collect(Collectors.toList());
    }

    /** Get all fields that are Settings */
    private void forSettingFields(Consumer<Field> function) {
        for (Field field : this.getClass().getFields()) {
            try {
                //Loop through fields
                if (field.get(this) instanceof Setting) {
                    if (field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    function.accept(field);
                }
            } catch (Exception e) {
                Log.warn("[Settings] Failed to get field: " + field.getName());
            }
        };
    }

    /**
     * Set a field in this object
     * @param f The field
     * @param value The value for the field
     */
    private void setFieldObject(Field f, Object value) {
        try {
            f.set(this, value);
        } catch (Exception e) {
            Log.warn("[Settings] Failed to set field: " + f.getName());
        }
    }

    /**
     * Get the value from a field
     * @param f The field
     * @return The value
     */
    private Object getFieldValue(Field f) {
        try {
            return f.get(this);
        } catch (Exception e) {
            Log.warn("[Settings] Failed to get field: " + f.getName());
            return null;
        }
    }

    /**
     * Set a setting and save the config
     * @param setting the setting
     */
    public void set(Setting... setting) {
        for (Setting setting1 : setting) {
            set(setting1, true);
        }
    }

    /**
     * Set a setting
     * @param setting The setting
     * @param saveConfig Should also save the config
     */
    public void set(Setting setting, boolean saveConfig) {
        if (setting.getValue() instanceof Location) {
            Location l = (Location) setting.getValue();
            String key = setting.getKey();
            config.set(key + ".w", l.getWorld().getName());
            config.set(key + ".v", l.toVector());
            config.set(key + ".p", l.getPitch());
            config.set(key + ".y", l.getYaw());
        } else if (setting.getValue() instanceof ProtectedRegion) {
            config.set(setting.getKey(), ((ProtectedRegion) setting.getValue()).getId());
        } else {
            config.set(setting.getKey(), setting.getValue());
        }
        if (saveConfig) {
            yamlFile.saveConfig();
        }
    }

    /**
     * Load a setting from the config
     * @param key The key in the config file
     * @param defValue The default value
     * @param name The presentable name
     * @param description The description
     * @param group The setting group this setting belongs in
     * @param saveIfNotFound Should set the setting in the config and save the config if the setting isn't found
     * @param <T1> The class of the setting and the default value
     * @return The setting
     * @throws ClassCastException if it was unable to cast the found value to the class of the default value
     */
    protected <T1 extends Object> Setting<T1> load(String key, T1 defValue, String name, String description, SettingGroup group, boolean saveIfNotFound) throws ClassCastException {
        //Check if the config contains the key
        boolean contains = config.contains(key);

        //Get the class
        Class<? extends Object> typeClass = defValue.getClass();

        //Get the value
        Object value;
        if (typeClass.equals(Boolean.class)) {
            value = config.getBoolean(key, (Boolean) defValue);
        } else if (typeClass.equals(String.class)) {
            value = config.getString(key, (String) defValue);
        } else if (typeClass.equals(Integer.class)) {
            value = config.getInt(key, (Integer) defValue);
        } else if (typeClass.equals(Double.class)) {
            value = config.getDouble(key, (Double) defValue);
        } else if (typeClass.equals(Long.class)) {
            value = config.getLong(key, (Long) defValue);
        } else if (typeClass.equals(Vector.class)) {
            value = config.getVector(key, (Vector) defValue);
        } else if (typeClass.equals(Location.class)) {
            if (!config.contains(key + ".v")) {
                value = defValue;
            } else {
                try {
                    Vector locVector = config.getVector(key + ".v");
                    String worldname = config.getString(key + ".w");
                    World world = Bukkit.getWorld(worldname);
                    float yaw = (float) config.getDouble(key + ".y");
                    float pitch = (float) config.getDouble(key + ".p");
                    value = locVector.toLocation(world, yaw, pitch);
                } catch (Exception e) {
                    value = defValue;
                }
            }
        } else {
            value = config.get(key, defValue);
        }

        //Create the setting
        Setting<T1> setting = new Setting<>((T1) value, key, name, description, group);

        //Save the value if it didn't contain the the current value
        if (!contains && saveIfNotFound) {
            set(setting, true);
        }

        //Returnt he setting
        return setting;
    }

    /**
     * Load a setting from the config. The value can be null
     * @param key The key in the config file
     * @param clazz The class of the Setting
     * @param name The presentable name
     * @param description The description
     * @param group The setting group this setting belongs in
     * @param <T1> The class of the setting and the default value
     * @return The setting
     * @throws ClassCastException if it was unable to cast the found value to the class of the default value
     */
    protected <T1 extends Object> Setting<T1> load(String key, Class<T1> clazz, String name, String description, SettingGroup group) throws ClassCastException {
        //Check if the config contains the key
        boolean contains = config.contains(key);

        //Get the value based on the class
        Object value = null;
        if (contains) {
            if (clazz.equals(Boolean.class)) {
                value = config.getBoolean(key);
            } else if (clazz.equals(String.class)) {
                value = config.getString(key);
            } else if (clazz.equals(Integer.class)) {
                value = config.getInt(key);
            } else if (clazz.equals(Double.class)) {
                value = config.getDouble(key);
            } else if (clazz.equals(Long.class)) {
                value = config.getLong(key);
            } else if (clazz.equals(Vector.class)) {
                value = config.getVector(key);
            } else if (clazz.equals(Location.class)) {
                try {
                    Vector locVector = config.getVector(key + ".v");
                    String worldname = config.getString(key + ".w");
                    World world = Bukkit.getWorld(worldname);
                    float yaw = (float) config.getDouble(key + ".y");
                    float pitch = (float) config.getDouble(key + ".p");
                    value = locVector.toLocation(world, yaw, pitch);
                } catch (Exception e) {
                    //Can be ignored
                }
            } else {
                value = config.get(key);
            }
        }

        //Create & return the setting
        return new Setting<T1>((T1) value, key, name, description, group);
    }

    /** Initialize the settings */
    public abstract void initializeSettings();

}
