package nl.stoux.SlapGames.Commands.Annotations;

import nl.stoux.SlapGames.Commands.Model.ArgumentType;

/**
 * Created by Stoux on 11/02/2015.
 */
public @interface Usage {

    /** The type of arguments expected */
    ArgumentType[] value() default {};

    /**
     * Presentable names for the ArgumentTypes
     * Values can be null. It will take the default value for the type.
     */
    String[] typeNames() default {};

    /** Allow when more arguments are given */
    boolean allowOtherArguments() default true;

    /** The last argument can be used multiple times */
    boolean repeatLastArguments() default false;

}
