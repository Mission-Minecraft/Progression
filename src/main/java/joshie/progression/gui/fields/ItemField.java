package joshie.progression.gui.fields;

import java.lang.reflect.Field;

import joshie.progression.api.IItemGetterCallback;
import joshie.progression.api.ISetterCallback;
import joshie.progression.api.ISpecialFilters;
import joshie.progression.api.filters.IFilterSelectorFilter;
import joshie.progression.gui.editors.IItemSelectable;
import joshie.progression.gui.newversion.overlays.DrawFeatureHelper;
import joshie.progression.gui.newversion.overlays.FeatureItemSelector;
import joshie.progression.gui.newversion.overlays.FeatureItemSelector.Type;
import joshie.progression.gui.newversion.overlays.FeatureTooltip;
import joshie.progression.helpers.MCClientHelper;
import net.minecraft.item.ItemStack;

public class ItemField extends AbstractField implements IItemSelectable {
    private Field field;
    protected Object object;
    private final int x;
    private final int y;
    private final float scale;
    protected final int mouseX1;
    protected final int mouseX2;
    protected final int mouseY1;
    protected final int mouseY2;
    protected final Type type;
    protected final IFilterSelectorFilter filter;

    public ItemField(String fieldName, Object object, int x, int y, float scale, Type type, IFilterSelectorFilter filter) {
        super(fieldName);
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.mouseX1 = x;
        this.mouseX2 = (int) (x + 14 * scale);
        this.mouseY1 = y - 2;
        this.mouseY2 = (int) (y + 15 * scale);
        this.type = type;
        if (object instanceof ISpecialFilters) {
            this.filter = ((ISpecialFilters) object).getFilterForField(fieldName);
        } else this.filter = filter;

        try {
            this.field = object.getClass().getField(fieldName);
            this.object = object;
        } catch (Exception e) {}
    }

    @Override
    public void click() {}

    @Override
    public String getFieldName() {
        return field.getName();
    }

    @Override
    public boolean attemptClick(int mouseX, int mouseY) {
        boolean clicked = mouseX >= mouseX1 && mouseX <= mouseX2 && mouseY >= mouseY1 && mouseY <= mouseY2;
        if (clicked) {
            FeatureItemSelector.INSTANCE.select(filter, this, type);
            return true;
        } else return false;
    }

    public ItemStack getStack() {
        try {
            if (object instanceof IItemGetterCallback) {
                ItemStack ret = ((IItemGetterCallback) object).getItem(field.getName());
                if (ret != null) return ret;
            }

            return (ItemStack) field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getField() {
        try {
            return getStack().getDisplayName();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void draw(DrawFeatureHelper helper, int renderX, int renderY, int color, int yPos, int mouseX, int mouseY) {
        try {
            boolean clicked = mouseX >= mouseX1 && mouseX <= mouseX2 && mouseY >= mouseY1 && mouseY <= mouseY2;
            if (clicked) FeatureTooltip.INSTANCE.addTooltip(getStack().getTooltip(MCClientHelper.getPlayer(), false));
            helper.drawStack(renderX, renderY, getStack(), x, y, scale);
        } catch (Exception e) {}
    }

    @Override
    public void setObject(Object object) {
        if (object instanceof ItemStack) {
            try {
                ItemStack stack = ((ItemStack) object).copy();
                if (this.object instanceof ISetterCallback) {
                    ((ISetterCallback) this.object).setField(field.getName(), stack);
                } else field.set(this.object, stack);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}