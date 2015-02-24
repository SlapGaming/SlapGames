package nl.stoux.SlapGames.Games.TNTRun;

import nl.stoux.SlapGames.Games.Base.BaseEventHandler;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by Stoux on 26/01/2015.
 */
public class TNTRunEventHandler extends BaseEventHandler<TNTRun, TNTRunPlayer> {

    @Override
    public void onPlayerMove(TNTRunPlayer gp, PlayerMoveEvent event) {
        super.onPlayerMove(gp, event);

        //Check if not canceled & playing
        if (isPlaying(gp) && !event.isCancelled()) {
            //Check if the player touched the death region
            if (Util.containsLocation(gp.getArenaState().getDeathRegion(), event.getTo())) {
                game.playerLost(gp);
            } else if (gp.isDoubleJumping()) {
                //Check if on ground to reset
                Location loc = gp.getPlayer().getLocation();
                if (loc.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                    //TODO More checks, maybe check direction?
                    gp.setDoubleJumping(false);
                }
            }
        }
    }

    @Override
    public void onPlayerToggleFly(TNTRunPlayer gp, PlayerToggleFlightEvent event) {
        if (!isPlaying(gp)) {
            return;
        }

        //Check if allowed to use a DoubleJump
        if (gp.getDoubleJumpsUsed() >= gp.getArenaState().getMaxDoubleJumps() || gp.isDoubleJumping()) {
            event.setCancelled(true);
            return;
        }

        //Increment the double jumps
        gp.setDoubleJumpsUsed(gp.getDoubleJumpsUsed() + 1);

        //Boost the player
        Player player = gp.getPlayer();
        player.setFlying(false);
        player.playSound(player.getLocation(), Sound.SHOOT_ARROW , 5, -500);
        Vector jump = player.getLocation().getDirection().multiply(gp.getArenaState().getDoubleJumpPower()).setY(1.2);
        player.setVelocity(player.getVelocity().add(jump));
        player.setAllowFlight(false);
        event.setCancelled(true);
    }

    @Override
    public void onPlayerToggleSneak(TNTRunPlayer gp, PlayerToggleSneakEvent event) {
        if (!isPlaying(gp)) {
            return;
        }

        event.setCancelled(true);
        //TODO Prevent better
    }

    @Override
    public void onPlayerDied(TNTRunPlayer gp, PlayerDeathEvent event) {
        if (isPlaying(gp)) {
            game.playerLost(gp);
        }
    }

}
