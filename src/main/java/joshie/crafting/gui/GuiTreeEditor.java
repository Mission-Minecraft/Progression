package joshie.crafting.gui;

import java.util.ArrayList;
import java.util.List;

import joshie.crafting.CraftAPIRegistry;
import joshie.crafting.CraftingMod;
import joshie.crafting.api.CraftingAPI;
import joshie.crafting.api.ICriteria;
import joshie.crafting.api.ITab;
import joshie.crafting.api.ITreeEditor;
import joshie.crafting.helpers.ClientHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiTreeEditor extends GuiBase {
    public static final GuiTreeEditor INSTANCE = new GuiTreeEditor();
    public String currentTabName;
    public ITab currentTab;
    public ClickMode mode;

    @Override
    public void initGui() {
        super.initGui();

        buttonList = new ArrayList(); //Recreate the button list, in order to reposition it
        int pos = y - 5;
        if (ClientHelper.canEdit()) {
            buttonList.add(new ButtonNewCriteria(pos));
            pos += 28;
        }

        for (ITab tab : CraftAPIRegistry.tabs.values()) {
            buttonList.add(new ButtonTab(tab, pos));
            pos += 28;
        }

        if (currentTabName == null) {
            currentTabName = CraftingMod.options.defaultTab;
        }

        currentTab = CraftingAPI.registry.getTabFromName(currentTabName);
    }

    @Override
    public void drawForeground() {
        for (ICriteria criteria : currentTab.getCriteria()) {
            ITreeEditor editor = criteria.getTreeEditor();
            List<ICriteria> prereqs = criteria.getRequirements();
            for (ICriteria p : prereqs) {
                int y1 = p.getTreeEditor().getY();
                int y2 = editor.getY();
                int x1 = p.getTreeEditor().getX();
                int x2 = editor.getX();
                drawLine(offsetX + 95 + x1, y + 12 + y1 - 1, offsetX + 5 + x2, y + 12 + y2 - 1, 1, 0xDDB9B9AD);
                drawLine(offsetX + 95 + x1, y + 12 + y1 + 1, offsetX + 5 + x2, y + 12 + y2 + 1, 1, 0xFF636C69); //#636C69
                drawLine(offsetX + 95 + x1, y + 12 + y1, offsetX + 5 + x2, y + 12 + y2, 1, 0xFFE8EFE7);
            }
        }

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        for (ICriteria criteria : currentTab.getCriteria()) {
            ITreeEditor editor = criteria.getTreeEditor();
            editor.draw(0, y, offsetX);
        }

        TreeItemSelect.INSTANCE.draw();
    }
    
    public ITab previousTab = null;

    @Override
    protected void keyTyped(char character, int key) {
        ICriteria toRemove = null;
        for (ICriteria criteria : currentTab.getCriteria()) {
            if (criteria.getTreeEditor().keyTyped(character, key)) {
                toRemove = criteria;
                break;
            }
        }

        if (toRemove != null) {
            CraftAPIRegistry.removeCriteria(toRemove.getUniqueName());
        }

        if (ClientHelper.canEdit()) {
            SelectTextEdit.INSTANCE.keyTyped(character, key);

            if (SelectItemOverlay.INSTANCE.getEditable() != null) {
                TreeItemSelect.INSTANCE.keyTyped(character, key);
            }
        }

        super.keyTyped(character, key);
    }

    @Override
    public void mouseMovedOrUp(int x, int y, int button) {
        for (ICriteria criteria : currentTab.getCriteria()) {
            criteria.getTreeEditor().release(mouseX, mouseY);
        }
    }

    private long lastClick;
    private int lastType;
    ICriteria lastClicked = null;

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        long thisClick = System.currentTimeMillis();
        long difference = thisClick - lastClick;
        boolean isDoubleClick = par3 == 0 && lastType == 0 && difference <= 500;
        lastClick = System.currentTimeMillis();
        lastType = par3;

        lastClicked = null;
        if (SelectItemOverlay.INSTANCE.getEditable() != null) {
            TreeItemSelect.INSTANCE.mouseClicked(mouseX, mouseY);
        }

        super.mouseClicked(par1, par2, par3);
        boolean clicked = false;
        for (ICriteria criteria : currentTab.getCriteria()) {
            if (criteria.getTreeEditor().click(mouseX, mouseY, isDoubleClick)) {
                if (!clicked) {
                    lastClicked = criteria;
                }
            }
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        for (ICriteria criteria : currentTab.getCriteria()) {
            criteria.getTreeEditor().follow(mouseX, mouseY);
            int wheel = Mouse.getDWheel();
            if (wheel != 0) {
                criteria.getTreeEditor().scroll(wheel < 0);
            }
        }
    }

    public static enum ClickMode {
        DEFAULT;
    }
}
