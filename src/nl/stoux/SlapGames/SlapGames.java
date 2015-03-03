package nl.stoux.SlapGames;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Commands.Handler.CommandHandler;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Util.Log;
import nl.stoux.SlapGames.Util.Util;
import nl.stoux.SlapPlayers.SQL.DAO.DaoControl;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Stoux on 12/12/2014.
 */
public class SlapGames extends JavaPlugin {


    //<editor-fold desc="Singleton">
    /* Singleton */
    @Getter(AccessLevel.PUBLIC)
    private static SlapGames instance;
    //</editor-fold>

    @Getter(AccessLevel.PUBLIC)
    private WorldGuardPlugin worldGuard;

    /** The util instance */
    private Util util;

    @Override
    public void onEnable() {
        super.onEnable();

        //Set singleton
        SlapGames.instance = this;
        util = new Util(this, null, null); //TODO
        Log.intialize(getLogger());

        //Register tables with SlapPlayers DAO
        DaoControl.registerTables(getClass());

        //Create folders
        for (GameType gameType : GameType.values()) {
            File f = new File(getDataFolder() + File.separator + gameType.getFolder() + File.separator);
            if (!f.exists()) {
                f.mkdirs();
            }
        }

        //Get WorldGuard
        worldGuard = WorldGuardPlugin.inst();

        //Register the command
        new CommandHandler();

    }

    @Override
    public void onDisable() {
        super.onDisable();

        //Destroy the statics in the Util class
        util.destroy();
    }

}
