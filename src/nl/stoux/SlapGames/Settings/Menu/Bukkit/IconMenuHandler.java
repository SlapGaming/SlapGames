package nl.stoux.SlapGames.Settings.Menu.Bukkit;

import lombok.Getter;
import lombok.Setter;
import nl.stoux.SlapGames.Settings.Menu.Events.OptionClickEvent;
import nl.stoux.SlapGames.Settings.Menu.IconMenu;
import nl.stoux.SlapGames.SlapGames;
import nl.stoux.SlapGames.Util.Schedule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;

/**
 * Created by Stoux on 04/03/2015.
 */
@Getter @Setter
public class IconMenuHandler implements Listener {

    private final IconMenu iconMenu;

    /** Should destroy on close */
    private boolean destroy = true;

    public IconMenuHandler(IconMenu iconMenu) {
        this.iconMenu = iconMenu;
        register();
    }

    /** Register this handler as a listener */
    private void register() {
        SlapGames.getInstance().getServer().getPluginManager().registerEvents(this, SlapGames.getInstance());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent closeEvent) {
        if (!is(closeEvent)) return;

        //Destroy the IconMenu if needed, otherwise just remove the inventory
        if (destroy) {
            iconMenu.destroy();
        } else {
            iconMenu.getInventories().remove(closeEvent.getInventory());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent clickEvent) {
        if (!is(clickEvent)) return;

        //Always cancel the event
        clickEvent.setCancelled(true);

        //Pass the event through
        OptionClickEvent optionEvent = get(clickEvent).clicked(iconMenu, clickEvent);

        //Check if close
        if (optionEvent.isClose() || optionEvent.isDestroy()) {
            Schedule.runLater(() -> {clickEvent.getWhoClicked().closeInventory();}, 1);
        }

        //Check if destroy
        if (optionEvent.isDestroy()) {
            iconMenu.destroy();
        }
    }

    /**
     * Check if this is an InventoryEvent for one of the inventories that belong to this IconMenu(Handler)
     * @param event The event
     * @return belongs to IconMenu
     */
    private boolean is(InventoryEvent event){
        return iconMenu.getInventories().containsKey(event.getInventory());
    }

    /**
     * Get the InventoryWrapper that wraps the inventory in the Event
     * @param event the event
     * @return The wrapper
     */
    private InventoryWrapper get(InventoryEvent event) {
        return iconMenu.getInventories().get(event.getInventory());
    }

}
