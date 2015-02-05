package nl.stoux.SlapGames.Games.Parkour;

import lombok.Getter;
import lombok.Setter;
import nl.stoux.SlapGames.Games.Parkour.Maps.ParkourMap;
import nl.stoux.SlapGames.Games.Parkour.Models.ParkourRun;
import nl.stoux.SlapGames.Games.Parkour.Models.StoredParkourRun;
import nl.stoux.SlapGames.Players.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Stoux on 24/01/2015.
 */
public class ParkourPlayer extends GamePlayer<Parkour> {

    /** The parkour map this player is on */
    @Getter @Setter
    private ParkourMap map;

    /** The current run of that player */
    @Getter @Setter
    private ParkourRun run;

    /** An old stored run */
    @Getter @Setter
    private StoredParkourRun storedRun;

    /** The number of teleports this player is still allowed to make */
    @Getter @Setter
    private int allowedTeleports;

    public ParkourPlayer(Player player, Parkour game) {
        super(player, game);
        allowedTeleports = 0;
    }

    /**
     * Check if the player is on a map
     * @return is on map
     */
    public boolean isOnMap() {
        return map != null;
    }

    /**
     * The player is doing a run on a map
     * @return is running
     */
    public boolean isRunning() {
        return (map != null && run != null);
    }

    @Override
    public void teleport(Location location) {
        allowedTeleports++;
        super.teleport(location);
    }

    /** Give the player the powertools */
    public void givePowertools() {
        for (Powertool powertool : Powertool.values()) {
            //Create the item
            ItemStack item = new ItemStack(powertool.getType(), 1);

            //Get, modify & set the item meta
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(powertool.getName());
            if (powertool.getLore().length > 0) {
                meta.setLore(Arrays.asList(powertool.getLore()));
            }
            item.setItemMeta(meta);

            //Add the item at the correct position
            player.getInventory().setItem(powertool.getPosition(), item);
        }
    }

    /**
     * Check if the player is holding a powertool
     * @return the powertool or null
     */
    public Powertool isHoldingPowertool() {
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand != null) {
            for (Powertool powertool : Powertool.values()) {
                if (powertool.getType() == itemInHand.getType()) {
                    return powertool;
                }
            }
        }
        return null;
    }

    public enum Powertool {

        RESTART_CHECKPOINT(Material.STICK, 0, "Checkpoint", "Warp to the last checkpoint"),
        TIME(Material.WATCH, 1, "Time", "Get your current time"),
        RESTART(Material.BLAZE_ROD, 4, "Restart", "Restart your run", "Warning: This will completely reset your progress!"),
        CONTINUE(Material.REDSTONE, 6, "Continue", "Continue your last run", "(if there's one saved)"),
        RESET(Material.REDSTONE_TORCH_ON, 7, "Reset", "Reset your last run", "Warning: This will wipe the progress of your last run!", "(if there's one saved)");

        @Getter private Material type;
        @Getter private int position;
        @Getter private String name;
        @Getter private String[] lore;

        Powertool(Material type, int position, String name, String... lore) {
            this.type = type;
            this.position = position;
            this.name = name;
        }
    }



}
