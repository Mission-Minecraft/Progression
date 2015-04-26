package joshie.progression.crafting;

import net.minecraft.item.ItemStack;

public abstract class Crafter {
    /** This is called when attempting to craft items
     *  If the item cannot be used in crafting, it should return false
     * @param           the stack
     * @return          whether the item can be used to craft */
    public abstract boolean canUseItemForCrafting(ActionType type, ItemStack stack);
    
    /** This is called to check whether the item is permitted to be crafted
     *  
     * @param           the stack
     * @return          whether it can be crafted or not */
    public abstract boolean canCraftItem(ActionType type, ItemStack stack);

    /** This is used to bypass the canUseItemForCrafting check
     *  Which is handy if you don't want to check for materials
     *  
     * @return          returns true if you can craft anything */
    public abstract boolean canCraftWithAnything();

    /** Used to bypass the can craft check **/
    public abstract boolean canCraftAnything();
}
