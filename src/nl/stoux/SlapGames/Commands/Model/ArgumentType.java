package nl.stoux.SlapGames.Commands.Model;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created by Stoux on 11/02/2015.
 */
public enum ArgumentType {

    OFFLINE_PLAYER("Player"),
    ONLINE_PLAYER("Player"),
    STRING("Value"),
    INT("Number"),
    POSITIVE_INT("Number"),
    GAME_TYPE("Gametype");

    @Getter(AccessLevel.PUBLIC)
    private String presentableName;

    ArgumentType(String presentableName) {
        this.presentableName = presentableName;
    }
}
