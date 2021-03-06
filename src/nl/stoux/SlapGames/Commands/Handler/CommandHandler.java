package nl.stoux.SlapGames.Commands.Handler;

import nl.stoux.SlapGames.Commands.Annotations.Cmd;
import nl.stoux.SlapGames.Commands.Annotations.CmdTrain;
import nl.stoux.SlapGames.Commands.Annotations.Usage;
import nl.stoux.SlapGames.Commands.Base.BaseCommand;
import nl.stoux.SlapGames.Commands.Exceptions.CommonException;
import nl.stoux.SlapGames.Commands.Exceptions.NoMessageException;
import nl.stoux.SlapGames.Commands.Exceptions.UsageException;
import nl.stoux.SlapGames.Commands.Model.*;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Games.GameControl;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Util.CommandUtil;
import nl.stoux.SlapGames.Util.Log;
import nl.stoux.SlapGames.Util.PlayerUtil;
import nl.stoux.SlapGames.Util.Util;
import nl.stoux.SlapPlayers.Util.SUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import static nl.stoux.SlapGames.Util.PlayerUtil.badMsg;
import static nl.stoux.SlapGames.Util.PlayerUtil.hasPermission;

/**
 * Created by Stoux on 12/02/2015.
 */
public class CommandHandler {

    /** StoredCommands isntance with all the commands */
    private StoredCommands commands;

    public CommandHandler() {
        //Create the new CommandMap
        commands = new StoredCommands();

        //Find the commands & store them
        CommandFinder finder = new CommandFinder();
        finder.findCommands().forEach(b -> commands.storeBox(b.getCommand().getCmd().command(), b));
        finder.findRedirects().forEach(r -> commands.storeBox(r.getRedirect().getFromCommand(), r));

        //Register all commands in the bukkit server
        CommandMap map = getCommandMap();
        commands.getBaseCommands().forEach(c -> {
            map.register("games", new ReflectCommand(c, this));
        });
    }

    //<editor-fold desc="Events">
    /**
     * A command has been used
     * @param sender The CommandSender
     * @param cmd The command
     * @param commandLabel The alias used
     * @param args The arguments used
     */
    public void onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            //Find the box
            RedirectBox redirectBox = null;
            AnnotationBox box = commands.getAnnotationBox(cmd.getName(), args);
            if (box == null || !box.isFilled()) {
                //No box found at all
                throw new BaseException("Eh... This command was somehow not found."); //TODO: Make this better?
            }

            //Check if redirect box
            if (box.isRedirect()) {
                redirectBox = box.getRedirect();
                CmdTrain toCmd = redirectBox.getToCommand();
                CmdTrain fromCmd = redirectBox.getFromCommand();

                //Create new Args string if needed
                if (redirectBox.isKeepArguments()) {
                    //Create the new array
                    int leftOverArgs = args.length - fromCmd.arguments().length;
                    String[] newArgs = new String[leftOverArgs + toCmd.arguments().length];

                    //Add new arguments to array
                    for (int i = 0; i < toCmd.arguments().length; i++) {
                        newArgs[i] = toCmd.arguments()[i];
                    }

                    //Add leftovers
                    for (int i = 0; i < args.length && i < leftOverArgs; i++) {
                        newArgs[newArgs.length - 1 - i] = args[args.length - 1 - i];
                    }

                    //Set the args
                    args = newArgs;
                } else {
                    args = toCmd.arguments();
                }

                //Get the box again
                box = commands.getAnnotationBox(toCmd.value(), args);
                if (box == null || !box.isFilled()) {
                    //No box found at all
                    throw new BaseException("Eh... This command was somehow not found."); //TODO: Make this better?
                } else if (box.isRedirect()) {
                    //Box is another redirect. Not going to allow double redirects.
                    throw new BaseException("Redirect loop. Warn Stoux!");
                }
            }


            //Get CommandBox & Cmd
            CommandBox commandBox = box.getCommand();
            Cmd ann = commandBox.getCmd();

            //Check if player
            if ((ann.playerOnly() || ann.inGameOnly()) && !PlayerUtil.isPlayer(sender)) {
                throw CommonException.PLAYERS_ONLY;
            }

            //Check if any permissions are given
            if (ann.permission().length > 0) {
                //Check if the player has all of them
                if (!Stream.of(ann.permission()).allMatch(p -> hasPermission(sender, p))) {
                    throw CommonException.NO_PERMISSION;
                }
            }

            //Check if in a game if specified
            if (ann.inGameOnly()) {
                //Check if the player is a GamePlayer
                GamePlayer gp = GameControl.getGamePlayer((Player) sender);
                if (gp == null) {
                    throw CommonException.NOT_IN_GAME;
                }

                //Check in the correct game
                if (gp.getGame().getGameType() != ann.gameMode()) {
                    throw CommonException.WRONG_GAME;
                }
            }

            //Filter the arguments
            int removedArgs = ann.command().arguments().length;
            int leftOverArgs = args.length - removedArgs;
            String[] newArgs = new String[leftOverArgs];
            //=> Copy the last arguments
            for (int i = 0; i < args.length && i < leftOverArgs; i++) {
                newArgs[newArgs.length - i - 1] = args[args.length - i - 1];
            }

            Log.info(SUtil.combineToString(newArgs, " ", s -> s));

            //Check usage
            Usage usage = ann.usage();
            int givenArgs = newArgs.length;
            int requiredArgs = usage.value().length;
            Object[] castObjects = null;

            //=> Not enough arguments
            if (givenArgs < requiredArgs) {
                throw buildUsageException(ann, redirectBox);
            }

            //=> To many arguments
            if (givenArgs > requiredArgs && !usage.allowOtherArguments() && !usage.repeatLastArguments()) {
                throw buildUsageException(ann, redirectBox);
            }

            //=> Check if any Usage types are given
            if (usage.value().length > 0) {
                castObjects = new Object[newArgs.length];

                try {
                    //Loop through given arguments
                    for (int i = 0; i < newArgs.length; i++) {
                        String arg = newArgs[i];
                        ArgumentType type;
                        Object object;

                        //Get type
                        if (i >= usage.value().length) {
                            if (usage.repeatLastArguments()) {
                                //Get last type
                                type = usage.value()[usage.value().length - 1];
                            } else {
                                //Nothing more we can do
                                break;
                            }
                        } else {
                            type = usage.value()[i];
                        }

                        //Check type
                        switch (type) {
                            //Get offline player
                            case OFFLINE_PLAYER:
                                try {
                                    object = PlayerUtil.getProfile(sender, arg);
                                } catch (NoMessageException e) {
                                    //Message has already been sent.
                                    return;
                                }
                                break;

                            //Get online player
                            case ONLINE_PLAYER:
                                object = CommandUtil.getOnlinePlayer(arg);
                                break;

                            //Parse as int
                            case INT:
                                object = CommandUtil.parseInt(arg);
                                break;
                            case POSITIVE_INT:
                                object = CommandUtil.parsePositiveInt(arg);
                                break;

                            //Parse to GameType
                            case GAME_TYPE:
                                object = CommandUtil.parseGameType(arg);
                                break;

                            //Default is fine with everything
                            case STRING:
                            default:
                                object = arg;
                                break;
                        }

                        //Set object
                        castObjects[i] = object;
                    }
                } catch (BaseException e) {
                    //Rethrow as UsageException
                    throw buildUsageException(ann, redirectBox, e.getMessage());
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
                BaseCommand foundCommand = commandBox.getConstructor().newInstance(constructorArgs);
                foundCommand.setCastArguments(castObjects);
                foundCommand.handle();
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
     * [TAB] has been pressed for a command
     * @param sender The CommandSender who pressed TAB
     * @param command The command
     * @param alias The used alias
     * @param args The arguments already given/being typed
     * @return the list with args
     */
    @SuppressWarnings("unchecked")
    public List<String> onTab(CommandSender sender, Command command, String alias, String[] args) {
        //Find the box
        AnnotationBox box = commands.getFirstAnnotationBox(command.getName());
        if (box == null) {
            return null;
        }





        return null; //TODO
    }
    //</editor-fold>

    /**
     * Get the CommandMap from the Bukkit server
     * @return The map
     */
    public CommandMap getCommandMap() {
        try {
            //Get the Field
            final Field f = Util.getPlugin().getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            return (CommandMap) f.get(Util.getPlugin().getServer());
        } catch (Exception e) {
            //TODO Maybe not stop the server...
            throw new RuntimeException("Failed to load CommandMap");
        }
    }

    /**
     * Build a Usage Exception
     * @param ann The command annotation
     * @param redirectBox A possible redirect box
     * @param topMessage lines appended before the the rest of the exception
     * @return the exception
     */
    private static UsageException buildUsageException(Cmd ann, RedirectBox redirectBox, String... topMessage) {
        String usageMsg = "Usage: /";
        int usageIndex = 0;

        //Create the usage message
        if (redirectBox != null) {
            //Old redirect command
            usageMsg += toString(redirectBox.getFromCommand());

            //Check howmany usage arguments were already given in the redirect
            usageIndex  = redirectBox.getToCommand().arguments().length - ann.command().arguments().length;
        } else {
            usageMsg += toString(ann.command());
        }

        Usage usage = ann.usage();
        ArgumentType[] usageArgs = usage.value();
        String[] typeNames = usage.typeNames();
        int givenNames = typeNames.length;
        int requiredArgs = usageArgs.length;

        //Add arguments
        for (int i = usageIndex; i < requiredArgs; i++) {
            //Get the presentable name
            String pr = null;
            if (givenNames > i) {
                pr = typeNames[i];
                if (pr == null || pr.isEmpty()) {
                    pr = null;
                }
            }

            //Get the default name if no name given
            if (pr == null) {
                pr = usageArgs[i].getPresentableName();
            }

            if (i == requiredArgs - 1 && usage.repeatLastArguments()) {
                usageMsg += " <" + pr + " 1> [" + pr + " 2...]";
            } else {
                usageMsg += " <" + pr + ">";
            }
        }

        //TODO Maybe add ... for optional other arguments

        //Create String array
        String[] msg;
        if (topMessage.length == 0) {
            msg = new String[]{ann.description(), usageMsg};
        } else {
            msg = new String[topMessage.length + 1];
            for (int i = 0; i < topMessage.length; i++) {
                msg[i] = topMessage[i];
            }
            msg[msg.length - 1] = usageMsg;
        }

        //Throw exception
        return new UsageException(msg);
    }


    /**
     * To string a CmdTrain
     * @param cmdTrain
     * @return
     */
    private static String toString(CmdTrain cmdTrain) {
        String c = cmdTrain.value();
        for (String s : cmdTrain.arguments()) {
            c += " " + s;
        }
        return c;
    }


}
