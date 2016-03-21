package joshie.progression.gui.base;

import joshie.progression.helpers.RenderItemHelper;
import net.minecraft.item.ItemStack;

public class GuiOffset extends GuiBase {    
    public static GuiOffset INSTANCE = new GuiOffset();
    
    public void drawSplitText(String text, int xCoord, int yCoord, int width, int color) {
        mc.fontRendererObj.drawSplitString(text, offsetX + xCoord, y + yCoord, width, color);
    }

    public void drawText(String text, int xCoord, int yCoord, int color) {
        mc.fontRendererObj.drawString(text, offsetX + xCoord, y + yCoord, color);
    }

    public void drawBox(int xCoord, int yCoord, int width, int height, int color, int border) {
        drawRectWithBorder(offsetX + xCoord, y + yCoord, offsetX + xCoord + width, y + yCoord + height, color, border);
    }

    public void drawGradient(int xCoord, int yCoord, int width, int height, int color, int color2, int border) {
        drawGradientRectWithBorder(offsetX + xCoord, y + yCoord, offsetX + xCoord + width, y + yCoord + height, color, color2, border);
    }

    public void drawStack(ItemStack stack, int xCoord, int yCoord, float scale) {
    	RenderItemHelper.drawStack(stack, offsetX + xCoord, y + yCoord, scale);
    }

    public void drawTexture(int xCoord, int yCoord, int u, int v, int width, int height) {
        drawTexturedModalRect(offsetX + xCoord, y + yCoord, u, v, width, height);
    }

    @Override
    public void drawForeground() {
        // TODO Auto-generated method stub
        
    }
}
