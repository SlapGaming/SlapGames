package nl.stoux.SlapGames.Settings.Errors;

import nl.stoux.SlapGames.Settings.Setting;

/**
 * Created by Stoux on 25/01/2015.
 */
public class MissingSettingError extends SettingError {

    public MissingSettingError(Setting setting) {
        super("The setting (" + setting.getSettingGroup().getPresentableName() + ") " + setting.getName() + " is required.");
    }
}
