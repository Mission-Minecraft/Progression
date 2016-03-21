package joshie.progression.gui.selector.filters;

import joshie.progression.api.filters.IFilterHelper;
import joshie.progression.api.filters.IFilterSelectorFilter;

public class FilterSelectorHelper implements IFilterHelper {
    @Override
    public IFilterSelectorFilter getBlockFilter() {
        return BlockFilter.INSTANCE;
    }

    @Override
    public IFilterSelectorFilter getEntityFilter() {
        return EntityFilter.INSTANCE;
    }

    @Override
    public IFilterSelectorFilter getPotionFilter() {
        return PotionFilter.INSTANCE;
    }

    @Override
    public IFilterSelectorFilter getLocationFilter() {
        return LocationFilter.INSTANCE;
    }

    @Override
    public IFilterSelectorFilter getItemStackFilter() {
        return ItemFilter.INSTANCE;
    }

    @Override
    public IFilterSelectorFilter getCraftingFilter() {
        return ActionFilter.INSTANCE;
    }
}
