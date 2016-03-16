package joshie.progression.api;

import net.minecraft.entity.Entity;

public interface IEntityFilter extends IFieldProvider {
    /** Return true if the passed in entity matches the filter. */
    public boolean matches(Entity entity);
}
