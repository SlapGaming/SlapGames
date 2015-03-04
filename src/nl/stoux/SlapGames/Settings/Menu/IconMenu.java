package nl.stoux.SlapGames.Settings.Menu;

import lombok.Getter;
import nl.stoux.SlapGames.Settings.Menu.Bukkit.IconMenuHandler;
import nl.stoux.SlapGames.Settings.Menu.Bukkit.InventoryWrapper;
import nl.stoux.SlapGames.Settings.Menu.Events.OptionClickHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

/**
 * Created by Stoux on 04/03/2015.
 */
public class IconMenu {

    /** Players that have IconMenu */
    private static HashMap<Player, IconMenu> playerToMenus = new HashMap<>();

    /** All inventories that belong to this IconMenu */
    @Getter private HashMap<Inventory, InventoryWrapper> inventories;

    /** The EventHandler */
    @Getter private IconMenuHandler eventHandler;

    /** The player who owns this IconMenu */
    private Player player;

    public IconMenu(Player player) {
        this.player = player;
        inventories = new HashMap<>();
        eventHandler = new IconMenuHandler(this);

        //Store in the map
        playerToMenus.put(player, this);
    }

    /**
     * Create a WrappedInventory
     * @param title The title of the Inventory
     * @param size  The size of the inventory (needs to be a multitude of 9)
     * @param handler The EventHandler when someone clicks on an Item
     * @return
     */
    public InventoryWrapper createInventory(String title, int size, OptionClickHandler handler) {
        //Create the inventory & wrap it
        InventoryWrapper wrapper = new InventoryWrapper(
                Bukkit.createInventory(player, size, title),
                handler
        );

        //Store it
        inventories.put(wrapper.getInventory(), wrapper);

        //Return it
        return wrapper;
    }

    /** Destroy this IconMenu */
    public void destroy() {
        //Clear the inventories
        inventories = null;

        //Unregister the EventHandler
        HandlerList.unregisterAll(eventHandler);
        eventHandler = null;

        //Remove from map
        playerToMenus.remove(player);
        player = null;
    }


}
