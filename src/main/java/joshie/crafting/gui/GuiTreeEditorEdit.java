package joshie.crafting.gui;

import joshie.crafting.CraftAPIRegistry;
import joshie.crafting.api.ICriteria;

import org.lwjgl.input.Mouse;

public class GuiTreeEditorEdit extends GuiTreeEditorDisplay {
    public static final GuiTreeEditorEdit INSTANCE = new GuiTreeEditorEdit();

    @Override
    protected void keyTyped(char character, int key) {
        super.keyTyped(character, key);
        for (ICriteria criteria : CraftAPIRegistry.criteria.values()) {
            criteria.keyTyped(character, key);
        }
    }

    @Override
    public void mouseMovedOrUp(int x, int y, int button) {
        for (ICriteria criteria : CraftAPIRegistry.criteria.values()) {
            criteria.release(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        for (ICriteria criteria : CraftAPIRegistry.criteria.values()) {
            criteria.click(mouseX, mouseY);
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        for (ICriteria criteria : CraftAPIRegistry.criteria.values()) {
            criteria.follow(mouseX, mouseY);
            int wheel = Mouse.getDWheel();
            if (wheel != 0) {
                criteria.scroll(wheel < 0);
            }
        }
    }
}
