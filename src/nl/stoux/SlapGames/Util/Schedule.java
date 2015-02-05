package nl.stoux.SlapGames.Util;

import nl.stoux.SlapGames.SlapGames;
import nl.stoux.SlapPlayers.SQL.Dao;
import nl.stoux.SlapPlayers.SQL.DaoControl;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.SQLException;

/**
 * Created by Stoux on 23/01/2015.
 */
public class Schedule {

    /**
     * Get the BukkitScheduler
     * @return the scheduler
     */
    public static BukkitScheduler getScheduler() {
        return SlapGames.getInstance().getServer().getScheduler();
    }

    /**
     * Get the SlapGames plugin
     * @return the plugin
     */
    private static SlapGames sg() {
        return SlapGames.getInstance();
    }

    /**
     * Run a runnable in sync
     * @param runnable The runnable
     * @return the task id
     */
    public static Integer run(Runnable runnable) {
        return getScheduler().runTask(sg(), runnable).getTaskId();
    }

    /**
     * Run a runnable in sync after some time
     * @param runnable The runnable
     * @param delayInTicks The delay in ticks
     * @return the task id
     */
    public static Integer runLater(Runnable runnable, long delayInTicks) {
        return getScheduler().runTaskLater(sg(), runnable, delayInTicks).getTaskId();
    }


    /**
     * Run a runnable in sync on a timer
     * @param runnable The runnable
     * @param delayInTicks The initial delay in ticks
     * @param repeatInTicks The repeated delay in ticks
     * @return the task id
     */
    public static Integer runTimer(Runnable runnable, long delayInTicks, long repeatInTicks) {
        return getScheduler().runTaskTimer(sg(), runnable, delayInTicks, repeatInTicks).getTaskId();
    }

    /**
     * Run a runnable async
     * @param runnable The runnable
     * @return the task id
     */
    public static Integer runAsync(Runnable runnable) {
        return getScheduler().runTaskAsynchronously(sg(), runnable).getTaskId();
    }

    /**
     * Run a runnable async after some time
     * @param runnable The runnable
     * @param delayInTicks The delay in ticks
     * @return the task id
     */
    public static Integer runAsyncLater(Runnable runnable, long delayInTicks) {
        return getScheduler().runTaskLaterAsynchronously(sg(), runnable, delayInTicks).getTaskId();
    }


    /**
     * Run a runnable async on a timer
     * @param runnable The runnable
     * @param delayInTicks The initial delay in ticks
     * @param repeatInTicks The repeated delay in ticks
     * @return the task id
     */
    public static Integer runAsyncTimer(Runnable runnable, long delayInTicks, long repeatInTicks) {
        return getScheduler().runTaskTimerAsynchronously(sg(), runnable, delayInTicks, repeatInTicks).getTaskId();
    }

    /**
     * Insert a @Table class into the Database using the SlapPlayers ORM
     * @param o The object
     * @param <T> The class that has been annotated with @Table
     */
    public static <T extends Object> void insertASync(T o) {
        runAsync(() -> {
            //Create the dao
            Dao<T> dao = (Dao<T>) DaoControl.createDAO(o.getClass());
            try {
                //Try to insert
                dao.insert(o);
            } catch (SQLException e) {
                Log.severe("[SQL] Failed to insert " + o.getClass().getName() + " entry: " + e.getMessage());
            } finally {
                //Destroy the dao
                dao.destroy();
            }
        });
    }


}
