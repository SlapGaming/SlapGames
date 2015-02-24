package nl.stoux.SlapGames.Games.TNTRun;

import lombok.Getter;
import lombok.Setter;
import nl.stoux.SlapGames.Games.TNTRun.Arenas.TNTRunArenaState;
import nl.stoux.SlapGames.Players.GamePlayer;
import org.bukkit.entity.Player;

/**
 * Created by Stoux on 26/01/2015.
 */
public class TNTRunPlayer extends GamePlayer<TNTRun> {

    /** The number of times the player used shift */
    @Getter @Setter
    private int shiftWarnings = 0;

    /** The number of double jumps the player has used */
    @Getter @Setter
    private int doubleJumpsUsed = 0;

    /** Is currently double jumping */
    @Getter @Setter
    private boolean doubleJumping = false;

    /** The ArenaState the player is currently playing on */
    @Getter @Setter
    private TNTRunArenaState arenaState;

    public TNTRunPlayer(Player player, TNTRun game) {
        super(player, game);
    }

    @Override
    public void resetPlayer() {
        super.resetPlayer();
        shiftWarnings = 0;
        doubleJumpsUsed = 0;
        doubleJumping = false;
    }
}
