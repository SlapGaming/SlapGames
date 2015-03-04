package nl.stoux.SlapGames.Settings.Menu.Events;

import lombok.Data;
import nl.stoux.SlapGames.Settings.Menu.IconMenu;
import org.bukkit.entity.Player;

/**
 * Created by Stoux on 04/03/2015.
 */
@Data
public class OptionClickEvent {

    /** The IconMenu it's bound to */
    private final IconMenu iconMenu;

    /** The player clicking */
    private final Player player;

    /** The clicked position in the IconMenu */
    private final int position;

    /** The key of the clicked item */
    private final String key;

    /** Should close the inventory */
    private boolean close = false;

    /** Should destroy the IconMenu */
    private boolean destroy = false;

}
