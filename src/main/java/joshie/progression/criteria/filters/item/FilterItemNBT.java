package joshie.progression.criteria.filters.item;

import joshie.progression.api.ISetterCallback;
import joshie.progression.helpers.StackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class FilterItemNBT extends FilterBaseItem implements ISetterCallback {
    public NBTTagCompound tagValue = new NBTTagCompound();
    public String tagText = "";

    public FilterItemNBT() {
        super("nbtString", 0xFF00B2B2);
    }

    @Override
    public boolean matches(ItemStack check) { //TODO: Add Partial matching
        return check.hasTagCompound() && check.getTagCompound().equals(tagValue);
    }

    @Override
    public boolean setField(String fieldName, Object object) {
        String fieldValue = (String) object;
        tagValue = StackHelper.getTag(new String[] { fieldValue }, 0);
        tagText = fieldValue; //Temporary field
        return false;
    }
}
