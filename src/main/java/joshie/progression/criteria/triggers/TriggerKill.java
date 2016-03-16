package joshie.progression.criteria.triggers;

import java.util.ArrayList;
import java.util.List;

import joshie.progression.api.IFilter;
import joshie.progression.api.ProgressionAPI;
import joshie.progression.helpers.EntityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TriggerKill extends TriggerBaseCounterVaries {
    public List<IFilter> filters = new ArrayList();
    private transient EntityLivingBase entity;

    public TriggerKill() {
        super(new ItemStack(Items.iron_sword), "kill", 0xFF000000);
    }

    @SubscribeEvent
    public void onEvent(LivingDeathEvent event) {
        Entity source = event.source.getSourceOfDamage();
        if (source instanceof EntityPlayer && source instanceof EntityLivingBase) {
            ProgressionAPI.registry.fireTrigger((EntityPlayer) source, getUnlocalisedName(), source);
        }
    }

    @Override
    protected boolean canIncrease(Object... data) {
        EntityLivingBase entity = (EntityLivingBase) data[0];
        for (IFilter filter : filters) {
            if (filter.matches(entity)) return true;
        }

        return false;
    }

    @Override
    public void updateDraw() {
        entity = EntityHelper.getRandomEntityForFilters(filters);
    }

    @Override
    public String getDescription() {
        String name = "INVALID";
        try {
            name = entity.getName();
        } catch (Exception e) {}

        return amount + " x " + name;
    }

    /*
    @Override
    public void drawDisplay(int mouseX, int mouseY) {
        try {
            int max = 36;
            double numberToUse = (double) (entity.width >= entity.height ? entity.width : entity.height);
            int scale = (int) (numberToUse != 0 ? max / numberToUse : max);
            glClear(GL_DEPTH_BUFFER_BIT);
            GuiInventory.drawEntityOnScreen(ProgressionAPI.draw.getXPosition() + 37 + GuiCriteriaEditor.INSTANCE.offsetX, GuiCriteriaEditor.INSTANCE.y + 117, scale, 25F, -5F, entity);
        } catch (Exception e) {}
    } */
}
