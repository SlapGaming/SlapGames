package nl.stoux.SlapGames.Commands.Handler;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Redirect;
import nl.stoux.SlapGames.Commands.Base.BaseCommand;
import nl.stoux.SlapGames.Commands.Model.AnnotationBox;
import nl.stoux.SlapGames.Commands.Model.CommandBox;
import nl.stoux.SlapGames.Commands.Model.RedirectBox;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.SlapGames;
import nl.stoux.SlapGames.Util.Log;
import nl.stoux.SlapGames.Util.Util;
import nl.stoux.SlapPlayers.Util.ReflectionUtil;
import nl.stoux.SlapPlayers.Util.SUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Stoux on 11/02/2015.
 */
public class CommandFinder {

    private Reflections reflections;

    public CommandFinder() {
        //Create the reflections instance
        reflections = ReflectionUtil.reflectPackage(SlapGames.class);
    }

    /**
     * Find all Commands
     * @return Collection with Command filled AnnotationBoxes
     */
    public Collection<AnnotationBox> findCommands() {
        //Find all Cmd Annotations
        return reflections
                .getTypesAnnotatedWith(Cmd.class)
                .stream()
                .filter(BaseCommand.class::isAssignableFrom)
                .filter(c -> c.getAnnotation(Cmd.class).enabled())
                .map(this::parseCommandClass)
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    /**
     * Parse a class that has a Cmd annotation
     * @param clazz The class
     * @return The annotation box containing a CommandBox or null
     */
    @SuppressWarnings("unchecked")
    private AnnotationBox parseCommandClass(Class<?> clazz) {
        //Get the Annotation
        Cmd ann = clazz.getAnnotation(Cmd.class);

        try {
            //Create the constructor arguments
            ArrayList<Class<?>> constructorArgs = SUtil.toArrayList(CommandSender.class, Command.class, String.class, String[].class);
            if (ann.gameMode() != GameType.NO_GAME) {
                constructorArgs.add(GameType.class);
            }
            //=> Get the constructor
            Constructor<? extends BaseCommand> constructor =
                    (Constructor<? extends BaseCommand>) clazz.getConstructor(
                            constructorArgs.toArray(new Class<?>[constructorArgs.size()])
                    );

            //Check if the Class has a TabHandler method
            Method tabMethod = null;
            try {
                tabMethod = clazz.getMethod("handleTab", CommandSender.class, Command.class, String.class, String[].class);
            } catch (NoSuchMethodException e) {
                //Can be ignored
            }

            //Create the Box
            return new AnnotationBox(new CommandBox(ann, constructor, tabMethod));
        } catch (Exception e) {
            //Something went wrong. Clearly.
            Log.severe("Failed to register command: " + ann.command().value() + " | Reason: " + e.getClass().getName() + " -> " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Find all Redirects
     * @return Collection with Redirect filled AnnotationBoxes
     */
    public Collection<AnnotationBox> findRedirects() {
        final List<AnnotationBox> boxes = new ArrayList<>();
        reflections
                .getTypesAnnotatedWith(Redirect.class)
                .stream()
                .filter(c -> c.getAnnotation(Redirect.class).enabled())
                .map(this::parseRedirectClass)
                .filter(c -> c != null)
                .forEach(boxes::addAll);
        return boxes;
    }

    /**
     * Parse a class that has a Redirect annotation
     * @param clazz The class
     * @return The annotation box containing a RedirectBox or null
     */
    public Collection<AnnotationBox> parseRedirectClass(Class<?> clazz) {
        //Get the Redirect annotation
        Redirect redirect = clazz.getAnnotation(Redirect.class);
        CmdTrain redirectToCommand = redirect.redirectToCommand();

        //Check reference
        if (redirectToCommand.value().equalsIgnoreCase("this")) {
            //Redirecting to this class. Check if actually a Command class
            Cmd cmd = clazz.getAnnotation(Cmd.class);
            if (!BaseCommand.class.isAssignableFrom(clazz) || cmd == null) {
                return null;
            }

            //Set the Redirect
            redirectToCommand = cmd.command();
        }

        //Create list with AnnotationBoxes
        List<AnnotationBox> boxes = new ArrayList<>();
        for (CmdTrain cmdTrain : redirect.commands()) {
            boxes.add(new AnnotationBox(new RedirectBox(cmdTrain, redirectToCommand, redirect.keepExtraArguments())));
        }
        return boxes;
    }

}
