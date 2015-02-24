package nl.stoux.SlapGames.Games.Spleef.Arenas;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.SlapGames.Games.Base.Arena.BaseArenaState;
import nl.stoux.SlapGames.Games.Spleef.Models.SpleefGameData;
import nl.stoux.SlapGames.Games.Spleef.Models.SpleefGameDataPlayer;
import nl.stoux.SlapGames.Games.Spleef.Spleef;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;

/**
 * Created by Stoux on 23/01/2015.
 */
public class SpleefArenaState extends BaseArenaState<SpleefArena, SpleefGameData, SpleefGameDataPlayer> {

    /** The Set with blocks that can be destroyed */
    @Getter private HashSet<Block> blocks;
    /** The death region */
    @Getter private ProtectedRegion deathRegion;

    public SpleefArenaState(SpleefArena arena) {
        super(arena);
        this.blocks = new HashSet<>(Util.getBlocksInRegion(arena.getRegion()));
        SpleefArenaSettings settings = arena.getSettings();
        this.deathRegion = settings.getDeathRegion().getValue();
    }

    @Override
    protected SpleefGameData createGameData() {
        return new SpleefGameData();
    }

    @Override
    protected SpleefGameDataPlayer createGameDataPlayer(SpleefGameData spleefGameData, int playerID) {
        return new SpleefGameDataPlayer(spleefGameData.getGameID(), playerID);
    }

    @Override
    public void setupArena(int forPlayers) {
        arena.setRandomFloor();
    }

    @Override
    public void gameStarts(int nrOfPlayers) {
        gameData.setStartTime(System.currentTimeMillis());
        gameData.setNrOfPlayers(nrOfPlayers);
    }

    @Override
    public void playerWon(int playerID) {
        gameData.setWinningUserID(playerID);
        gameData.setFinishTime(System.currentTimeMillis());
    }

    /**
     * Check if this arena's floor contains a block
     * @param b The block
     * @return contains the block
     */
    public boolean containsBlock(Block b) {
        return blocks.contains(b);
    }

    /**
     * Teleport the players to random points on the spleef floor
     * @param players the players
     */
    public void teleport(Collection<GamePlayer<Spleef>> players) {
        List<Location> spawnLocations = new ArrayList<>();

        //Find spawn locations
        int highestY = 0;
        for (Block block : blocks) {
            //Get the location
            Location loc = block.getLocation();
            int blockY = loc.getBlockY();

            //Check if lower
            if (blockY < highestY) {
                continue;
            }

            //Check if higher
            if (loc.getBlockY() > highestY) {
                highestY = loc.getBlockY();
                spawnLocations.clear();
            }

            //Add to locations
            spawnLocations.add(block.getLocation());
        }

        //Shuffle the list
        Collections.shuffle(spawnLocations);

        //Teleport the players
        int x = 0;
        int size = spawnLocations.size();
        for (GamePlayer player : players) {
            player.teleport(spawnLocations.get(x++).add(0, 1.5, 0));
            if (x >= size) {
                x = 0;
            }
        }
    }

}
