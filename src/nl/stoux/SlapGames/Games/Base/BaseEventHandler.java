package nl.stoux.SlapGames.Games.Base;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.Util.Util;
import nl.stoux.SlapPlayers.Util.SUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

/**
 * Created by Stoux on 12/12/2014.
 */
@SuppressWarnings("unchecked")
public abstract class BaseEventHandler<T1 extends BaseGame, T2 extends GamePlayer> {

    /** The blocks a player is allowed to move into */
    private static final Material[] ALLOWED_SPECTATOR_BLOCKS = new Material[]{
            Material.AIR, Material.WATER, Material.WATER_LILY, Material.LAVA, Material.STATIONARY_LAVA,
            Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.LADDER, Material.GRASS,
            Material.VINE, Material.WHEAT, Material.WEB
    };

    /** The game */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    protected T1 game;

    public void onQuit(T2 gp, PlayerQuitEvent event) {
        game.playerQuits(gp);
    }

    public void onPlayerInteract(T2 gp, PlayerInteractEvent event) {}

    public void onPlayerMove(T2 gp, PlayerMoveEvent event){
        Location to = event.getTo();

        //Get regions
        ProtectedRegion outOfBoundsRegion = game.getOutOfBounds(gp);
        ProtectedRegion specOutOfBoundsRegion = game.getSpectatorOutOfBounds(gp);
        //=> Booleans
        boolean hasOutOfBounds = (outOfBoundsRegion != null);
        boolean hasSpecOutOfBounds = (specOutOfBoundsRegion != null);
        boolean spectating = gp.isSpectating();

        //Check for out of bounds
        if ((spectating && hasOutOfBounds && !hasSpecOutOfBounds) || (!spectating && hasOutOfBounds)) {
            if (!Util.containsLocation(outOfBoundsRegion, to)) {
                event.setCancelled(true);
            }
        } else if (spectating && hasSpecOutOfBounds) {
            if (!Util.containsLocation(specOutOfBoundsRegion, to)) {
                event.setCancelled(true);
                gp.teleport(game.getSpectatorLocation(gp));
            }
        }

        //Block no clipping
        if (!event.isCancelled() && isGmSpectating(gp) && !game.isAllowedToNoClip(gp)) {
            Material m = gp.getPlayer().getEyeLocation().getBlock().getType();
            if (!SUtil.contains(m, ALLOWED_SPECTATOR_BLOCKS)) {
                event.setCancelled(true);
            }
        }
    }

    public void onPlayerTeleport(T2 gp, PlayerTeleportEvent event){
        if (isGmSpectating(gp) && event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) {
            Location to = event.getTo();
            if (to.getWorld() != Util.getGameWorld() || Util.containsLocation(game.getSpectatorOutOfBounds(gp), to)) {
                event.setCancelled(true);
                game.hMessagePlayer(gp, "You can't teleport to there!");
            }
        }
    }

    public void onBlockDamage(T2 gp, BlockDamageEvent event) {
        event.setCancelled(true);
    }

    public void onFoodLevelChange(T2 gp, FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    public void onInventoryClickEvent(T2 gp, InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            game.hMessagePlayer(gp, "You're not allowed to take your armor off!");
            event.setCancelled(true);
        }
    }

    public void onDropItem(T2 gp, PlayerDropItemEvent event) {
        game.hMessagePlayer(gp, "You're not allowed to drop items!");
        event.setCancelled(true);
    }

    public void onItemPickup(T2 gp, PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    public abstract void onPlayerDied(T2 gp, PlayerDeathEvent event);

    public void onRespawnEvent(T2 gp, PlayerRespawnEvent event) {
        if (gp.isSpectating()) {
            gp.teleport(game.getSpectatorLocation(gp));
        } else {
            gp.teleport(game.getLobby(gp));
        }
    }

    public void onPlayerDamage(T2 gp, EntityDamageEvent event) {
        event.setCancelled(true);
    }

    public void onPlayerToggleSneak(T2 gp, PlayerToggleSneakEvent event) {}

    public void onPlayerChangeGameMode(T2 gp, PlayerGameModeChangeEvent event) {}

    public void onPlayerToggleFly(T2 gp, PlayerToggleFlightEvent event) {}

    /**
     * Check if the arena is playing
     * @return is playing
     */
    protected boolean isPlaying() {
        return game.getGameState() == GameState.PLAYING;
    }

    /**
     * Check if a player is in the PLAYING state
     * @param gp The player
     * @return is playing
     */
    protected boolean isPlaying(T2 gp) {
        return gp.getPlayerState() == PlayerState.PLAYING;
    }

    /**
     * Check if the player is spectating in GameMode.SPECTATOR gamemode
     * @param gp The player
     * @return is GM spectating
     */
    protected boolean isGmSpectating(T2 gp) {
        return (gp.isSpectating() && gp.getPlayer().getGameMode() == GameMode.SPECTATOR);
    }

}
