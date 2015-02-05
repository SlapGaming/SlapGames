package nl.stoux.SlapGames.Commands.Model;

import lombok.AllArgsConstructor;
import nl.stoux.SlapGames.Commands.CommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by Stoux on 05/02/2015.
 */
public class ReflectCommand extends Command {

    private CommandHandler handler;

    public ReflectCommand(String name, String description, String usageMessage, List<String> aliases, CommandHandler handler) {
        super(name, description, usageMessage, aliases);
        this.handler = handler;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        handler.onCommand(commandSender, this, s, strings);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return handler.onTab(sender, this, alias, args);
    }
}
