package joshie.progression.criteria.conditions;

import joshie.progression.api.ProgressionAPI;
import joshie.progression.api.criteria.IProgressionField;
import joshie.progression.api.criteria.IProgressionFilter;
import joshie.progression.api.special.DisplayMode;
import joshie.progression.api.special.IEnum;
import joshie.progression.api.special.ISpecialFieldProvider;
import joshie.progression.api.special.IStackSizeable;
import joshie.progression.gui.fields.ItemFilterFieldPreview;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ConditionInInventory extends ConditionBaseItemFilter implements IEnum, ISpecialFieldProvider, IStackSizeable {
    private static enum CheckSlots {
        HELD, ARMOR, HOTBAR, INVENTORY;
    }

    public int stackSize = 1;
    public CheckSlots slotType = CheckSlots.INVENTORY;

    public ConditionInInventory() {
        super("ininventory", 0xFF660000);
    }

    @Override
    public void addSpecialFields(List<IProgressionField> fields, DisplayMode mode) {
        if (mode == DisplayMode.EDIT) fields.add(ProgressionAPI.fields.getItemPreview(this, "filters", 30, 35, 1.9F));
        else fields.add(new ItemFilterFieldPreview("filters", this, 18, 25, 2.8F));
    }

    @Override
    public int getStackSize() {
        return stackSize;
    }

    @Override
    public Enum next(String name) {
        int id = slotType.ordinal() + 1;
        if (id < CheckSlots.values().length) {
            return CheckSlots.values()[id];
        }

        return CheckSlots.values()[0];
    }

    private boolean matches(ItemStack check) {
        for (IProgressionFilter filter : filters) {
            if (filter.matches(check)) return true;
        }

        return false;
    }

    private int getAmount(EntityPlayer player, int slots) {
        boolean hasItem = false;
        for (int i = 0; i < slots; i++) {
            if (matches(player.inventory.mainInventory[i])) {
                hasItem = true;
                break;
            }
        }

        if (!hasItem) return 0;
        int amount = 0;
        for (int i = 0; i < slots; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (matches(stack)) {
                amount += stack.stackSize;
            }
        }

        return amount;
    }

    @Override
    public boolean isSatisfied(World world, EntityPlayer player, UUID uuid) {
        if (player == null) return false;
        if (slotType == CheckSlots.HELD) {
            if (matches(player.getCurrentEquippedItem())) return player.getCurrentEquippedItem().stackSize >= stackSize;
        } else if (slotType == CheckSlots.ARMOR) {
            for (ItemStack armor : player.inventory.armorInventory) {
                if (armor != null) {
                    if (matches(armor)) return armor.stackSize >= stackSize;
                }
            }

            return false;
        } else if (slotType == CheckSlots.HOTBAR) {
            return getAmount(player, 9) >= stackSize;
        } else if (slotType == CheckSlots.INVENTORY) {
            return getAmount(player, 36) >= stackSize;
        }

        return false;
    }

    @Override
    public int getWidth(DisplayMode mode) {
        return mode == DisplayMode.EDIT ? super.getWidth(mode) : 75;
    }
}
