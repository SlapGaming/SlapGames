package nl.stoux.SlapGames.Games.Parkour;

import lombok.Getter;
import lombok.Setter;
import nl.stoux.SlapGames.Exceptions.BaseException;
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
        RESET(Material.REDSTONE_TORCH_ON, 7, "Reset", "Remove your saved run", "Warning: This will wipe the progress of your last run!", "(if there's one saved)");

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



    /** Restart at the last checkpoint if possible, otherwise full restart */
    public void restartRunAtLastCheckpoint() throws BaseException {
        checkRun();
        //Check if there's a checkpoint to go back to | Otherwise full restart
        int lastCheckpoint = run.getLastCheckpoint();
        if (lastCheckpoint > 0) {
            teleport(map.getRespawnLocation(this));
            game.hMessagePlayer(this, "You have been warped back to checkpoint #" + lastCheckpoint + "!");
        } else {
            restartRun();
        }
    }

    /** Restart the whole map */
    public void restartRun() {
        setRun(null);
        teleport(map.getSettings().getLobby().getValue());
        game.hMessagePlayer(this, "You've been warped back to the start!");
    }

    /**
     * Continue a stored run
     * @throws BaseException if no stored run
     */
    public void continueStoredRun() throws BaseException {
        checkStoredRun();
        setRun(ParkourRun.continueStoredParkourRun(storedRun));
        setStoredRun(null);
        teleport(map.getRespawnLocation(this));
        game.hMessagePlayer(this, "You are continuing your run at checkpoint #" + run.getLastCheckpoint() + "!");
        sendCurrentTime();
    }

    /** Get the time of this current run */
    public void sendCurrentTime() throws BaseException {
        checkRun();
        //Get the current time
        game.hMessagePlayer(this, "Current time: " + run.getCurrentTookTime()); //TODO Seconds/Minutes format
    }

    /**
     * Remove a saved run
     * @throws BaseException if no saved run
     */
    public void removeSavedRun() throws BaseException {
        checkStoredRun();
        setStoredRun(null);
        game.hMessagePlayer(this, "Your saved run has been removed!");
    }

    /**
     * Check if the player is on a run
     * @throws BaseException if not on a run
     */
    public void checkRun() throws BaseException {
        if (run == null) {
            throw new BaseException("You're currently not busy with a run!");
        }
    }


    /**
     * Check if the player has a stored run
     * @throws BaseException if no stored run
     */
    public void checkStoredRun() throws BaseException {
        if (storedRun == null) {
            throw new BaseException("You don't have a saved run on this map!");
        }
    }

    /**
     * Check if the player is on a map
     * @throws BaseException if not on a map
     */
    public void checkMap() throws BaseException {
        if (map == null) {
            throw new BaseException("You're not on a Parkour map!");
        }
    }


}
