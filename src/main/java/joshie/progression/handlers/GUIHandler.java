package joshie.progression.handlers;

import joshie.progression.gui.GuiTreeEditor;
import joshie.progression.gui.GuiTriggerEditor;
import joshie.progression.gui.newversion.GuiCriteriaEditor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 1) {
            //GuiCriteriaEditor.INSTANCE.offsetX = 0;
            return GuiCriteriaEditor.INSTANCE;
        } //else if (ID == 3) return GuiCriteriaViewer.INSTANCE;
        else if (ID == 2) return GuiTriggerEditor.INSTANCE;
        return GuiTreeEditor.INSTANCE;
    }
}