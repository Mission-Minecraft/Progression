package joshie.progression.crafting.actions;

import joshie.progression.Progression;
import joshie.progression.api.criteria.ICriteria;
import joshie.progression.crafting.ActionType;
import joshie.progression.crafting.Crafter;
import joshie.progression.crafting.CraftingRegistry;
import joshie.progression.gui.editors.GuiCriteriaEditor;
import joshie.progression.lib.GuiIDs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Collection;

public class ActionGeneral extends ActionForgeEvent {
    public ActionGeneral(String name) {
        super(name);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerAttack(AttackEntityEvent event) {
        checkAndCancelEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(EntityInteractEvent event) {
        checkAndCancelEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!checkAndCancelEvent(event)) {
            Collection<ICriteria> requirements = CraftingRegistry.getRequirements(ActionType.GENERAL, event.entityPlayer.getCurrentEquippedItem());
            if (requirements.size() > 0) {
                if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                    for (ICriteria c : requirements) {
                        GuiCriteriaEditor.INSTANCE.setCriteria(c);
                        break;
                    }
                } else event.entityPlayer.openGui(Progression.instance, GuiIDs.EDITOR, null, 0, 0, 0);
            }
        }
    }

    public boolean checkAndCancelEvent(net.minecraftforge.event.entity.player.PlayerEvent event) {
        if (event.entityPlayer.getCurrentEquippedItem() == null) return true;
        EntityPlayer player = event.entityPlayer;
        Crafter crafter = CraftingRegistry.getCrafterFromPlayer(player);
        if (!crafter.canUseItemWithAction(ActionType.GENERAL, player.getCurrentEquippedItem())) {
            event.setCanceled(true);
            return false;
        } else return true;
    }
}
