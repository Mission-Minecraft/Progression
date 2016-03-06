package joshie.progression.gui;

import org.lwjgl.opengl.GL11;

import joshie.progression.gui.editors.SelectItem;
import joshie.progression.gui.editors.TextEditable;
import joshie.progression.helpers.RenderItemHelper;
import net.minecraft.client.gui.ScaledResolution;

public class TreeEditorSelection extends TextEditable {
    public static TreeEditorSelection INSTANCE = new TreeEditorSelection();

    public boolean mouseClicked(int mouseX, int mouseY) {
        if (SelectItem.INSTANCE.sorted == null) {
            SelectItem.INSTANCE.updateSearch();
        }

        ScaledResolution res = GuiTreeEditor.INSTANCE.res;
        int fullWidth = res.getScaledWidth() - 10;
        int width = (int) ((double) fullWidth / 18.633333334D);
        int j = 0;
        int k = 0;
        for (int i = SelectItem.INSTANCE.position; i < SelectItem.INSTANCE.position + (width * 10); i++) {
            if (i >= 0 && i < SelectItem.INSTANCE.sorted.size()) {
                if (mouseX >= 32 + (j * 16) && mouseX <= 32 + (j * 16) + 16) {
                    if (mouseY >= 45 + (k * 16) && mouseY <= 45 + (k * 16) + 16) {
                        SelectItem.INSTANCE.selectable.setItemStack(SelectItem.INSTANCE.sorted.get(i).copy());
                        return true;
                    }
                }

                j++;

                if (j > width) {
                    j = 0;
                    k++;
                }
            }
        }

        return false;
    }

    public void draw() {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        if (SelectItem.INSTANCE.selectable != null) {
            if (SelectItem.INSTANCE.sorted == null) {
                SelectItem.INSTANCE.updateSearch();
            }
            
            int y = GuiTreeEditor.INSTANCE.y;

            int offsetX = GuiCriteriaEditor.INSTANCE.offsetX;
            ScaledResolution res = GuiTreeEditor.INSTANCE.res;
            int fullWidth = res.getScaledWidth() - 10;

            GuiTreeEditor.INSTANCE.drawGradientRectWithBorder(30, y + 20, res.getScaledWidth() - 30, y + 40, theme.blackBarGradient1, theme.blackBarGradient2, theme.blackBarBorder);
            GuiTreeEditor.INSTANCE.drawRectWithBorder(30, y + 40, res.getScaledWidth() - 30, y + 210, theme.blackBarUnderLine, theme.blackBarUnderLineBorder);

            GuiTreeEditor.INSTANCE.mc.fontRendererObj.drawString("Select Item - Click elsewhere to close", 35 - offsetX, y + 27, theme.blackBarFontColor);
            GuiTreeEditor.INSTANCE.drawRectWithBorder(res.getScaledWidth() - 180, y + 23, res.getScaledWidth() - 35, y + 38, theme.blackBarUnderLine, theme.blackBarUnderLineBorder);
            GuiTreeEditor.INSTANCE.mc.fontRendererObj.drawString(SelectItem.INSTANCE.getText(), res.getScaledWidth() - 175, y + 29, theme.blackBarFontColor);

            int width = (int) ((double) fullWidth / 18.633333334D);
            int j = 0;
            int k = 0;

            //Switch 8 > 32 (-offsetX + 32)
            //Switch 16.6333333 to 18
            //width * 4 to width *10
            for (int i = SelectItem.INSTANCE.position; i < SelectItem.INSTANCE.position + (width * 10); i++) {
                if (i >= 0 && i < SelectItem.INSTANCE.sorted.size()) {
                	RenderItemHelper.drawStack(SelectItem.INSTANCE.sorted.get(i), -offsetX + 32 + (j * 16), y + 45 + (k * 16), 1F);

                    j++;

                    if (j > width) {
                        j = 0;
                        k++;
                    }
                }
            }
        }
    }

    @Override
    public String getTextField() {
        return SelectItem.INSTANCE.getTextField();
    }

    @Override
    public void setTextField(String str) {
        SelectItem.INSTANCE.setTextField(str);
    }
}