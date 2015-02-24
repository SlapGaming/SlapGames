package nl.stoux.SlapGames.Commands.Annotations;

/**
 * Created by Stoux on 11/02/2015.
 */
public @interface CmdTrain {

    /** The command */
    String value();

    /** Possible arguments */
    String[] arguments() default {};

}
