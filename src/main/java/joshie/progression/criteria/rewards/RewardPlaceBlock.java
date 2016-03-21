package joshie.progression.criteria.rewards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import joshie.progression.Progression;
import joshie.progression.api.IFilter;
import joshie.progression.api.ISpecialFilters;
import joshie.progression.api.fields.IField;
import joshie.progression.api.fields.ISpecialFieldProvider;
import joshie.progression.api.filters.IFilterSelectorFilter;
import joshie.progression.gui.fields.ItemFilterFieldPreview;
import joshie.progression.gui.selector.filters.BlockFilter;
import joshie.progression.gui.selector.filters.LocationFilter;
import joshie.progression.helpers.ItemHelper;
import joshie.progression.helpers.PlayerHelper;
import joshie.progression.lib.WorldLocation;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class RewardPlaceBlock extends RewardBaseItemFilter implements ISpecialFieldProvider, ISpecialFilters {
    public List<IFilter> locations = new ArrayList();

    public RewardPlaceBlock() {
        super("placeBlock", 0xFF00680A);
    }

    @Override
    public boolean shouldReflectionSkipField(String name) {
        return name.equals("filters");
    }

    @Override
    public void addSpecialFields(List<IField> fields, DisplayMode mode) {
        if (mode == DisplayMode.EDIT) fields.add(new ItemFilterFieldPreview("filters", this, 25, 30, 2.8F));
    }

    @Override
    public IFilterSelectorFilter getFilterForField(String fieldName) {
        if (fieldName.equals("locations")) return LocationFilter.INSTANCE;
        if (fieldName.equals("filters")) return BlockFilter.INSTANCE;

        return null;
    }

    @Override
    public List<IFilter> getAllFilters() {
        ArrayList<IFilter> all = new ArrayList();
        all.addAll(locations);
        all.addAll(filters);
        return all;
    }

    @Override
    public void reward(UUID uuid) {
        List<EntityPlayerMP> players = PlayerHelper.getPlayersFromUUID(uuid);
        for (EntityPlayerMP player : players) {
            if (player != null) {
                ArrayList<IFilter> locality = new ArrayList(locations);
                if (locality.size() > 0) {
                    Collections.shuffle(locality);
                    WorldLocation location = (WorldLocation) locality.get(0).getMatches(player).get(0);
                    World world = DimensionManager.getWorld(location.dimension);

                    ItemStack stack = ItemHelper.getRandomBlock(filters);
                    Block block = ItemHelper.getBlock(stack);
                    int damage = stack.getItemDamage();
                    world.setBlockState(location.pos, block.getStateFromMeta(damage));
                }
            }
        }
    }

    @Override
    public void addTooltip(List list) {
        list.add(EnumChatFormatting.WHITE + Progression.translate("block.place"));
        list.add(getIcon().getDisplayName());
    }
}
