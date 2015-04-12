package joshie.crafting;

import joshie.crafting.gui.GuiCriteriaEditor;
import joshie.crafting.gui.GuiTreeEditor;
import joshie.crafting.gui.GuiTriggerEditor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CraftingGUIHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 1) {
            GuiCriteriaEditor.INSTANCE.offsetX = 0;
            return GuiCriteriaEditor.INSTANCE;
        } //else if (ID == 3) return GuiCriteriaViewer.INSTANCE;
        else if (ID == 2) return GuiTriggerEditor.INSTANCE;
        return GuiTreeEditor.INSTANCE;
    }
}