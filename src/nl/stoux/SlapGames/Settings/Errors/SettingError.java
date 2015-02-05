package nl.stoux.SlapGames.Settings.Errors;

/**
 * Created by Stoux on 25/01/2015.
 */
public class SettingError extends Exception {

    /** The exception is severe */
    private boolean severe;

    public SettingError(String message) {
        super(message);
        severe = true;
    }

    public SettingError(String message, boolean severe) {
        super(message);
        this.severe = severe;
    }
}
