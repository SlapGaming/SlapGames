package nl.stoux.SlapGames.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Stoux on 05/02/2015.
 */
public class PlayerUtil {

    /**
     * Check if the CommandSender is a player
     * @param sender The CommandSender
     * @return is a player
     */
    public static boolean isPlayer(CommandSender sender) {
        return (sender instanceof Player);
    }

    /**
     * Check if a CommandSender has a permission
     * @param sender The CommandSender
     * @param subPermission The sub permission (appended after 'slapgames.')
     * @return has the permission
     */
    public static boolean hasPermission(CommandSender sender, String subPermission) {
        return sender.hasPermission("slapgames." + subPermission);
    }

    /**
     * Send a 'Bad Message' in red to the CommandSender
     * @param sender The sender
     * @param message The message
     */
    public static void badMsg(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

}
