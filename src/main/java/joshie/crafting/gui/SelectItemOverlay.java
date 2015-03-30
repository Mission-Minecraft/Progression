package joshie.crafting.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import joshie.crafting.helpers.ItemHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

public class SelectItemOverlay extends TextEditable implements IRenderOverlay {
    public static SelectItemOverlay INSTANCE = new SelectItemOverlay();
    private static IItemSelectable selectable = null;
    private static ArrayList<ItemStack> sorted;
    private static String search = "";
    private static int position;
    private static Type type;

    public SelectItemOverlay() {
        ItemHelper.addInventory();
    }

    public static enum Type {
        REWARD(0), TRIGGER(95);

        public int yOffset;

        private Type(int offset) {
            this.yOffset = offset;
        }
    }

    public void select(IItemSelectable selectable, Type type) {
        if (reset()) {
            //Setup the info
            SelectItemOverlay.type = type;
            SelectItemOverlay.selectable = selectable;
            super.position = search.length();
        }
    }

    @Override
    void clear() {
        SelectItemOverlay.selectable = null;
        SelectItemOverlay.search = "";
        SelectItemOverlay.position = 0;
    }

    public void updateSearch() {
        if (search == null || search.equals("")) {
            sorted = new ArrayList(ItemHelper.getItems());
        } else {
            position = 0;
            sorted = new ArrayList();
            for (ItemStack stack : ItemHelper.getItems()) {
                if (stack != null && stack.getItem() != null) {
                    if (stack.getDisplayName().toLowerCase().contains(search.toLowerCase())) {
                        sorted.add(stack);
                    }
                }
            }
        }
    }

    @Override
    public void setTextField(String text) {
        this.search = text;
        this.updateSearch();
    }

    @Override
    public String getTextField() {
        return this.search;
    }

    @Override
    public boolean isVisible() {
        return selectable != null;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (sorted == null) {
            updateSearch();
        }

        mouseY -= type.yOffset;

        ScaledResolution res = new ScaledResolution(GuiTreeEditorEdit.INSTANCE.mc, GuiTreeEditorEdit.INSTANCE.mc.displayWidth, GuiTreeEditorEdit.INSTANCE.mc.displayHeight);
        int fullWidth = res.getScaledWidth() - 10;
        int width = (int) ((double) fullWidth / 16.633333334D);
        int j = 0;
        int k = 0;
        for (int i = position; i < position + (width * 4); i++) {
            if (i >= 0 && i < sorted.size()) {
                if (mouseX >= 8 + (j * 16) && mouseX <= 8 + (j * 16) + 16) {
                    if (mouseY >= 45 + (k * 16) && mouseY <= 45 + (k * 16) + 16) {
                        selectable.setItemStack(sorted.get(i).copy());
                        clear();
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

    @Override
    public void draw(int x, int y) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        
        if (selectable != null) {
            if (sorted == null) {
                updateSearch();
            }
            
            drawBox(0, 40 + type.yOffset, GuiTreeEditorEdit.INSTANCE.mc.displayWidth, 73, 0xFFFFFFFF, 0xFFFFFFFF);

            ScaledResolution res = new ScaledResolution(GuiTreeEditorEdit.INSTANCE.mc, GuiTreeEditorEdit.INSTANCE.mc.displayWidth, GuiTreeEditorEdit.INSTANCE.mc.displayHeight);
            int fullWidth = res.getScaledWidth() - 10;
            drawText(getText(), 190, 29 + type.yOffset, 0xFF000000);

            int width = (int) ((double) fullWidth / 16.633333334D);
            int j = 0;
            int k = 0;
            for (int i = position; i < position + (width * 4); i++) {
                if (i >= 0 && i < sorted.size()) {
                    drawStack(sorted.get(i), -GuiCriteriaEditor.INSTANCE.offsetX + 8 + (j * 16), type.yOffset + 45 + (k * 16), 1F);

                    j++;

                    if (j > width) {
                        j = 0;
                        k++;
                    }
                }
            }
        }
    }
}