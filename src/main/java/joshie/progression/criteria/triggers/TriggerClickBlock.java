package joshie.progression.criteria.triggers;

import joshie.progression.Progression;
import joshie.progression.api.ProgressionAPI;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TriggerClickBlock extends TriggerBaseBlock {
    public TriggerClickBlock() {
        super("clickBlock", 0xFF69008C);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEvent(PlayerInteractEvent event) {
        if (event.pos != null) {
        	IBlockState state = event.world.getBlockState(event.pos);
        	Block block = state.getBlock();
        	int meta = block.getMetaFromState(state);
    
            if (ProgressionAPI.registry.fireTrigger(event.entityPlayer, getUnlocalisedName(), block, meta) == Result.DENY) {
                event.setCanceled(true);
            }
        }
    }
        
    @Override
    public String getDescription() {
        if (cancel) {
            return Progression.translate("trigger.clickBlock.cancel");
        }

        int percentage = (counter * 100) / amount;
        return Progression.format("trigger.clickBlock.description", amount, percentage);
    }
}
