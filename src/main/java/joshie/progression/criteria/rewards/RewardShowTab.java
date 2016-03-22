package joshie.progression.criteria.rewards;

import java.util.UUID;

import joshie.progression.api.ProgressionAPI;
import joshie.progression.api.criteria.IProgressionTab;
import joshie.progression.api.event.TabVisibleEvent;
import joshie.progression.api.special.IGetterCallback;
import joshie.progression.api.special.IHasEventBus;
import joshie.progression.api.special.IInit;
import joshie.progression.api.special.IStoreNBTData;
import joshie.progression.handlers.APIHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RewardShowTab extends RewardBase implements IStoreNBTData, IHasEventBus, IInit, IGetterCallback {
    public boolean hideByDefault = true;
    public String displayName = "";
    private String tabID = "";
    private IProgressionTab tab;

    public RewardShowTab() {
        super("tab.show", 0xFFCCCCCC);
    }

    @Override
    public EventBus getEventBus() {
        return MinecraftForge.EVENT_BUS;
    }

    @Override
    public void init() {
        try {
            for (IProgressionTab t : APIHandler.getTabs().values()) {
                String display = t.getDisplayName();
                if (t.getDisplayName().equals(displayName)) {
                    tab = t;
                    tabID = t.getUniqueName();
                    break;
                }
            }
        } catch (Exception e) {}
    }

    public IProgressionTab getAssignedTab() {
        return APIHandler.getTabFromName(tabID);
    }

    @SubscribeEvent
    public void onFeatureRender(TabVisibleEvent event) {
        NBTTagCompound tag = ProgressionAPI.player.getCustomData(event.entityPlayer, "progression.tab.hidden");
        if (tag != null) {
            if (tag.hasKey(event.unique)) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public String getNBTKey() {
        return "progression.tab.hidden";
    }

    @Override
    public NBTTagCompound getDefaultTags(NBTTagCompound tag) {
        if (hideByDefault) {
            if (tab != null) {
                tag.setBoolean(tab.getUniqueName(), true);
            }
        }

        return tag;
    }

    @Override
    public String getField(String fieldName) {
        return tab != null ? EnumChatFormatting.GREEN + displayName : EnumChatFormatting.RED + displayName;
    }

    @Override
    public void reward(UUID uuid) {
        if (tab == null) return; //Do not give the reward

        NBTTagCompound tag = ProgressionAPI.player.getCustomData(uuid, "progression.tab.hidden");
        if (tag == null) tag = new NBTTagCompound();
        if (hideByDefault) tag.removeTag(tab.getUniqueName());
        else tag.setBoolean(tab.getUniqueName(), true);

        ProgressionAPI.player.setCustomData(uuid, "progression.tab.hidden", tag);
    }

}
