package joshie.progression.criteria.filters.entity;

import joshie.progression.api.ProgressionAPI;
import joshie.progression.api.criteria.IFilterType;
import joshie.progression.criteria.filters.FilterBase;
import joshie.progression.helpers.EntityHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public abstract class FilterBaseEntity extends FilterBase {
    @Override
    public EntityLivingBase getRandom(EntityPlayer player) {
        return EntityHelper.getRandomEntity(this.getProvider());
    }

    @Override
    public boolean matches(Object object) {
        if (!(object instanceof EntityLivingBase)) return false;
        return matches((EntityLivingBase) object);
    }

    @Override
    public IFilterType getType() {
        return ProgressionAPI.filters.getEntityFilter();
    }

    protected abstract boolean matches(EntityLivingBase entity);
}
