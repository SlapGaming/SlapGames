package nl.stoux.SlapGames.Commands.Annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Stoux on 11/02/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Redirect {

    CmdTrain[] commands();

    CmdTrain redirectToCommand() default @CmdTrain("this");

    /** The exstra arguments given by the player should be kept and used on the redirected command */
    boolean keepExtraArguments() default true;

    /** The command is enabled and should be registered */
    boolean enabled() default true;

}
