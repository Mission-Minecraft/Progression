package joshie.progression.crafting;

import joshie.progression.json.Options;
import net.minecraft.item.ItemStack;

/** This class is returned when machines look for their owners
 *  And they cannot be find. */
public class CraftingUnclaimed extends Crafter {
	public static final Crafter INSTANCE = new CraftingUnclaimed();
	
	@Override
	public boolean canUseItemWithAction(ActionType type, ItemStack stack) {
		return Options.settings.unclaimedTileCanDoAnything;
	}

	@Override
	public boolean canDoAnything() {
		return Options.settings.unclaimedTileCanDoAnything;
	}
}
