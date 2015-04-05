package joshie.crafting.gui;

import net.minecraft.item.ItemStack;

public class OverlayBase implements IRenderOverlay {
    public OverlayBase() {
        GuiCriteriaEditor.registerOverlay(this);
        GuiTriggerEditor.registerOverlay(this);
    }

    public void drawBox(int x, int y, int width, int height, int color, int border) {
        GuiCriteriaEditor.INSTANCE.drawBox(x, y, width, height, color, border);
    }

    public void drawGradient(int x, int y, int width, int height, int color, int color2, int border) {
        GuiCriteriaEditor.INSTANCE.drawGradient(x, y, width, height, color, color2, border);
    }

    public void drawText(String text, int x, int y, int color) {
        GuiCriteriaEditor.INSTANCE.drawText(text, x, y, color);
    }

    public void drawStack(ItemStack stack, int x, int y, float size) {
        GuiCriteriaEditor.INSTANCE.drawStack(stack, x, y, size);
    }

    //Resets
    public boolean reset() {
        SelectItemOverlay.INSTANCE.clear();
        SelectTextEdit.INSTANCE.clear();
        SelectEntity.INSTANCE.clear();
        NewCondition.INSTANCE.clear();
        NewTrigger.INSTANCE.clear();
        NewReward.INSTANCE.clear();
        return true;
    }

    void clear() {}

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean keyTyped(char character, int key) {
        return false;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return false;
    }

    @Override
    public void draw(int x, int y) {}
}
