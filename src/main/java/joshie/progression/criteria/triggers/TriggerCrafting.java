package joshie.progression.criteria.triggers;

import joshie.progression.Progression;
import joshie.progression.api.ProgressionAPI;
import joshie.progression.api.criteria.IField;
import joshie.progression.api.criteria.IFilter;
import joshie.progression.api.criteria.ITrigger;
import joshie.progression.api.special.DisplayMode;
import joshie.progression.api.special.ISpecialFieldProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

import java.util.List;
import java.util.UUID;

public class TriggerCrafting extends TriggerBaseItemFilter implements ISpecialFieldProvider {
    public int timesCrafted = 1;
    protected transient int timesItemCrafted;

    public TriggerCrafting() {
        super("crafting", 0xFF663300, "crafting");
    }

    @Override
    public ITrigger copy() {
        TriggerCrafting trigger = new TriggerCrafting();
        trigger.timesCrafted = timesCrafted;
        return copyBase(copyCounter(copyFilter(trigger)));
    }

    @SubscribeEvent
    public void onEvent(ItemCraftedEvent event) {
        ProgressionAPI.registry.fireTrigger(event.player, getUnlocalisedName(), event.crafting.copy());
    }

    @Override
    public void addSpecialFields(List<IField> fields, DisplayMode mode) {
        if (mode == DisplayMode.EDIT) fields.add(ProgressionAPI.fields.getItemPreview(this, "filters", 30, 35, 1.9F));
        else fields.add(ProgressionAPI.fields.getItemPreview(this, "filters", 65, 35, 1.9F));
    }

    @Override
    public boolean isCompleted() {
        return counter >= amount && timesItemCrafted >= timesCrafted;
    }

    @Override
    public boolean onFired(UUID uuid, Object... additional) {
        ItemStack crafted = (ItemStack) (additional[0]);
        for (IFilter filter : filters) {
            if (filter.matches(crafted)) {
                counter += crafted.stackSize;
                timesItemCrafted++;
                return true;
            }
        }

        return true;
    }

    @Override
    public String getDescription() {
        int percentageItemTotal = (counter * 100) / amount;
        int percentageCraftedTotal = (timesItemCrafted * 100) / timesCrafted;
        int percentageTotal = (percentageItemTotal + percentageCraftedTotal) / 2;
        return Progression.format("trigger.crafting.description", amount) + "\n\n" + Progression.format("completed", percentageTotal);
    }

    @Override
    public void readDataFromNBT(NBTTagCompound tag) {
        super.readDataFromNBT(tag);
        timesItemCrafted = tag.getInteger("Times");
    }

    @Override
    public void writeDataToNBT(NBTTagCompound tag) {
        super.writeDataToNBT(tag);
        tag.setInteger("Times", timesItemCrafted);
    }
}
