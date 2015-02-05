package nl.stoux.SlapGames.Games.Sonic;

import nl.stoux.SlapGames.Games.Base.BaseGame;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Players.PlayerState;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.entity.Player;

/**
 * Created by Stoux on 23/01/2015.
 */
public class Sonic extends BaseGame<SonicEventHandler, SonicPlayer, SonicSettings> {

    public Sonic() {
        super(GameType.SONIC);
    }

    //<editor-fold desc="Create Methods">
    @Override
    protected SonicEventHandler createHandler() {
        return new SonicEventHandler();
    }

    @Override
    protected SonicSettings createSettings() {
        return new SonicSettings(Util.getYamlFile(gameType, "config"));
    }

    @Override
    protected SonicPlayer createGamePlayer(Player player) {
        return new SonicPlayer(player, this);
    }
    //</editor-fold>

    //<editor-fold desc="Join/Leave Methods">
    @Override
    protected void newPlayerJoins(SonicPlayer player) {
        setPlayerInLobby(player);
        player.becomeSonic();
    }

    @Override
    protected void newPlayerSpectates(SonicPlayer player) {
        player.setPlayerState(PlayerState.SPECTATOR);
        setSpectating(player);
    }

    @Override
    protected void playerLeaves(SonicPlayer player) {}

    @Override
    protected void spectatorLeaves(SonicPlayer player) {}
    //</editor-fold>

}
