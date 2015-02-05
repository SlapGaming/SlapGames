package nl.stoux.SlapGames.Games.Parkour;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import nl.stoux.SlapGames.Games.Base.BaseGame;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Games.Parkour.Maps.ParkourMap;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

/**
 * Created by Stoux on 24/01/2015.
 */
public class Parkour extends BaseGame<ParkourEventHandler, ParkourPlayer, ParkourSettings> {

    private static final String GHOST_TEAM_NAME = "Parkour Ghosts";

    /** The ghost team that allows players to be see-through */
    private Team ghostTeam;

    /**
     * A HashMap linking the available pads to the maps
     * K: The pad
     * V: The map
     */
    @Getter private HashMap<ParkourSettings.Pad, ParkourMap> padToMap;


    public Parkour(GameType gameType) {
        super(gameType);
        padToMap = new HashMap<>();

        //Load maps

        //TODO Load maps

        //Create the ghost team
        Scoreboard scoreboard = Util.getPlugin().getServer().getScoreboardManager().getMainScoreboard();
        ghostTeam = scoreboard.getTeam(GHOST_TEAM_NAME);
        if (ghostTeam == null) {
            ghostTeam = scoreboard.registerNewTeam(GHOST_TEAM_NAME);
            ghostTeam.setCanSeeFriendlyInvisibles(true);
        }
    }

    //<editor-fold desc="Create Functions">
    @Override
    protected ParkourEventHandler createHandler() {
        return new ParkourEventHandler();
    }

    @Override
    protected ParkourSettings createSettings() {
        return new ParkourSettings(Util.getYamlFile(gameType, "config"));
    }

    @Override
    protected ParkourPlayer createGamePlayer(Player player) {
        return new ParkourPlayer(player, this);
    }
    //</editor-fold>

    @Override
    protected void newPlayerJoins(ParkourPlayer player) {
        setPlayerInLobby(player);
        ghostTeam.addPlayer(player.getPlayer());
    }

    @Override
    protected void newPlayerSpectates(ParkourPlayer player) {
        setSpectating(player);
        ghostTeam.addPlayer(player.getPlayer());
    }

    @Override
    protected void playerLeaves(ParkourPlayer player) {
        //Let the map handle the leave
        if (player.getMap() != null) {
            player.getMap().playerLeaves(player);
        }

        //Reset values
        player.setRun(null);
        player.setStoredRun(null);
        player.setMap(null);

        //Leave the ghost team
        ghostTeam.removePlayer(player.getPlayer());
    }

    @Override
    protected void spectatorLeaves(ParkourPlayer player) {
        setSpectating(player);
        ghostTeam.removePlayer(player.getPlayer());
    }


    //<editor-fold desc="Lobby/Out of bounds overrides">
    @Override
    public boolean canGoIntoSpectatorGameMode(ParkourPlayer player) {
        if (player.getMap() != null) {
            return player.getMap().getSettings().getSpectatorGameMode().getValue();
        } else {
            return super.canGoIntoSpectatorGameMode(player);
        }
    }

    @Override
    public ProtectedRegion getSpectatorOutOfBounds(ParkourPlayer player) {
        if (player.getMap() != null) {
            return player.getMap().getSettings().getSpecOutOfBoundsRegion().getValue();
        } else {
            return super.getSpectatorOutOfBounds(player);
        }
    }

    @Override
    public ProtectedRegion getOutOfBounds(ParkourPlayer player) {
        if (player.getMap() != null) {
            return player.getMap().getSettings().getOutOfBoundsRegion().getValue();
        } else {
            return super.getOutOfBounds(player);
        }
    }

    @Override
    public Location getSpectatorLocation(ParkourPlayer player) {
        if (player.getMap() != null) {
            return player.getMap().getSettings().getSpectatorLocation().getValue();
        } else {
            return super.getSpectatorLocation(player);
        }
    }

    @Override
    public Location getLobby(ParkourPlayer player) {
        if (player.isOnMap()) {
            return player.getMap().getSettings().getLobby().getValue();
        } else {
            return super.getLobby(player);
        }
    }
    //</editor-fold>



}
