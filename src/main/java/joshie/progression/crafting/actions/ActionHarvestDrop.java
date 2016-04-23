package joshie.progression.crafting.actions;

import joshie.progression.crafting.ActionType;
import joshie.progression.handlers.ProgressionEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;

public class ActionHarvestDrop extends ActionForgeEvent {
    public static final ActionHarvestDrop INSTANCE = new ActionHarvestDrop();

    @SubscribeEvent
    public void onHarvestDrop(HarvestDropsEvent event) {
        EntityPlayer player = event.harvester;
        if (player != null) {
            Iterator<ItemStack> it = event.drops.iterator();
            while (it.hasNext()) {
                ItemStack stack = it.next();
                if (ProgressionEvents.isEventCancelled(player, ActionType.HARVESTDROPWITH, player.getCurrentEquippedItem(), ActionType.HARVESTDROP, stack)) {
                    it.remove();
                }
            }
        }
    }
}
