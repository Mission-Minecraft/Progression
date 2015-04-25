package joshie.crafting.trigger;

import java.util.UUID;

import joshie.crafting.api.Bus;
import joshie.crafting.api.CraftingAPI;
import joshie.crafting.api.ITriggerData;
import joshie.crafting.gui.SelectItemOverlay.Type;
import joshie.crafting.gui.fields.BooleanField;
import joshie.crafting.gui.fields.ItemField;
import joshie.crafting.gui.fields.TextField;
import joshie.crafting.helpers.JSONHelper;
import joshie.crafting.trigger.data.DataCrafting;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class TriggerCrafting extends TriggerBase {
    public ItemStack stack = new ItemStack(Blocks.crafting_table);
    public boolean matchDamage = true;
    public boolean matchNBT = false;
    public int times = 1;
    public int amount = 1;

    public TriggerCrafting() {
        super("crafting", 0xFF663300, "crafting");
        list.add(new BooleanField("matchDamage", this));
        list.add(new BooleanField("matchNBT", this));
        list.add(new TextField("amount", this));
        list.add(new TextField("times", this));
        list.add(new ItemField("stack", this, 76, 44, 1.4F, 77, 100, 43, 68, Type.TRIGGER));
    }

    @Override
    public Bus getEventBus() {
        return Bus.FML;
    }

    @SubscribeEvent
    public void onEvent(ItemCraftedEvent event) {        
        CraftingAPI.registry.fireTrigger(event.player, getUnlocalisedName(), event.crafting.copy());
    }

    @Override
    public void readFromJSON(JsonObject data) {
        stack = JSONHelper.getItemStack(data, "item", new ItemStack(Blocks.crafting_table));
        matchDamage = JSONHelper.getBoolean(data, "matchDamage", matchDamage);
        matchNBT = JSONHelper.getBoolean(data, "matchNBT", matchNBT);
        times = JSONHelper.getInteger(data, "craftingTimes", times);
        amount = JSONHelper.getInteger(data, "itemAmount", amount);
    }

    @Override
    public void writeToJSON(JsonObject data) {
        JSONHelper.setItemStack(data, "item", stack);
        JSONHelper.setBoolean(data, "matchDamage", matchDamage, true);
        JSONHelper.setBoolean(data, "matchNBT", matchNBT, false);
        JSONHelper.setInteger(data, "craftingTimes", times, 1);
        JSONHelper.setInteger(data, "itemAmount", amount, 1);
    }

    @Override
    public boolean isCompleted(ITriggerData existing) {
        DataCrafting data = (DataCrafting) existing;
        return data.amountCrafted >= amount && data.timesCrafted >= times;
    }

    @Override
    public void onFired(UUID uuid, ITriggerData existing, Object... additional) {        
        DataCrafting data = (DataCrafting) existing;
        ItemStack crafted = (ItemStack)(additional[0]);        
        if (stack.getItem() == crafted.getItem()) {
            if (matchDamage && stack.getItemDamage() != crafted.getItemDamage()) return;
            if (matchNBT && stack.getTagCompound() != crafted.getTagCompound()) return;
            data.amountCrafted += crafted.stackSize;
            data.timesCrafted++;
        }
    }
}
