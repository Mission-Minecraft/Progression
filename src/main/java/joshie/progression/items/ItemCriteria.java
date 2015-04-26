package joshie.progression.items;

import java.util.List;

import joshie.progression.crafting.Crafter;
import joshie.progression.crafting.CraftingRegistry;
import joshie.progression.crafting.CraftingUnclaimed;
import joshie.progression.criteria.Criteria;
import joshie.progression.handlers.APIHandler;
import joshie.progression.helpers.PlayerHelper;
import joshie.progression.network.PacketClaimed;
import joshie.progression.network.PacketHandler;
import joshie.progression.player.PlayerTracker;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCriteria extends Item {
    public static CreativeTabs tab = new CreativeTabs("progression") {
        @Override
        public String getTranslatedTabLabel() {
            return "Progression";
        }

        @Override
        public Item getTabIconItem() {
            return Items.book;
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }
    };

    public static final int CRITERIA = 0;
    public static final int CLAIM = 1;

    public ItemCriteria() {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setCreativeTab(tab);
    }

    public static Criteria getCriteriaFromStack(ItemStack stack) {
        if (!stack.hasTagCompound()) return null;
        return APIHandler.getCriteriaFromName(stack.getTagCompound().getString("Criteria"));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getItemDamage() == 1) {
            return "Progression Tile Claimer";
        }

        Criteria criteria = getCriteriaFromStack(stack);
        return criteria == null ? "BROKEN ITEM" : criteria.displayName;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote || player == null || stack == null) return true;

        if (stack.getItemDamage() == CLAIM) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile != null) {
                Crafter crafter = CraftingRegistry.getCrafterFromTile(tile);
                if (crafter == CraftingUnclaimed.INSTANCE) {
                    PlayerTracker.setTileOwner(tile, PlayerHelper.getUUIDForPlayer(player));
                    PacketHandler.sendToClient(new PacketClaimed(x, y, z), (EntityPlayerMP) player);
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            Criteria criteria = getCriteriaFromStack(stack);
            if (criteria != null) {
                boolean completed = PlayerTracker.getServerPlayer(PlayerHelper.getUUIDForPlayer(player)).getMappings().fireAllTriggers("forced-complete", criteria, criteria.rewards);
                if (!player.capabilities.isCreativeMode && completed) {
                    stack.stackSize--;
                }
            }
        }

        return stack;
    }

    @SideOnly(Side.CLIENT)
    protected String getIconString() {
        return "crafting:padlock";
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(Items.book));
        list.add(new ItemStack(item, 1, CLAIM));
        for (Criteria c : APIHandler.criteria.values()) {
            ItemStack stack = new ItemStack(item);
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setString("Criteria", c.uniqueName);
            list.add(stack);
        }
    }
}
