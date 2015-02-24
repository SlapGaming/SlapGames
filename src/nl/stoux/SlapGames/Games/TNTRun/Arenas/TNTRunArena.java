package nl.stoux.SlapGames.Games.TNTRun.Arenas;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.stoux.SlapGames.Games.Base.Arena.BaseArena;
import nl.stoux.SlapGames.Settings.Setting;
import nl.stoux.SlapGames.Storage.YamlFile;
import nl.stoux.SlapGames.Util.Util;

/**
 * Created by Stoux on 26/01/2015.
 */
public class TNTRunArena extends BaseArena<TNTRunArenaSettings> {

    public TNTRunArena(YamlFile file) {
        super(file);
    }

    @Override
    protected TNTRunArenaSettings createSettings(YamlFile file) {
        return new TNTRunArenaSettings(file);
    }

    @Override
    public boolean saveArena() {
        boolean greatSuccess = true;
        int floorNr = 0;
        //Loop through floors
        for (Setting<ProtectedRegion> floor : settings.getFloors()) {
            if (floor.getValue() == null) {
                continue;
            }

            //Save the floor
            if (!Util.saveSchematic(floor.getValue(), "TNTRun", filename + ++floorNr)) {
                greatSuccess = false;
            }
        }
        return greatSuccess;
    }

    @Override
    public boolean restoreArena() {
        boolean success = true;
        int floorNr = 0;
        //Loop through floors
        for (Setting<ProtectedRegion> floor : settings.getFloors()) {
            if (floor.getValue() == null) {
                continue;
            }

            //Restore the floor
            if (Util.pasteClipboard(Util.loadSchematic("TNTRun", filename + ++floorNr))) {
                success = false;
            }
        }
        return success;
    }
}
