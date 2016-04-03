package joshie.progression.criteria.triggers;

import joshie.progression.api.ProgressionAPI;
import joshie.progression.api.criteria.ITrigger;
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

    @Override
    public ITrigger copy() {
        TriggerClickBlock trigger = new TriggerClickBlock();
        trigger.cancel = cancel;
        return copyBase(copyCounter(copyFilter(trigger)));
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
}
