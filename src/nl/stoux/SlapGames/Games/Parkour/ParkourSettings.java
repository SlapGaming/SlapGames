package nl.stoux.SlapGames.Games.Parkour;

import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Games.Base.BaseGameSettings;
import nl.stoux.SlapGames.Settings.Errors.SettingError;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapGames.Storage.YamlFile;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.HashSet;


/**
 * Created by Stoux on 24/01/2015.
 */
public class ParkourSettings extends BaseGameSettings {

    @Getter
    private HashSet<Pad> pads;

    public ParkourSettings(YamlFile yamlFile) {
        super(yamlFile);
    }

    @Override
    public void initializeSettings() {
        super.initializeSettings();
        pads = new HashSet<>();

        //Get pads
        ConfigurationSection section = yamlFile.getConfig().getConfigurationSection("pads");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                pads.add(initializePad("pads", key));
            }
        }
    }

    @Override
    protected void checkExceptions() {
        super.checkExceptions();

        //Check how many valid pads there are
        int validPads = pads.size();
        for (Pad pad : pads) {
            if (!pad.isValid()) {
                errors.add(new SettingError("Invalid Pad, Key: " + pad.getKey(), false));
                validPads--;
            }
        }

        //Add another exception if there are no valid pads
        if (validPads == 0) {
            errors.add(new SettingError("No valid Pads found!"));
            notSetup();
        }
    }

    /**
     * Initialize a pad
     * @param base The base to the pads
     * @param key The key of the pad
     * @return the pad or null if failed
     */
    private Pad initializePad(String base, String key) {
        //Create the pad
        Pad pad = new Pad(key);
        String baseKey = base + "." + key + ".";

        //Get the locations
        pad.padLocation = loadBlock(baseKey + "loc", "Pad Location", "The location of the pad",
                SettingGroup.PARKOUR_PADS, Material.GOLD_PLATE, Material.IRON_PLATE, Material.STONE_PLATE, Material.WOOD_PLATE, Material.STONE_BUTTON, Material.WOOD_BUTTON
        );
        pad.signLocation = loadBlock(baseKey + "sign", "Sign Location", "The location of the sign that displays the info", SettingGroup.PARKOUR_PADS,
                Material.SIGN, Material.SIGN_POST, Material.WALL_SIGN
        );

        //Return the pad
        return pad;
    }

    @Getter
    public class Pad {

        /** The key in the config */
        private String key;

        /** The location of the pad */
        private Setting<Vector> padLocation;

        /** The location of the sign */
        private Setting<Vector> signLocation;

        public Pad(String key) {
            this.key = key;
        }

        /**
         * Check if the pad is valid
         * @return is valid
         */
        public boolean isValid() {
            return (padLocation.getValue() != null && signLocation.getValue() != null);
        }

    }

}
