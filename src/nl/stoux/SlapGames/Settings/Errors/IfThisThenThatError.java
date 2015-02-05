package nl.stoux.SlapGames.Settings.Errors;

import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Settings.SettingGroup;
import nl.stoux.SlapPlayers.Util.SUtil;

/**
 * Created by Stoux on 25/01/2015.
 *
 * 'If a certain value is set than another setting is needed'... -Exception
 */
public class IfThisThenThatError extends SettingError {

    private IfThisThenThatError(String message) {
        super(message);
    }

    /**
     * Create a IfThisThenThatException that needs all given settings if the base setting is set/true
     * @param ifThis The base setting
     * @param thanThese All of these are required if the base setting is set
     * @return the exception
     */
    public static IfThisThenThatError ifThisThanThese(Setting ifThis, Setting... thanThese) {
        String message = baseString(ifThis) + "these are required: " + combineRequiredSettings(ifThis, thanThese);
        return new IfThisThenThatError(message);
    }

    /**
     * Create a IfThisThenThatException that needs one of the given settings if the base setting is set/true
     * @param ifThis The base setting
     * @param thanOneOfThese One of these is required if the base setting is set
     * @return the exception
     */
    public static IfThisThenThatError ifThisThenOneOfThose(Setting ifThis, Setting... thanOneOfThese) {
        String message = baseString(ifThis) + "one of these is required: " + combineRequiredSettings(ifThis, thanOneOfThese);
        return new IfThisThenThatError(message);
    }

    private static String baseString(Setting setting) {
        return "Setting (" + setting.getSettingGroup() + ") '" + setting.getName() + "' is set/true, so ";
    }

    /**
     * Combine the required settings into one string
     * @param givenSetting The givenSetting
     * @param settings The required settings
     * @return the combined string
     */
    private static String combineRequiredSettings(Setting givenSetting, Setting... settings) {
        SettingGroup g = givenSetting.getSettingGroup();
        return SUtil.combineToString(settings, ", ", s ->
                        (s.getSettingGroup() == g ? "" : "(" + s.getSettingGroup().getPresentableName() + ") ")
                                + s.getName()
        );
    }
}
