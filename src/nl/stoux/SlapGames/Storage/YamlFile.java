package nl.stoux.SlapGames.Storage;

import lombok.Getter;
import nl.stoux.SlapGames.SlapGames;
import nl.stoux.SlapGames.Util.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * YamlStorage class from SlapHomebrew. All credits to {@author naithantu}
 * Minor modifications
 * @author naithantu
 */
public class YamlFile {

    @Getter
    private String filename;

    private File file;

    @Getter
    private FileConfiguration config;

    /**
     * Constructor
     * @param filename The filename without .yml
     */
    public YamlFile(String filename) {
        this.filename = filename;
        file = new File(SlapGames.getInstance().getDataFolder(), filename + ".yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Get the Config
     * @return the config
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Save the config
     * @return saved
     */
    public boolean saveConfig() {
        if (config == null || file == null) {
            return false;
        }

        //Try to save the config
        try {
            config.save(file);
            return true;
        } catch (IOException ex) {
            Log.severe("Could not save config to " + config + " | Ex: " + ex);
            return false;
        }
    }

    /** Reload the config */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

}
