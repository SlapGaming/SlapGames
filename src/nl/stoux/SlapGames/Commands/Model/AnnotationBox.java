package nl.stoux.SlapGames.Commands.Model;

import lombok.Getter;
import lombok.Setter;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;

import java.util.HashMap;

/**
 * Created by Stoux on 11/02/2015.
 */
public class AnnotationBox {

    /** The Command */
    @Getter @Setter
    private CommandBox command;

    /** The Redirect */
    @Getter @Setter
    private RedirectBox redirect;

    /** Subcommands */
    private HashMap<String, AnnotationBox> arguments;

    public AnnotationBox() {}

    public AnnotationBox(CommandBox command) {
        this.command = command;
    }

    public AnnotationBox(RedirectBox redirect) {
        this.redirect = redirect;
    }

    /**
     * Get an AnnotationBox by it's argument
     * @param argument The argument in lowercase
     * @return The Box or null
     */
    public AnnotationBox getAnnotationBox(String argument) {
        if (arguments == null || !arguments.containsKey(argument)) {
            return null;
        } else {
            return arguments.get(argument);
        }
    }

    /**
     * Store the AnnotationBox in this AnnotationBox
     * @param argument The argument
     * @param box The box
     */
    public void storeAnnotationBox(String argument, AnnotationBox box) {
        //Create the map if it doesn't exist yet
        if (arguments == null) {
            arguments = new HashMap<>();
        }

        //Store the box
        arguments.put(argument.toLowerCase(), box);
    }

    /**
     * Check if either redirect or command is filled
     * @return is filled
     */
    public boolean isFilled() {
        return isCommand() || isRedirect();
    }

    /**
     * Check if this AnnotationBox contains a CommandBox
     * @return is command
     */
    public boolean isCommand() {
        return getCommand() != null;
    }

    /**
     * Check if this AnnotationBox contains a RedirectBox
     * @return is redirect
     */
    public boolean isRedirect() {
        return getRedirect() != null;
    }




}
