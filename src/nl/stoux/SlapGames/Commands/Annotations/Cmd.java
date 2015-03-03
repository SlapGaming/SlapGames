package nl.stoux.SlapGames.Commands.Annotations;

import nl.stoux.SlapGames.Games.GameType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Stoux on 04/02/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cmd {

    /** Command can only be used by players */
    boolean playerOnly() default false;

    /** The command */
    CmdTrain command();

    /** Aliases for this command */
    //CmdTrain[] aliases() default {};

    /** The description of the command */
    String description() default "";

    /** The usage message given */
    Usage usage() default @Usage();

    /** Build a usage message of all possible arguments when used in TAB */
    boolean buildTabUsageMessage() default false;

    /** The permissions needed for this command */
    String[] permission() default {};

    /** Command is tied to a GameMode */
    GameType gameMode() default GameType.NO_GAME;

    /** Command can only be used by players in that minigame */
    boolean inGameOnly() default false;

    /** The command is enabled and should be registered */
    boolean enabled() default true;

}
