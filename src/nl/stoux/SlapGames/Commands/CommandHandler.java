package nl.stoux.SlapGames.Commands;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Base.BaseCommand;
import nl.stoux.SlapGames.Commands.Exceptions.CommonException;
import nl.stoux.SlapGames.Commands.Exceptions.UsageException;
import nl.stoux.SlapGames.Commands.Model.CommandBox;
import nl.stoux.SlapGames.Commands.Model.ReflectCommand;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Util.Log;
import nl.stoux.SlapGames.Util.Util;
import nl.stoux.SlapPlayers.Util.SUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static nl.stoux.SlapGames.Util.PlayerUtil.*;


/**
 * Created by Stoux on 05/02/2015.
 */
public class CommandHandler {

    /**
     * A HashMap containing a combination of a
     */
    private ConcurrentHashMap<List<String>, CommandBox> commands;


    @SuppressWarnings("unchecked")
    public CommandHandler() {
        commands = new ConcurrentHashMap<>();
        //Find all the commands
        Reflections reflections = new Reflections(ClasspathHelper.forPackage("nl.stoux.SlapGames", Util.getPlugin().getClass().getClassLoader()));
        Set<Class<?>> commandClasses = reflections.getTypesAnnotatedWith(Cmd.class);

        //Get the CommandMap
        CommandMap commandMap;
        try {
            //Get the Field
            final Field f = Util.getPlugin().getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Util.getPlugin().getServer());
        } catch (Exception e) {
            //TODO Maybe not stop the server...
            throw new RuntimeException("Failed to load CommandMap");
        }

        //Keep track of the MainCommands that have been registered
        HashSet<String> registeredCommands = new HashSet<>();

        //Parse the commands
        commandClasses.stream().filter(BaseCommand.class::isAssignableFrom).forEach(c -> {
            //Get the annotation
            Cmd ann = c.getAnnotation(Cmd.class);

            //Check if enabled
            if (!ann.enabled()) {
                return;
            }

            try {
                //Create the constructor arguments
                ArrayList<Class<?>> constructorArgs = SUtil.toArrayList(CommandSender.class, Command.class, String.class, String[].class);
                if (ann.gameMode() != GameType.NO_GAME) {
                    constructorArgs.add(GameType.class);
                }
                //=> Get the constructor
                Constructor<? extends BaseCommand> constructor =
                        (Constructor<? extends BaseCommand>) c.getConstructor(
                                constructorArgs.toArray(new Class<?>[constructorArgs.size()])
                        );

                //Check if the Class has a TabHandler method
                Method tabMethod = null;
                try {
                    tabMethod = c.getMethod("handleTab", CommandSender.class, Command.class, String.class, String[].class);
                } catch (NoSuchMethodException e) {
                    //Can be ignored
                }

                //Create the CommandBox
                CommandBox box = new CommandBox(ann, constructor, tabMethod);

                //Create the list of command + subcommands
                ArrayList<String> cmdTrain = SUtil.toArrayList(ann.subCommand());
                cmdTrain.add(0, ann.command());
                cmdTrain = SUtil.toArrayList(
                        cmdTrain.stream().map(String::toLowerCase)
                );

                //Store the command
                commands.put(cmdTrain, box);

                //Register the command with Bukkit
                if (!registeredCommands.contains(ann.command().toLowerCase())) {
                    registeredCommands.add(ann.command().toLowerCase());
                    commandMap.register("games", new ReflectCommand(ann.command(), ann.description(), ann.usageMessage(), Arrays.asList(ann.aliases()), this));
                }
            } catch (Exception e) {
                Log.severe("Failed to register command: " + ann.command() + " | Reason: " + e.getClass().getName() + " -> " + e.getMessage());
                e.printStackTrace();
            }
        });

        //Print status
        Log.info("Registered " + commands.size() + " commands.");
    }


    /**
     * A command has been used
     * @param sender The CommandSender
     * @param cmd The command
     * @param commandLabel The alias used
     * @param args The arguments used
     */
    public void onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            //Get the Command
            CommandBox box = getCommandBox(cmd, args);
            if (box == null) {
                throw new BaseException("Err... Never heard of the command.");
            }

            //Check common things
            Cmd ann = box.getCmd();

            //Check if a player
            if (ann.playerOnly() && !isPlayer(sender)) {
                throw CommonException.PLAYERS_ONLY;
            }

            //Check if there are any permissions that this command requires
            if (ann.permission().length > 0) {
                //Check if the player has all of them
                if (!Stream.of(ann.permission()).allMatch(p -> hasPermission(sender, p))) {
                    throw CommonException.NO_PERMISSION;
                }
            }

            try {
                //Create Argument list
                Object[] constructorArgs = new Object[ann.gameMode() == GameType.NO_GAME ? 4 : 5];
                constructorArgs[0] = sender;
                constructorArgs[1] = cmd;
                constructorArgs[2] = commandLabel;
                constructorArgs[3] = args;

                //Check if bound to a Game
                if (ann.gameMode() != GameType.NO_GAME) {
                    constructorArgs[4] = ann.gameMode();
                }

                //Create & handle the Command
                BaseCommand foundCommand = box.getConstructor().newInstance(constructorArgs);
                foundCommand.handle();
            } catch (UsageException e) {
                //Can only be thrown from the handle
                if (ann.usageMessage().equals("")) {
                    throw e; //Rethrow UsageException if no UsageMessage is given
                } else {
                    throw new BaseException(ann.usageMessage());
                }
            } catch (Exception e) {
                //A severe error occurred (most likely @ creating a new instance)
                Log.severe("Failed to create Command | " + e.getClass().getName() + ": " + e.getMessage());
                throw new BaseException();
            }
        } catch (BaseException e) {
            badMsg(sender, e.getMessage());
        }
    }

    /**
     * Get a CommandBox based on the Command + arguments
     * @param command The command
     * @param args The arguments
     * @return the CommandBox or null
     */
    private CommandBox getCommandBox(Command command, String[] args) {
        //Create the List with arguments to find the command
        List<String> cmdStrings = SUtil.toArrayList(args);
        cmdStrings.add(0, command.getName());

        //=> Lowercase all
        cmdStrings = SUtil.toArrayList(
                cmdStrings.stream().map(String::toLowerCase)
        );

        //Get the Command
        return getCommandBox(cmdStrings);
    }

    /**
     * Get a command based on the arguments used
     * @param cmds The command + arguments
     * @return The CommandBox or null
     */
    private CommandBox getCommandBox(List<String> cmds) {
        //Get the CommandBox
        CommandBox box = commands.get(cmds);
        if (box == null) {
            //Remove the last param
            int cmdsSize = cmds.size();
            if (cmdsSize > 1) {
                //If enough params left, remove the last one & try again
                cmds.remove(cmdsSize - 1);
                box = getCommandBox(cmds);
            }
        }
        return box;
    }

    /**
     * [TAB] has been pressed for a command
     * @param sender The CommandSender who pressed TAB
     * @param command The command
     * @param alias The used alias
     * @param args The arguments already given/being typed
     * @return the list with args
     */
    public List<String> onTab(CommandSender sender, Command command, String alias, String[] args) {
        CommandBox box = getCommandBox(command, args);
        if (box == null || box.getTabMethod() == null) {
            return null;
        } else {
            //Try to invoke the static method
            try {
                return (List<String>) box.getTabMethod().invoke(null, sender, command, alias, args);
            } catch (Exception e) {
                return null;
            }
        }
    }



}
