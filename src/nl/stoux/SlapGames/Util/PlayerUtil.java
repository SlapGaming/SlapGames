package nl.stoux.SlapGames.Util;

import nl.stoux.SlapGames.Commands.Exceptions.NoMessageException;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapPlayers.Control.UUIDControl;
import nl.stoux.SlapPlayers.Model.Name;
import nl.stoux.SlapPlayers.Model.Profile;
import nl.stoux.SlapPlayers.SlapPlayers;
import nl.stoux.SlapPlayers.Util.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

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


    public static void hMsg(CommandSender sender, String message) {
        //TODO
    }

    /**
     * Get the UUIDController
     * @return the controller
     */
    public static UUIDControl getUUIDController() {
        return SlapPlayers.getUUIDController();
    }

    /**
     * Get a player's UUID profile
     * @param p The player
     * @return The profile
     */
    public static Profile getProfile(Player p) {
        return getUUIDController().getProfile(p);
    }

    /**
     * Get a player
     * @param commandSender The sender who is request the profile
     * @param playername The value of the player
     * @return The profile
     * @throws BaseException if not found
     */
    public static Profile getProfile(CommandSender commandSender, String playername) throws BaseException {
        //Get UUIDControl
        UUIDControl control = getUUIDController();

        //Get UserIDs with this playername
        Collection<Integer> ids = control.getUserIDs(playername);
        if (ids.isEmpty()) {
            //No players with this value
            throw new BaseException("There is no player that has ever used this name.");
        } else {
            //Multiple players with this value. Check if one is currently using it.
            for (int id : ids) {
                Profile foundProfile = control.getProfile(id);
                if (foundProfile.getCurrentName().equalsIgnoreCase(playername)) { //If currently being used profile
                    return foundProfile;
                }
            }
            //No user is currently using the value
            hMsg(commandSender, "No user is currently using that name! Did you mean:");
            for (int id : ids) {
                Profile foundProfile = control.getProfile(id);
                Name nameProfile = foundProfile.getNames().get(0);
                commandSender.sendMessage(ChatColor.GOLD + "   ┗▶ " + ChatColor.WHITE + nameProfile.getPlayername() + ChatColor.GRAY + " (since " + DateUtil.format("dd/MM/yyyy", nameProfile.getKnownSince()) + ")");
            }
            //Message is already send, throw NoMessageException
            throw new NoMessageException();
        }
    }

    /**
     * Get a player's User ID as specified in the database
     * @param p The player
     * @return The ID
     */
    public static int getUserID(Player p) {
        return getProfile(p).getID();
    }

    /**
     * Get a player's User ID as specified in the database
     * @param gp The player
     * @return The ID
     */
    public static int getUserID(GamePlayer gp) {
        return getUserID(gp.getPlayer());
    }


}
