package nl.stoux.SlapGames.Games.Parkour;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.stoux.SlapGames.Exceptions.BaseException;
import nl.stoux.SlapGames.Games.Base.BaseEventHandler;
import nl.stoux.SlapGames.Games.Parkour.Maps.ParkourMap;
import nl.stoux.SlapGames.Games.Parkour.Maps.ParkourMapSettings;
import nl.stoux.SlapGames.Games.Parkour.Models.ParkourMapAccess;
import nl.stoux.SlapGames.Games.Parkour.Models.ParkourRun;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.Util.Util;
import nl.stoux.SlapPlayers.Util.SUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by Stoux on 24/01/2015.
 */
public class ParkourEventHandler extends BaseEventHandler<Parkour, ParkourPlayer> {

    private RegionManager manager;

    public ParkourEventHandler() {
        manager = Util.getPlugin().getWorldGuard().getRegionManager(Util.getGameWorld());
    }

    @Override
    public void onPlayerMove(ParkourPlayer gp, PlayerMoveEvent event) {
        super.onPlayerMove(gp, event);

        //Check if playing
        if (event.isCancelled() || !isPlaying(gp)) {
            return;
        }

        //Get the map
        ParkourMap map = gp.getMap();
        if (map == null) {
            return;
        }

        ParkourMapAccess model = map.getParkourMapAccess();
        Location to = event.getTo();


        //Check death height
        if (to.getBlockY() <= model.getDeathHeight()) {
            map.playerDies(gp);
            return;
        }

        //Get the regions on this location
        for (ProtectedRegion protectedRegion : manager.getApplicableRegions(to)) {
            //Check if there's an action on this region
            Consumer<ParkourPlayer> consumer = model.getActionByRegion(protectedRegion.getId());
            if (consumer == null) {
                continue;
            }

            //Execute the action | Execute only one action per loop
            consumer.accept(gp);
            break;
        }
    }

    @Override
    public void onPlayerInteract(ParkourPlayer gp, PlayerInteractEvent event) {
        if (gp.getMap() == null) {
            //Not on a map yet
            if (SUtil.contains(event.getAction(), Action.PHYSICAL, Action.RIGHT_CLICK_BLOCK)) {
                Location blockLocation = event.getClickedBlock().getLocation();

                //Find the pad
                ParkourSettings.Pad foundPad = null;
                for (ParkourSettings.Pad pad : game.getSettings().getPads()) {
                    if (Util.isSameBlock(pad.getPadLocation().getValue(), blockLocation)) {
                        foundPad = pad;
                        break;
                    }
                }
                //=> Check if pad found
                if (foundPad == null) {
                    return;
                }

                //Get the map linked to that pad
                ParkourMap map = game.getPadToMap().get(foundPad);
                if (map == null) {
                    return;
                }


                //Join the map
                if (gp.getPlayerState() == PlayerState.SPECTATOR) {
                    //Check if the map supports spectators
                    if (!map.getSettings().getSpectators().getValue()) {
                        game.hMessagePlayer(gp, "This map doesn't support spectators!");
                        return;
                    }

                    //Join the map
                    gp.setMap(map);
                    gp.resetPlayer();
                    game.setSpectating(gp);
                } else {
                    //Join the map
                    gp.setMap(map);
                    gp.resetPlayer();
                    gp.givePowertools();
                    gp.teleport(map.getSettings().getLobby().getValue());

                    //Make the player invisible
                    gp.giveInfinitePotion(PotionEffectType.INVISIBILITY);

                    //Check if the player has a stored run
                    //TODO Check for stored run
                }
            }
        } else {
            ParkourMap map = gp.getMap();
            ParkourMapSettings mSetttings = map.getSettings();

            //Check if one of the buttons
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && mSetttings.getStoreCheckpointProgress().getValue()) {
                Location clickedBlock = event.getClickedBlock().getLocation();
                if (Util.isSameBlock(mSetttings.getContinueButton().getValue(), clickedBlock)) {
                    //Clicked the continue button
                    //TODO
                    return;
                } else if (Util.isSameBlock(mSetttings.getResetButton().getValue(), clickedBlock)) {
                    //CLicked the reset button
                    gp.setStoredRun(null);
                    gp.setRun(null);
                    game.hMessagePlayer(gp, "Your run has been reset!");
                    return;
                }
            }

            //Check if powertooling
            if (SUtil.contains(event.getAction(), Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR)) {
                ParkourPlayer.Powertool pt = gp.isHoldingPowertool();
                if (pt != null) {
                    try {
                        switch (pt) {
                            case RESTART_CHECKPOINT:
                                //Check if there's a checkpoint to go back to | Otherwise full restart
                                if (gp.getRun() != null) {
                                    int lastCheckpoint = gp.getRun().getLastCheckpoint();
                                    if (lastCheckpoint > 0) {
                                        gp.teleport(gp.getMap().getRespawnLocation(gp));
                                        game.hMessagePlayer(gp, "You have been warped back to checkpoint #" + lastCheckpoint + "!");
                                        break;
                                    }
                                }
                            case RESTART:
                                //Restart the run
                                gp.setRun(null);
                                gp.teleport(gp.getMap().getSettings().getLobby().getValue());
                                game.hMessagePlayer(gp, "You've been warped back to the start!");
                                break;

                            case CONTINUE:
                                //Continue from a stored run
                                checkStoredRun(gp);
                                gp.setRun(ParkourRun.continueStoredParkourRun(gp.getStoredRun()));
                                gp.setStoredRun(null);
                                gp.teleport(gp.getMap().getRespawnLocation(gp));
                                game.hMessagePlayer(gp, "You are continueing your run at checkpoint #" + gp.getRun().getLastCheckpoint() + "!");

                            case TIME:
                                //Get the current time
                                game.hMessagePlayer(gp, "Current time: " + gp.getRun().getCurrentTookTime());
                                break;

                            case RESET:
                                checkStoredRun(gp);
                                gp.setStoredRun(null);
                                game.hMessagePlayer(gp, "Your saved run has been reset!");
                                break;
                        }
                    } catch (BaseException e) {
                        game.hMessagePlayer(gp, e.getMessage());
                    }
                }
            }

        }
    }

    /**
     * Check if the player has a stored run
     * @param gp The player
     * @throws BaseException if no stored run
     */
    private void checkStoredRun(ParkourPlayer gp) throws BaseException {
        if (gp.getStoredRun() == null) {
            throw new BaseException("You don't have a saved run on this map!");
        }
    }


    @Override
    public void onPlayerDied(ParkourPlayer gp, PlayerDeathEvent event) {
        if (gp.getMap() != null) {
            gp.getMap().playerDies(gp);
        }
    }

    @Override
    public void onRespawnEvent(ParkourPlayer gp, PlayerRespawnEvent event) {
        if (gp.isOnMap()) {
            event.setRespawnLocation(gp.getMap().getRespawnLocation(gp));
        } else {
            super.onRespawnEvent(gp, event);
        }
    }

    @Override
    public void onPlayerDamage(ParkourPlayer gp, EntityDamageEvent event) {
        //Allow damage but heavily nerf the amount
        if (!isCause(event.getCause(), EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.DROWNING, EntityDamageEvent.DamageCause.SUFFOCATION)) {
            gp.getPlayer().setHealth(20);
            event.setDamage(0.1); //TODO See if 0.0 is possible?
        }
    }

    /**
     * Check if one of the options is the cause
     * @param cause The cause
     * @param options The options
     * @return is the cause
     */
    private boolean isCause(EntityDamageEvent.DamageCause cause, EntityDamageEvent.DamageCause... options) {
        return Arrays.stream(options).filter(c -> c == cause).count() > 0;
    }

    @Override
    public void onPlayerTeleport(ParkourPlayer gp, PlayerTeleportEvent event) {
        super.onPlayerTeleport(gp, event);

        //Check if busy with a run
        if (!event.isCancelled() && isPlaying(gp) && gp.getRun() != null && !gp.getRun().isFinished()) {
            if (gp.getAllowedTeleports() > 0) {
                //Allowed to teleport
                gp.setAllowedTeleports(gp.getAllowedTeleports() - 1);
            } else {
                //Not allowed -> aka cheating
                event.setCancelled(true);
                playerCheating(gp, "teleporting");
            }
        }
    }

    @Override
    public void onPlayerChangeGameMode(ParkourPlayer gp, PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() != GameMode.SURVIVAL) {
            event.setCancelled(true);
            playerCheating(gp, "changing GameMode");
        }
    }

    @Override
    public void onPlayerToggleFly(ParkourPlayer gp, PlayerToggleFlightEvent event) {
        if (gp.getPlayerState() == PlayerState.PLAYING) {
            event.setCancelled(true);
            playerCheating(gp, "flying");
        }
    }

    /**
     * Called when a player is cheating
     * @param player The cheating player
     * @param reason The reason in a couple of words
     */
    private void playerCheating(ParkourPlayer player, String reason) {
        //Reset run
        player.setRun(null);
        player.setStoredRun(null);
        player.teleport(player.getMap().getSettings().getLobby().getValue());

        //Broadcast
        game.broadcast("Oh oh.. " + player.getPlayername() + " just tried to cheat by " + reason + "!");
    }

}
