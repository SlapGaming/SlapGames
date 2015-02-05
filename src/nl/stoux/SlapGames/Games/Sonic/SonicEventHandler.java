package nl.stoux.SlapGames.Games.Sonic;

import nl.stoux.SlapGames.Games.Base.BaseEventHandler;
import nl.stoux.SlapGames.Games.Base.GameState;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.Util.Schedule;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Stoux on 23/01/2015.
 */
public class SonicEventHandler extends BaseEventHandler<Sonic, SonicPlayer> {

    private SonicSettings settings;

    public SonicEventHandler() {
        settings = game.getSettings();
    }

    @Override
    public void onPlayerInteract(SonicPlayer gp, PlayerInteractEvent event) {
        //Check if a button was pressed
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material type = event.getClickedBlock().getType();
            if (type == Material.WOOD_BUTTON || type == Material.STONE_BUTTON) {
                Location blockLoc = event.getClickedBlock().getLocation();
                //Only looking for one of 3 buttons
                if (Util.isSameBlock(settings.getSpawnToRaceButton().getValue(), blockLoc) || Util.isSameBlock(settings.getTutorialToRaceButton().getValue(), blockLoc)) {
                    // => Race
                    gp.teleport(settings.getRacetrack().getValue());
                    gp.setPlayerState(PlayerState.PLAYING);
                    game.hMessagePlayer(gp, "Teleported to the racetrack!");
                } else if (Util.isSameBlock(settings.getSpawnToTutorialButton().getValue(), blockLoc)) {
                    // => Tutorial
                    gp.teleport(settings.getTutorial().getValue());
                    game.hMessagePlayer(gp, "Teleported to the tutorial!");
                }
            }
        }
    }

    @Override
    public void onPlayerMove(SonicPlayer gp, PlayerMoveEvent event) {
        super.onPlayerMove(gp, event);

        Location to = event.getTo();
        //Check if playing
        if (!event.isCancelled() && gp.getPlayerState() == PlayerState.PLAYING && game.getGameState() == GameState.PLAYING) {
            //Check which checkpoint the players needs
            if (!gp.isRunning()) {
                //Looking for the start
                if (isOnStartFinishLine(to)) {
                    gp.startNewRun();
                    game.hMessagePlayers(gp.getPlayername() + " has started racing!");
                }
            } else if (gp.getRun().getLastCheckpoint() == 5) {
                //Looking for the finish
                if (isOnStartFinishLine(to)) {
                    gp.finishedRun();
                    game.hMessagePlayers(gp.getPlayername() + " finished with a time of "); //TODO Add time
                }
            } else {
                //Check if on Checkpoint
                int lastCheckpoint = gp.getRun().getLastCheckpoint();
                if (Util.containsLocation(settings.getCheckpoints()[lastCheckpoint].getValue(), to)) {
                    gp.getRun().passedCheckpoint(++lastCheckpoint);
                    game.hMessagePlayer(gp, "You passed checkpoint " + lastCheckpoint + "! (Time: "); //TODO Add time
                }

                //Check for jump
                int lastJump = gp.getRun().getLastJump();
                if (lastJump < 5) {
                    if (Util.containsLocation(settings.getJumps()[lastJump].getValue(), to)) {
                        gp.getRun().passedJump(lastJump + 1);
                    }
                }
            }
        }
    }

    /**
     * Check if the player is on the start/finish line
     * @param loc The location
     * @return is on start/finish line
     */
    private boolean isOnStartFinishLine(Location loc) {
        return Util.containsLocation(settings.getStartFinishLine().getValue(), loc);
    }

    @Override
    public void onPlayerDied(SonicPlayer gp, PlayerDeathEvent event) {
        if (gp.getPlayerState() == PlayerState.PLAYING) {
            gp.setPlayerState(PlayerState.LOBBY);
        }
        if (gp.isRunning()) {
            gp.setRunning(false);
        }
    }

    @Override
    public void onRespawnEvent(SonicPlayer gp, PlayerRespawnEvent event) {
        super.onRespawnEvent(gp, event);
        if (gp.getPlayerState() != PlayerState.SPECTATOR) {
            gp.becomeSonic();
        }
    }

    @Override
    public void onPlayerDamage(final SonicPlayer gp, EntityDamageEvent event) {
        Schedule.runLater(() -> {
            Player p = gp.getPlayer();
            //Check if actually still alive
            if (!p.isOnline() || p.isDead()) {
                return;
            }

            //Get the boots
            ItemStack boots = p.getInventory().getBoots();
            if (boots != null && boots.getType() == Material.LEATHER_BOOTS) {
                //Set to full health
                boots.setDurability((short) 0);
            }
        }, 1);
    }
}
