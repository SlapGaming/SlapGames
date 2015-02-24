package nl.stoux.SlapGames.Games.Base.Arena;

import lombok.Getter;
import nl.stoux.SlapGames.Storage.YamlFile;

/**
 * Created by Stoux on 21/02/2015.
 */
public abstract class BaseArena<Settings extends BaseArenaSettings> {

    /** The filename without .yml */
    protected String filename;

    /** The settings for this Arena */
    @Getter protected Settings settings;

    public BaseArena(YamlFile file) {
        this.settings = createSettings(file);
        this.filename = file.getFilename();
    }

    /** Create the settings */
    protected abstract Settings createSettings(YamlFile file);

    /**
     * Save the Arena
     * @return saved
     */
    public abstract boolean saveArena();

    /**
     * Restore the Arena
     * @return restored
     */
    public abstract boolean restoreArena();

}
