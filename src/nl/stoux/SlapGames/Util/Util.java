package nl.stoux.SlapGames.Util;

import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.SchematicReader;
import com.sk89q.worldedit.extent.clipboard.io.SchematicWriter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Games.GameType;
import nl.stoux.SlapGames.Players.GamePlayer;
import nl.stoux.SlapGames.Players.PlayerControl;
import nl.stoux.SlapGames.SlapGames;
import nl.stoux.SlapGames.Storage.YamlFile;
import nl.stoux.SlapPlayers.Control.UUIDControl;
import nl.stoux.SlapPlayers.Model.Profile;
import nl.stoux.SlapPlayers.SlapPlayers;
import nl.stoux.SlapPlayers.Util.SQLPool;
import nl.stoux.SlapPlayers.Util.SUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Stoux on 22/01/2015.
 */
public class Util {

    @Getter(AccessLevel.PUBLIC)
    private static SlapGames plugin;

    @Getter(AccessLevel.PUBLIC)
    private static PlayerControl playerControl;

    @Getter(AccessLevel.PUBLIC)
    private static World gameWorld;

    @Getter(AccessLevel.PUBLIC)
    private static com.sk89q.worldedit.world.World weGameWorld;

    @Getter(AccessLevel.PUBLIC)
    private static Random random;

    public Util(SlapGames plugin, PlayerControl playerControl, World world) {
        Util.plugin = plugin;
        Util.playerControl = playerControl;
        Util.gameWorld = world;
        Util.weGameWorld = new BukkitWorld(gameWorld);
        Util.random = new Random();
    }

    /** Destroy the Util class */
    public void destroy() {
        Util.plugin = null;
        Util.playerControl = null;
        Util.gameWorld = null;
        Util.weGameWorld = null;
        Util.random = null;
    }

    //<editor-fold desc="Hub Functions">
    /**
     * Get the location of the hub/spawn
     * @return The hub locaiton
     */
    public static Location getHubLocation() {
        return gameWorld.getSpawnLocation();
    }

    /**
     * Teleport a player to the games hub
     * @param player The player
     */
    public static void toHub(Player player){
        player.teleport(getGameWorld().getSpawnLocation());
    }

    /**
     * Teleport a player to the games hub
     * @param player The player
     */
    public static void toHub(GamePlayer player) {
        toHub(player.getPlayer());
    }
    //</editor-fold>

    //<editor-fold desc="WorldEdit/WorldGuard Functions">
    /**
     * Get a Region by its id
     * @param id The id
     * @return The region or null
     */
    public static ProtectedRegion getRegion(String id) {
        return SlapGames.getInstance().getWorldGuard().getRegionManager(gameWorld).getRegion(id);
    }

    /**
     * Check if a region contains a location
     * @param region The region
     * @param loc The location
     * @return contains the loc
     */
    public static boolean containsLocation(ProtectedRegion region, Location loc) {
        return region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Get all blocks from a WorldGuard region
     * @param region The region
     * @return The list with blocks in that region
     */
    public static List<Block> getBlocksInRegion(ProtectedRegion region) {
        List<Block> blocks = new ArrayList<>();

        //Check region type
        Region targetRegion = toWorldEditRegion(region);

        //Get the blocks
        targetRegion.forEach(bl -> blocks.add(getBlock(bl)));

        //Return the list
        return blocks;
    }


    /**
     * Save a schematic from a WorldGuard region
     * @param region The region
     * @param path The path to the file. The last entry should be the filename
     * @return saved
     */
    public static boolean saveSchematic(ProtectedRegion region, String... path) {
        try {
            //Create the file
            File f = createSchemFile(path);

            //Create the clipboard from the region
            BlockArrayClipboard clipboard = new BlockArrayClipboard(Util.toWorldEditRegion(region));

            //Create & write the schematic
            new SchematicWriter(new NBTOutputStream(new FileOutputStream(f)))
                    .write(clipboard, Util.getWeGameWorld().getWorldData());

            return true;
        } catch (Exception e) {
            Log.warn("Failed to save schematic: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load a Schematic into a Clipboard
     * @param path The path to the file. The last entry should be the filename
     * @return the clipboard or null
     */
    public static Clipboard loadSchematic(String... path) {
        //Create the file
        File f = createSchemFile(path);

        try {
            //Read the file
            Clipboard clipboard = new SchematicReader(new NBTInputStream(new FileInputStream(f)))
                    .read(weGameWorld.getWorldData());

            //Return the clipboard
            return clipboard;
        } catch (Exception e) {
            Log.warn("Failed to load schematic: " + e.getMessage());
            return null;
        }
    }

    /**
     * Create a file directing at a schematic
     * @param path The path to the file. The last entry should be the filename
     * @return The file
     */
    private static File createSchemFile(String... path) {
        return new File(SlapGames.getInstance().getDataFolder() + File.separator + SUtil.combineToString(path, File.separator, s -> s) + ".schem");
    }


    /**
     * Paste a clipboard into the world
     * @param clipboard The clipboard
     * @return pasting
     */
    public static boolean pasteClipboard(Clipboard clipboard) {
        //Check if clipboard is not null
        if (clipboard == null) {
            return false;
        }

        //Create the ClipboardHolder
        ClipboardHolder holder = new ClipboardHolder(clipboard, Util.getWeGameWorld().getWorldData());

        //Create the builder & paste it
        PasteBuilder builder = holder.createPaste(clipboard, null);
        builder.build();
        return true;
    }

    /**
     * Create a WorldEdit region from a WorldGuard region
     * @param region The region
     * @return the WE region
     */
    public static Region toWorldEditRegion(ProtectedRegion region) {
        if (region instanceof ProtectedPolygonalRegion) {
            ProtectedPolygonalRegion poly = (ProtectedPolygonalRegion) region;
            return new Polygonal2DRegion(getWeGameWorld(), poly.getPoints(), poly.getMinimumPoint().getBlockY(), poly.getMaximumPoint().getBlockY());
        } else {
            ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion) region;
            return new CuboidRegion(getWeGameWorld(), cuboid.getMinimumPoint(), cuboid.getMaximumPoint()); //Make WorldEdit Cuboid region
        }
    }

    /**
     * Get the block from a vector
     * @param vector The vector
     * @return The block
     */
    private static Block getBlock(com.sk89q.worldedit.BlockVector vector) {
        return gameWorld.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }
    //</editor-fold>

    //<editor-fold desc="SlapPlayers Functions">
    /**
     * Get the SQLPool
     * @return the SQLPool
     */
    public static SQLPool getSQLPool() {
        return SlapPlayers.getSQLPool();
    }

    /**
     * Get a SQL connection
     * @return the connection
     */
    public static Connection getSQLConnection() {
        return getSQLPool().getConnection();
    }

    /**
     * Return a SQL connection the SQLPool
     * @param connection the connection
     */
    public static void returnSQLConnection(Connection connection) {
        getSQLPool().returnConnection(connection);
    }
    //</editor-fold>

    //<editor-fold desc="Location/Block Functions">
    /**
     * Check if the X/Y/Z differ
     * @param from The from location
     * @param to The to location
     * @return Has moved
     */
    public static boolean hasMoved(Location from, Location to) {
        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if a vector and a location point to the same block
     * @param vector The vector
     * @param location The location
     * @return is same
     */
    public static boolean isSameBlock(org.bukkit.util.Vector vector, Location location) {
        return (vector.getBlockX() == location.getBlockX() && vector.getBlockY() == location.getBlockY() && vector.getBlockZ() == location.getBlockZ());
    }

    /**
     * Check if 2 locations point to the same block
     * @param loc1 The first location
     * @param loc2 The second location
     * @return is same block
     */
    public static boolean isSameBlock(Location loc1, Location loc2) {
        return (loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ());
    }

    /**
     * Check if a block is a certain type
     * @param location The location of the block
     * @param types The possible types
     * @return is type
     */
    public static boolean isType(Vector location, Material... types) {
        //Get the type of the block
        Material foundMaterial = gameWorld.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()).getType();

        //Check if one of the given types
        return SUtil.contains(foundMaterial, types);
    }

    //</editor-fold>

    //<editor-fold desc="File Functions">
    /**
     * Get the game folder (DataFolder + GameType)
     * @param gameType The type of game
     * @return The folder
     */
    public static File getGameFolder(GameType gameType) {
        return new File(plugin.getDataFolder() + gameType.getFolder(true, true));
    }

    /**
     * Get a YamlFile in a GameType folder
     * @param gameType The type of game
     * @param path The path to the file. The last entry should be the filename
     * @return the YamlFile
     */
    public static YamlFile getYamlFile(GameType gameType, String... path) {
        return new YamlFile(gameType.getFolder(false, true) + SUtil.combineToString(path, File.separator, p -> p));
    }
    //</editor-fold>

}
