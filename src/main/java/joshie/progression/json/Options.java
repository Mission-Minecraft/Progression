package joshie.progression.json;

import joshie.progression.Progression;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

public class Options {
    public static boolean editor = true;
    public static boolean tileClaimerRecipe = false;
    public static DefaultSettings settings;

    public static void init(Configuration config) {
        try {
            config.load();
            editor = config.get("Settings", "Enable Editing", true).getBoolean(true);
            tileClaimerRecipe = config.get("Settings", "Add Recipe for Tile Entity Claimer", true).getBoolean(true);
        } catch (Exception e) {
            Progression.logger.log(Level.ERROR, "Progression failed to load it's config");
            e.printStackTrace();
        } finally {
            config.save();
        }
    }
}
