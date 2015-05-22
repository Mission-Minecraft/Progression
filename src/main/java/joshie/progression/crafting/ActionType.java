package joshie.progression.crafting;

import net.minecraft.util.StatCollector;

public enum ActionType {
   CRAFTING, FURNACE, BREAKBLOCK, HARVESTDROP, ENTITYDROP;

    public String getDisplayName() {
        return StatCollector.translateToLocal("progression.action." + name().toLowerCase());
    }
    
    public static ActionType getCraftingActionFromName(String name) {
        for (ActionType type : ActionType.values()) {
            if (name.equalsIgnoreCase(type.name())) return type;
        }

        return ActionType.CRAFTING;
    }
}
