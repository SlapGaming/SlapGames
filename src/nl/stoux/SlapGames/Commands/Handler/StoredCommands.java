package nl.stoux.SlapGames.Commands.Handler;

import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Model.AnnotationBox;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Stoux on 11/02/2015.
 */
public class StoredCommands {

    /** The HashMap containing all base commands */
    private HashMap<String, AnnotationBox> baseCommands;

    public StoredCommands() {
        baseCommands = new HashMap<>();
    }

    /**
     * Get the AnnotationBox for a command (with arguments)
     * @param command The command
     * @param arguments The possible arguments
     * @return the AnnotationBox or null
     */
    public AnnotationBox getAnnotationBox(String command, String... arguments) {
        //Find the BaseBox
        AnnotationBox currentBox = baseCommands.get(command.toLowerCase());
        AnnotationBox filledBox = null;

        //Check if arguments are given
        if (currentBox == null) {
            return null;
        } else if (arguments.length == 0) {
            if (currentBox.isFilled()) {
                return currentBox;
            } else {
                return null;
            }
        }

        //Check if filled
        if (currentBox.isFilled()) {
            filledBox = currentBox;
        }

        //Loop through arguments till no box can be found
        for (String argument : arguments) {
            //Find the box
            String arg = argument.toLowerCase();
            AnnotationBox foundBox = currentBox.getAnnotationBox(arg);

            //Check if found
            if (foundBox == null) {
                break;
            }

            //Check if filled
            if (foundBox.isFilled()) {
                filledBox = foundBox;
            }

            //Set the current
            currentBox = foundBox;
        }

        //Return the filled box | which still might be null
        return filledBox;
    }

    /**
     * Get the base/first command
     * @param command The command
     * @return the box or null
     */
    public AnnotationBox getFirstAnnotationBox(String command) {
        return baseCommands.get(command.toLowerCase());
    }


    //<editor-fold desc="Store Functions">
    /**
     * Store a Box in the StoredCommands
     * @param cmdTrain
     * @param newBox
     */
    public void storeBox(CmdTrain cmdTrain, AnnotationBox newBox) {
        //Get the Command
        String cmd = cmdTrain.value().toLowerCase();
        AnnotationBox baseBox = baseCommands.get(cmd);

        //Create baseBox if it doesn't exist
        if (baseBox == null) {
            baseCommands.put(cmd, baseBox = new AnnotationBox());
        }

        //Check if base command
        if (cmdTrain.arguments().length == 0) {
            copyBoxContents(newBox, baseBox);
            return;
        }

        //Store the box
        storeBox(cmdTrain.arguments(), 0, baseBox, newBox);
    }

    /**
     * Store a Box in another Box
     * @param arguments The arguments
     * @param depth The depth of arguments
     * @param currentBox The current box
     * @param newBox The new box that needs to be added
     */
    private void storeBox(String[] arguments, int depth, AnnotationBox currentBox, AnnotationBox newBox) {
        //See if the box already exists
        String arg = arguments[depth].toLowerCase();
        AnnotationBox foundBox = currentBox.getAnnotationBox(arg);

        if (arguments.length == depth - 1) {
            //Last argument. Must store now.
            if (foundBox == null) {
                currentBox.storeAnnotationBox(arg, newBox);
            } else {
                copyBoxContents(newBox, foundBox);
            }
        } else {
            //Check if the box already exists
            if (foundBox == null) {
                currentBox.storeAnnotationBox(arg, foundBox = new AnnotationBox());
            }

            //Go a level deeper
            storeBox(arguments, depth + 1, foundBox, newBox);
        }
    }


    /**
     * Copy the annotation contents of the box
     * @param fromBox Take from this box
     * @param toBox Copy to this box
     */
    private void copyBoxContents(AnnotationBox fromBox, AnnotationBox toBox) {
        if (fromBox.getCommand() != null) {
            toBox.setCommand(fromBox.getCommand());
        }
        if (fromBox.getRedirect() != null) {
            toBox.setRedirect(fromBox.getRedirect());
        }
    }
    //</editor-fold>

    /**
     * Get all the base commands
     * @return the commands
     */
    public Collection<String> getBaseCommands(){
        return baseCommands.keySet();
    }

}
