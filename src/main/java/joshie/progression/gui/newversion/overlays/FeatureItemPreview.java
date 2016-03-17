package joshie.progression.gui.newversion.overlays;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import joshie.progression.api.IFilter;
import joshie.progression.gui.GuiCriteriaEditor;
import joshie.progression.gui.editors.SelectItem.Type;
import joshie.progression.gui.newversion.GuiItemFilterEditor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class FeatureItemPreview extends FeatureAbstract {
    public static FeatureItemPreview INSTANCE = new FeatureItemPreview();
    private IFilterSelectorFilter filter;
    private ArrayList<Object> sorted;
    private boolean blocksOnly;
    private int position;

    public FeatureItemPreview() {}

    public void select(IFilterSelectorFilter filter) {
        this.filter = filter;
        updateSearch();
    }

    @Override
    public boolean scroll(int mouseX, int mouseY, boolean scrolledDown) {
        if (FeatureItemSelector.INSTANCE.isVisible()) return false;
        mouseY -= 95;
        if (mouseY >= 40 && mouseY <= 110) {
            int width = (int) ((double) (screenWidth - 10) / 16.133333334D) * 4;
            if (scrolledDown) position = Math.min(sorted.size() - 200, position + width);
            else position = Math.max(0, position - width);
            return true;
        }

        return false;
    }

    private void attemptToAddBlock(ItemStack stack) {
        Block block = null;
        int meta = 0;

        try {
            block = Block.getBlockFromItem(stack.getItem());
            meta = stack.getItemDamage();
        } catch (Exception e) {}

        if (block != null) sorted.add(stack);
    }

    public void updateSearch() {
        sorted = new ArrayList();
        for (Object stack: filter.getAllItems()) {
            int matches = 0;
            for (IFilter filter: GuiItemFilterEditor.INSTANCE.field.getFilters()) {
                if (filter.matches(stack)) {
                    sorted.add(stack);
                    matches++;
                }
            }
        }
    }

    @Override
    public void drawFeature(int mouseX, int mouseY) {
        if (FeatureItemSelector.INSTANCE.isVisible()) return;
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        if (sorted == null) {
            updateSearch();
        }

        int offsetX = GuiCriteriaEditor.INSTANCE.offsetX;
        mouseY -= 95;

        int width = (int) ((double) (screenWidth - 10) / filter.getScale());
        int j = 0;
        int k = 0;
        for (int i = position; i < position + (width * 4); i++) {
            if (i >= 0 && i < sorted.size()) {
                filter.draw(offset, sorted.get(i), offsetX, j, Type.TRIGGER.yOffset, k, mouseX, mouseY);

                j++;

                if (j >= width) {
                    j = 0;
                    k++;
                }
            }
        }
    }
}