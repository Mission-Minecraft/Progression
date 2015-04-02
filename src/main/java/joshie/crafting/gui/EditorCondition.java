package joshie.crafting.gui;

import java.util.Iterator;
import java.util.List;

import joshie.crafting.api.ICondition;
import joshie.crafting.api.IConditionEditor;
import joshie.crafting.api.ITrigger;
import joshie.crafting.helpers.ClientHelper;
import joshie.crafting.helpers.StackHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.Event.Result;

public class EditorCondition implements IConditionEditor {
    private static final ResourceLocation textures = new ResourceLocation("crafting", "textures/gui/textures.png");
    private final ITrigger trigger;
    private int xCoord;
    private int yCoord;

    public EditorCondition(ITrigger criteria) {
        this.trigger = criteria;
    }
    
    @Override
    public void drawSplitText(String text, int x, int y, int color, int width) {
        GuiTreeEditor.INSTANCE.mc.fontRenderer.drawSplitString(text, xCoord + x, yCoord + y, width, color);
    }

    @Override
    public void drawText(String text, int x, int y, int color) {
        GuiTreeEditor.INSTANCE.mc.fontRenderer.drawString(text, xCoord + x, yCoord + y, color);
    }

    @Override
    public void drawBox(int x, int y, int width, int height, int color, int border) {
        GuiTreeEditor.INSTANCE.drawRectWithBorder(xCoord + x, yCoord + y, xCoord + x + width, yCoord + y + height, color, border);
    }

    @Override
    public void drawGradient(int x, int y, int width, int height, int color, int color2, int border) {
        GuiTreeEditor.INSTANCE.drawGradientRectWithBorder(xCoord + x, yCoord + y, xCoord + x + width, yCoord + y + height, color, color2, border);
    }

    @Override
    public void drawStack(ItemStack stack, int x, int y, float scale) {
        StackHelper.drawStack(stack, xCoord + x, yCoord + y, scale);
    }

    @Override
    public void drawTexture(int x, int y, int u, int v, int width, int height) {
        GuiTreeEditor.INSTANCE.drawTexturedModalRect(xCoord + x, yCoord + y, u, v, width, height);
    }

    private int offsetX = 0;

    @Override
    public void draw(int x, int y, int offsetX) {
        this.offsetX = offsetX;
        this.xCoord = x + offsetX;
        this.yCoord = y;

        ScaledResolution res = new ScaledResolution(GuiTreeEditor.INSTANCE.mc, GuiTreeEditor.INSTANCE.mc.displayWidth, GuiTreeEditor.INSTANCE.mc.displayHeight);
        int fullWidth = (res.getScaledWidth()) - offsetX + 5;
        //Title and Repeatability Box
        drawText("Editing Trigger conditions for the Criteria: " + trigger.getCriteria().getDisplayName() + " - " + trigger.getLocalisedName(), 9 - offsetX, 9, 0xFFFFFFFF);
        drawBox(-1, 210, fullWidth, 1, 0xFFFFFFFF, 0xFFFFFFFF);
        drawText("Use arrow keys to scroll sideways, or use the scroll wheel. (Down to go right)", 9 - offsetX, 215, 0xFFFFFFFF);
        drawText("Hold shift with arrow keys to scroll faster.", 9 - offsetX, 225, 0xFFFFFFFF);

        //Triggers
        drawGradient(-1, 25, fullWidth, 15, 0xFFFF8000, 0xFFB25900, 0xFF8C4600);
        drawBox(-1, 40, fullWidth, 1, 0xFF8C4600, 0x00000000);
        drawText("Conditions", 9 - offsetX, 29, 0xFFFFFFFF);
        int xCoord = 0;
        List<ICondition> conditions = trigger.getConditions();
        int mouseX = GuiTriggerEditor.INSTANCE.mouseX - offsetX;
        int mouseY = GuiTriggerEditor.INSTANCE.mouseY;
        for (int i = 0; i < conditions.size(); i++) {
            ICondition condition = conditions.get(i);
            int xPos = 100 * xCoord;
            condition.draw(mouseX, mouseY, xPos);
            xCoord++;
        }

        int crossX = 55;
        if (!NewCondition.INSTANCE.isVisible()) {
            if (mouseX >= 15 + 100 * xCoord && mouseX <= 15 + 100 * xCoord + 55) {
                if (mouseY >= 49 && mouseY <= 49 + 55) {
                    crossX = 165;
                }
            }
        }

        GL11.glColor4f(1F, 1F, 1F, 1F);
        ClientHelper.getMinecraft().getTextureManager().bindTexture(textures);
        drawTexture(15 + 100 * xCoord, 49, crossX, 180, 55, 55);
    }

    private void remove(List list, Object object) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o.equals(object)) {
                it.remove();
                break;
            }
        }
    }

    @Override
    public boolean click(int mouseX, int mouseY, boolean isDoubleClick) {        
        boolean hasClicked = false;
        //Name and repeat
        ScaledResolution res = new ScaledResolution(GuiTreeEditor.INSTANCE.mc, GuiTreeEditor.INSTANCE.mc.displayWidth, GuiTreeEditor.INSTANCE.mc.displayHeight);
        int fullWidth = (res.getScaledWidth()) - offsetX + 5;
        
        //Triggers
        xCoord = 0;
        List<ICondition> conditions = trigger.getConditions();
        for (int i = 0; i < conditions.size(); i++) {
            Result result = conditions.get(i).onClicked();
            if (result != Result.DEFAULT) {
                hasClicked = true;
            }

            if (result == Result.DENY) {
                remove(conditions, conditions.get(i));
                break;
            }

            xCoord++;
        }

        mouseX = GuiTriggerEditor.INSTANCE.mouseX - offsetX;
        mouseY = GuiTriggerEditor.INSTANCE.mouseY;        
        int color = 0xFF0080FF;
        if (mouseX >= 15 + 100 * xCoord && mouseX <= 15 + 100 * xCoord + 55) {
            if (mouseY >= 49 && mouseY <= 49 + 55) {
                NewCondition.INSTANCE.select(trigger);
                hasClicked = true;
            }
        }

        return hasClicked;
    }
}
