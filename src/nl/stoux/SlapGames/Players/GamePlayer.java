package nl.stoux.SlapGames.Players;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.SlapGames.Games.Base.BaseGame;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;

/**
 * Created by Stoux on 12/12/2014.
 */
public class GamePlayer<T1 extends BaseGame> {

    /** The actual player */
    @Getter(AccessLevel.PUBLIC)
    protected Player player;

    /** The playername */
    @Getter(AccessLevel.PUBLIC)
    protected String playername;

    /** The game the player is in */
    @Getter(AccessLevel.PUBLIC)
    protected T1 game;

    /** The current state of the player */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    protected PlayerState playerState;

    /** The time the player joined this minigame */
    @Getter(AccessLevel.PUBLIC)
    protected long joinTime;

    /** The player is currently spectating */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    protected boolean spectating;

    public GamePlayer(Player player, T1 game) {
        this.player = player;
        this.game = game;
        playerState = PlayerState.JOINING;
        playername = player.getName();
        joinTime = System.currentTimeMillis();
        spectating = false;
    }

    /**
     * Send one or more messages to the Player
     * @param message the player
     */
    public void sendMessage(String... message) {
        player.sendMessage(message);
    }

    /**
     * Teleport a player to a location
     * @param location The location
     */
    public void teleport(Location location) {
        player.teleport(location);
    }

    /** Reset the player's stats, inv, etc */
    public void resetPlayer(){
        //Check if alive and online
        if (player.isDead() || !player.isOnline()) {
            return;
        }

        //Wipe XP
        player.setExp(0);
        player.setLevel(0);

        //Health
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);

        //Wipe potions
        removeAllPotions();

        //Clear inventory
        player.getInventory().clear();
        player.getInventory().setContents(new ItemStack[4]);

        //Remove vehicles
        player.eject();
        player.leaveVehicle();

        //Set player stats
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);
    }

    /**
     * Give the player a potion for an infinite amount of time with strength 1
     * @param type The type of potion
     */
    public void giveInfinitePotion(PotionEffectType type) {
        giveInfinitePotion(type, 1);
    }

    /**
     * Give the player a potioneffect for an infinte amount of time
     * @param type The type of potion
     * @param strength The strength of the potion
     */
    public void giveInfinitePotion(PotionEffectType type, int strength) {
        player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, strength));
    }

    /**
     * Remove all the current potion effects from the player
     */
    public void removeAllPotions() {
        HashSet<PotionEffect> effects = new HashSet<>(player.getActivePotionEffects());
        for (PotionEffect effect : effects) {
            player.removePotionEffect(effect.getType());
        }
    }

}
