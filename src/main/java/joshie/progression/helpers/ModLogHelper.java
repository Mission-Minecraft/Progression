package joshie.progression.helpers;

import org.apache.logging.log4j.Level;

import joshie.progression.Progression;
import net.minecraftforge.fml.common.Loader;

public class ModLogHelper {
    public static void log(String mod, String text) {
        if (Loader.isModLoaded(mod)) {
            Progression.logger.log(Level.INFO, text);
        }
    }
}
