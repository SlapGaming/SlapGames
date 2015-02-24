package nl.stoux.SlapGames.Games;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.File;

/**
 * Created by Stoux on 12/12/2014.
 */
public enum GameType {

    /** No Game indicator for Cmd annotations */
    NO_GAME,

    CAKE_DEFENCE("Cake Defence", "CD", "CD"),
    PARKOUR("Parkour", "Parkour"),
    SONIC("Sonic", "Sonic"),
    SPLEEF("Spleef", "Spleef"),
    TNT_RUN("TNT Run", "TNTRun");

    /** The name in normal english */
    @Getter(AccessLevel.PUBLIC)
    private String presentableName;

    /** A shorter version of the name */
    @Getter(AccessLevel.PUBLIC)
    private String shortName;

    /** The folder name */
    @Getter(AccessLevel.PUBLIC)
    private String folder;

    /** Is actually a game */
    @Getter
    private boolean isGame = true;

    GameType(){
        this.isGame = false;
    }

    GameType(String presentableName, String folder) {
        this.presentableName = presentableName;
        this.folder = folder;
        shortName = presentableName;
    }

    GameType(String presentableName, String shortName, String folder) {
        this.presentableName = presentableName;
        this.shortName = shortName;
        this.folder = folder;
    }

    /**
     * Get the folder name with File seperators on the side
     * @param sepLeft
     * @param sepRight
     * @return the folder
     */
    public String getFolder(boolean sepLeft, boolean sepRight) {
        String f = (sepLeft ? File.separator : "");
        f += getFolder();
        f += (sepRight ? File.separator : "");
        return f;
    }

    /**
     * Parse a String into a GameType
     * @param arg The string
     * @return The GameType or null
     */
    public static GameType parseGameType(String arg) {
        //Stip the argument
        arg = stripString(arg);

        //Check if match
        for (GameType gameType : GameType.values()) {
            //Only accept actual games
            if (!gameType.isGame()) {
                continue;
            }

            //Strip strings to match
            String toString = stripString(gameType.toString());
            String name = stripString(gameType.getShortName());
            String presentable = stripString(gameType.getPresentableName());

            //Try to match
            if (toString.equalsIgnoreCase(arg) || name.equalsIgnoreCase(arg) || presentable.equalsIgnoreCase(arg)) {
                return gameType;
            }
        }

        //Nothing matched
        return null;
    }

    /**
     * Strip a string of underscores, dashes and spaces
     * @param value The string
     * @return The stripped string
     */
    private static String stripString(String value) {
        return value.replace("_", "").replace("-", "").replace(" ", "");
    }



}
