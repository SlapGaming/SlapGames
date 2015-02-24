package nl.stoux.SlapGames.Games.Spleef;

import nl.stoux.SlapGames.Games.Base.Arena.BaseVersusArenaGame;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Games.Spleef.Arenas.SpleefArena;
import nl.stoux.SlapGames.Games.Spleef.Arenas.SpleefArenaState;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.Storage.YamlFile;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by Stoux on 22/02/2015.
 */
public class Spleef extends BaseVersusArenaGame<SpleefEventHandler, GamePlayer<Spleef>, SpleefSettings, SpleefArena, SpleefArenaState> {

    public Spleef() {
        super(GameType.SPLEEF, 3);
    }

    //<editor-fold desc="Create functions">
    @Override
    protected SpleefEventHandler createHandler() {
        return new SpleefEventHandler();
    }

    @Override
    protected SpleefSettings createSettings() {
        return new SpleefSettings(Util.getYamlFile(gameType, "config"));
    }

    @Override
    protected GamePlayer<Spleef> createGamePlayer(Player player) {
        return new GamePlayer<>(player, this);
    }

    @Override
    protected SpleefArena createArena(YamlFile file) {
        return new SpleefArena(file);
    }
    //</editor-fold>

    @Override
    protected void startingIn(int secondsLeft) {
        switch (secondsLeft) {
            case 3:
                //Teleport the players
                currentArena.teleport(getPlayers(PlayerState.PLAYING));
                forEachPlayer(gp -> setSpectating(gp), PlayerState.SPECTATOR);
                break;

            case 2:
                hMessagePlayers("Get ready...");
                break;

            case 1:
                //TODO Enable specials (potions, etc)
                hMessagePlayers("Set...");
                break;

            case 0:
                //Create the SpleefGame
                currentArena.gameStarts(countPlayers(PlayerState.PLAYING));
                hMessagePlayers("Spleef!");
                break;
        }
    }


    @Override
    protected SpleefArenaState selectArena() {
        SpleefArena selectedArena = enabledArenas.get(Util.getRandom().nextInt(enabledArenas.size())); //TODO Replace with vote based system
        return new SpleefArenaState(selectedArena);
    }


    //<editor-fold desc="Events">
    /**
     * A player who is playing while the arena is running clicks a block
     * @param player The player
     * @param clickedBlock The block
     */
    public void onTouchedBlock(GamePlayer<Spleef> player, Block clickedBlock) {
        if (currentArena.containsBlock(clickedBlock)) {
            //Remove the block
            clickedBlock.setType(Material.AIR);
        }
    }

    /**
     * A player moves
     * @param player The player
     * @param to To location
     */
    public void onPlayerMove(GamePlayer<Spleef> player, Location to) {
        if (Util.containsLocation(currentArena.getDeathRegion(), to)) {
            //Player dies
            playerLost(player);
        }
    }
    //</editor-fold>

}
