package nl.stoux.SlapGames.Games.Spleef.Arenas;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import nl.stoux.SlapGames.Games.Base.Arena.BaseArena;
import nl.stoux.SlapGames.Storage.YamlFile;
import nl.stoux.SlapGames.Util.Util;
import org.bukkit.Material;

/**
 * Created by Stoux on 22/01/2015.
 */
public class SpleefArena extends BaseArena<SpleefArenaSettings> {

    /** Available floor types */
    @Getter
    public static final Material[] DEFAULT_FLOOR_TYPES = new Material[]{
            Material.ICE,
            Material.SNOW_BLOCK,
            Material.QUARTZ_BLOCK,
            Material.GLASS,
            Material.FENCE,
            Material.LEAVES,
            Material.ENCHANTMENT_TABLE,
            Material.SOUL_SAND
    };

    public SpleefArena(YamlFile file) {
        super(file);
    }

    @Override
    protected SpleefArenaSettings createSettings(YamlFile file) {
        return new SpleefArenaSettings(file);
    }

    @Override
    public boolean saveArena() {
        return Util.saveSchematic(getRegion(), "Spleef", filename);
    }

    @Override
    public boolean restoreArena() {
        return Util.pasteClipboard(Util.loadSchematic("Spleef", filename));
    }

    /**
     * Set the floor to a material
     * @param floor The floor
     * @param data Optional data
     */
    public void setFloor(Material floor, Byte data) {
        //Create the block
        BaseBlock block = new BaseBlock(floor.getId());
        if (data != null) {
            block.setData(data);
        }

        ProtectedRegion region = getRegion();
        try {
            //Create the session & set the blocks
            EditSession session = new EditSessionFactory().getEditSession(Util.getWeGameWorld(), Integer.MAX_VALUE);
            session.setBlocks(Util.toWorldEditRegion(region), block);
        } catch (MaxChangedBlocksException e) {
            //Shouldn't be thrown
        }
    }

    /**
     * Get the ground region
     * @return The region
     */
    public ProtectedRegion getRegion() {
        return settings.getRegion().getValue();
    }

    /** Set a random floor */
    public void setRandomFloor() {
        setFloor(DEFAULT_FLOOR_TYPES[Util.getRandom().nextInt(DEFAULT_FLOOR_TYPES.length)], null);
    }

}
