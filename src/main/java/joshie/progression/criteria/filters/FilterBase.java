package joshie.progression.criteria.filters;

import joshie.progression.Progression;
import joshie.progression.api.criteria.IProgressionFilter;

import java.util.UUID;

public abstract class FilterBase implements IProgressionFilter {
    private int color;
    private String name;
    private UUID uuid;

    public FilterBase(String name, int color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public String getUnlocalisedName() {
        return name;
    }

    @Override
    public String getLocalisedName() {
        return Progression.translate("filter." + getType().name().toLowerCase() + "." + getUnlocalisedName());
    }

    @Override
    public UUID getUniqueID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        return uuid;
    }

    @Override
    public void updateDraw() {}

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public boolean matches(Object object) {
        return false;
    }
}
