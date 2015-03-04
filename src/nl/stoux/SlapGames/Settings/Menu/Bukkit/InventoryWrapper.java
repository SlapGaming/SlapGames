package nl.stoux.SlapGames.Settings.Menu.Bukkit;

import lombok.Getter;
import nl.stoux.SlapGames.Settings.Menu.Events.OptionClickEvent;
import nl.stoux.SlapGames.Settings.Menu.Events.OptionClickHandler;
import nl.stoux.SlapGames.Settings.Menu.IconMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Stoux on 04/03/2015.
 */
public class InventoryWrapper {

    /** The inventory */
    @Getter private Inventory inventory;

    /** The EventHandler for this inventory */
    private OptionClickHandler eventHandler;

    /** The keys of the items */
    private String[] optionKeys;

    public InventoryWrapper(Inventory inventory, OptionClickHandler eventHandler) {
        this.eventHandler = eventHandler;
        this.inventory = inventory;
        optionKeys = new String[inventory.getSize()];
    }

    /**
     * Get the key for a certain position
     * @param position The position of the item
     * @return The key
     */
    public String getKey(int position) {
        return optionKeys[position];
    }

    /**
     * Set an item in the inventory
     * @param position The position in the inventory (starts at 0)
     * @param item The item
     * @param key The key (will use toString())
     * @param name The displayname
     * @param lore The lore for the item
     * @return Itself for chaining
     */
    public InventoryWrapper setItem(int position, ItemStack item, Object key, String name, String... lore) {
        //Set the name & lore of the item
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);

        //Set the key
        optionKeys[position] = key.toString();

        //Set the item in the inventory
        inventory.setItem(position, item);

        //Return itself for chaining
        return this;
    }

    /**
     * Set an item in the inventory
     * Name == Key
     * @param position The position in the inventory (starts at 0)
     * @param item The item
     * @param name The displayname & the key
     * @param lore The lore for the item
     * @return Itself for chaining
     */
    public InventoryWrapper setItem(int position, ItemStack item, String name, String... lore) {
        return setItem(position, item, name, name, lore);
    }

    /**
     * An item has been clicked
     * @param event the event
     * @return the used OptionClickEvent
     */
    public OptionClickEvent clicked(IconMenu menu, InventoryClickEvent event) {
        OptionClickEvent optionClickEvent = new OptionClickEvent(menu, (Player) event.getWhoClicked(), event.getRawSlot(), getKey(event.getRawSlot()));
        eventHandler.onOptionClick(optionClickEvent);
        return optionClickEvent;
    }

}
