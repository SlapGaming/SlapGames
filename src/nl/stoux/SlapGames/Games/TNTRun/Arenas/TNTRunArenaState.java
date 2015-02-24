package nl.stoux.SlapGames.Games.TNTRun.Arenas;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import nl.stoux.SlapGames.Games.Base.Arena.BaseArenaState;
import nl.stoux.SlapGames.Games.TNTRun.Models.TNTRunGameData;
import nl.stoux.SlapGames.Games.TNTRun.Models.TNTRunGameDataPlayer;
import nl.stoux.SlapGames.Games.TNTRun.TNTRunPlayer;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by Stoux on 22/02/2015.
 */
public class TNTRunArenaState extends BaseArenaState<TNTRunArena, TNTRunGameData, TNTRunGameDataPlayer> {

    /** The HashSet containing all the blocks on all floors */
    @Getter private HashSet<Block> destroyableBlocks;

    /** Possible spawn locations */
    private ArrayList<Location> spawnLocations;

    /** The death region */
    @Getter private ProtectedRegion deathRegion;

    /** The max number of double jumps */
    @Getter private int maxDoubleJumps;

    /** The Double Jump Power multiplier */
    @Getter private double doubleJumpPower = 0;

    public TNTRunArenaState(TNTRunArena arena) {
        super(arena);
        deathRegion = arena.getSettings().getDeathRegion().getValue();
        maxDoubleJumps = arena.getSettings().getMaxDoubleJumps().getValue();
        Integer power = arena.getSettings().getDoubleJumpPower().getValue();
        if (power != null) {
            doubleJumpPower = power / 10.0;
        }
    }

    @Override
    protected TNTRunGameData createGameData() {
        return new TNTRunGameData();
    }

    @Override
    protected TNTRunGameDataPlayer createGameDataPlayer(TNTRunGameData tntRunGameData, int playerID) {
        return new TNTRunGameDataPlayer(tntRunGameData.getGameID(), playerID);
    }

    @Override
    public void setupArena(int forPlayers) {
        destroyableBlocks = new HashSet<>();

        int floor = 1;
        //Load the floors
        for (Setting<ProtectedRegion> region : arena.getSettings().getFloors()) {
            //Check if set
            if (region.getValue() == null) {
                continue;
            }

            //Get all blocks
            List<Block> blocks = Util.getBlocksInRegion(region.getValue());

            //First floor, so get the spawn locations
            if (floor++ == 1) {
                //Create the list with locations
                spawnLocations = new ArrayList<>();

                //Set variables
                final int max = (forPlayers > blocks.size() ? blocks.size() : forPlayers);
                final Random rand = new Random();

                //Get locations
                IntStream.range(0, forPlayers).forEach(c ->  {
                    spawnLocations.add(blocks.get(rand.nextInt(max)).getLocation());
                });
            }

            //Add blocks to main hashset
            destroyableBlocks.addAll(blocks);
        }
    }

    @Override
    public void gameStarts(int nrOfPlayers) {

    }

    @Override
    public void playerWon(int playerID) {

    }

    /**
     * Teleport all players to spawn locations
     * @param players the players
     */
    public void teleport(Collection<TNTRunPlayer> players) {
        int pos = 0;
        for (TNTRunPlayer player : players) {
            player.teleport(spawnLocations.get(pos++));
        }
    }
}
