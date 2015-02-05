package nl.stoux.SlapGames.Games.Spleef;

import nl.stoux.SlapGames.Games.Base.BaseEventHandler;
import nl.stoux.SlapGames.Games.Base.GameState;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Stoux on 22/01/2015.
 */
public class SpleefEventHandler extends BaseEventHandler<Spleef, GamePlayer<Spleef>> {

    @Override
    public void onBlockDamage(GamePlayer<Spleef> gp, BlockDamageEvent event) {
        if (isPlaying()) {
            game.onTouchedBlock(gp, event.getBlock());
        }
    }

    @Override
    public void onPlayerInteract(GamePlayer<Spleef> gp, PlayerInteractEvent event) {
        if (isPlaying()) {
            game.onTouchedBlock(gp, event.getClickedBlock());
        }

    }

    @Override
    public void onPlayerMove(GamePlayer<Spleef> gp, PlayerMoveEvent event) {
        if (game.getGameState() == GameState.PLAYING && !event.isCancelled()) {
            game.onPlayerMove(gp, event.getTo());
        }
    }

    @Override
    public void onPlayerDied(GamePlayer<Spleef> gp, PlayerDeathEvent event) {
        //todo
    }

}
