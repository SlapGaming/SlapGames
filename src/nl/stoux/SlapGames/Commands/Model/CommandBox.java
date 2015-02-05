package nl.stoux.SlapGames.Commands.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Base.BaseCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by Stoux on 05/02/2015.
 */
@Getter
@AllArgsConstructor
public class CommandBox {

    /** The Cmd Annotation */
    private Cmd cmd;

    /** The constructor for creating this command */
    private Constructor<? extends BaseCommand> constructor;

    /** Method for completing tabs, can be null */
    private Method tabMethod;

}
