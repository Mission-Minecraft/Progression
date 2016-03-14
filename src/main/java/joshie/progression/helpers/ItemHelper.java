package joshie.progression.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import joshie.progression.api.IItemFilter;
import joshie.progression.gui.newversion.overlays.IItemSelectorFilter;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemHelper {
    private static final ArrayList<ItemStack> itemsWithoutInventory = new ArrayList();
    private static final ArrayList<ItemStack> itemsWithInventory = new ArrayList();
    private static ArrayList<ItemStack> shuffledItemsCache;

    static {
        Iterator iterator = Item.itemRegistry.iterator();
        while (iterator.hasNext()) {
            Item item = (Item) iterator.next();

            if (item == null) {
                continue;
            }

            if (item.getCreativeTabs() != null && item.getCreativeTabs().length > 0) {
                for (CreativeTabs tab : item.getCreativeTabs()) {
                    try {
                        item.getSubItems(item, tab, itemsWithInventory);
                        item.getSubItems(item, tab, itemsWithoutInventory);
                    } catch (Exception e) {}
                }
            }
        }
    }

    public static ArrayList<ItemStack> getAllItems() {
        return itemsWithInventory;
    }

    public static ArrayList<ItemStack> getCreativeItems() {
        return itemsWithoutInventory;
    }

    public static void addInventory() {
        for (ItemStack stack : MCClientHelper.getPlayer().inventory.mainInventory) {
            if (stack != null) {
                if (!itemsWithInventory.contains(stack)) {
                    itemsWithInventory.add(stack);
                }
            }
        }
    }

    private static boolean isBlock(ItemStack stack) {
        Block block = null;
        int meta = 0;
        try {
            block = Block.getBlockFromItem(stack.getItem());
            meta = stack.getItemDamage();
        } catch (Exception e) {}

        return block != null;
    }

    public static ItemStack getRandomItem(List<IItemFilter> filters) {
        return getRandomItem(filters, null);
    }

    public static ItemStack getRandomItem(List<IItemFilter> filters, IItemSelectorFilter selector) {
        ArrayList<IItemFilter> shuffledFilters = new ArrayList(filters);
        if (shuffledItemsCache == null) shuffledItemsCache = new ArrayList(getCreativeItems());
        Collections.shuffle(shuffledItemsCache);
        Collections.shuffle(shuffledFilters);
        for (ItemStack stack : shuffledItemsCache) {
            if (selector != null && !selector.isAcceptable(stack)) continue;
            for (IItemFilter filter : shuffledFilters) {
                if (filter.matches(stack)) return stack;
            }
        }

        //In theory if set up correctly this should be no issue
        return null;
    }

    public static List<ItemStack> getAllMatchingItems(List<IItemFilter> filters) {
        ArrayList<ItemStack> stacks = new ArrayList();
        for (ItemStack stack : getAllItems()) {
            for (IItemFilter filter : filters) {
                if (filter.matches(stack)) {
                    stacks.add(stack.copy());
                }
            }
        }

        return stacks;
    }
}
