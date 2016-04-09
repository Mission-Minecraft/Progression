package joshie.progression.criteria;

import com.google.gson.JsonObject;
import joshie.progression.Progression;
import joshie.progression.api.criteria.ICondition;
import joshie.progression.api.criteria.IConditionProvider;
import joshie.progression.api.criteria.ITriggerProvider;
import joshie.progression.api.special.*;
import joshie.progression.helpers.JSONHelper;
import joshie.progression.player.PlayerTracker;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class Condition implements IConditionProvider {
    private final ICondition condition;
    private final String unlocalised;

    private ITriggerProvider provider;
    private UUID uuid;

    private ItemStack stack;

    public boolean isVisible = true;
    public boolean inverted = false;

    //Dummy constructor for storing the default values
    public Condition(ICondition condition, String unlocalised) {
        this.condition = condition;
        this.unlocalised = unlocalised;
        this.condition.setProvider(this);
    }

    public Condition(ITriggerProvider trigger, UUID uuid, ICondition condition, ItemStack stack, String unlocalised) {
        this.provider = trigger;
        this.uuid = uuid;
        this.condition = condition;
        this.unlocalised = unlocalised;
        this.stack = stack;
        this.condition.setProvider(this);
    }

    @Override
    public ITriggerProvider getTrigger() {
        return provider;
    }

    @Override
    public ICondition getProvided() {
        return condition;
    }

    @Override
    public String getUnlocalisedName() {
        return unlocalised;
    }

    @Override
    public int getColor() {
        return getTrigger().getColor();
    }

    @Override
    public ItemStack getIcon() {
        return condition instanceof ICustomIcon ? ((ICustomIcon)condition).getIcon() : stack;
    }

    @Override
    public String getLocalisedName() {
        return condition instanceof ICustomDisplayName ? ((ICustomDisplayName)condition).getDisplayName() : Progression.translate(getUnlocalisedName());
    }

    private transient boolean isTrue = false;
    private transient int checkTick = 0;
    private boolean isSatisfied() {
        if (checkTick == 0 || checkTick >= 200) {
            isTrue = condition.isSatisfied(PlayerTracker.getClientPlayer().getTeam());
            checkTick = 1;
        }

        checkTick++;
        return isTrue;
    }

    private String getConditionDescription() {
        if (condition instanceof ICustomDescription) return ((ICustomDescription)condition).getDescription();
        if (inverted) return Progression.translate(getUnlocalisedName() + ".description.inverted");
        else return Progression.translate(getUnlocalisedName() + ".description");
    }

    @Override
    public String getDescription() {
        if (inverted) return getConditionDescription() + "\n\n" + Progression.format("truth", !isSatisfied());
        return getConditionDescription() + "\n\n" + Progression.format("truth", isSatisfied());
    }

    @Override
    public int getWidth(DisplayMode mode) {
        return condition instanceof ICustomWidth ? ((ICustomWidth)condition).getWidth(mode) : 100;
    }

    @Override
    public UUID getUniqueID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        return uuid;
    }

    @Override
    public IConditionProvider setIcon(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public boolean isInverted() {
        return inverted;
    }

    @Override
    public void readFromJSON(JsonObject data) {
        isVisible = JSONHelper.getBoolean(data, "isVisible", true);
        inverted = JSONHelper.getBoolean(data, "inverted", false);
    }

    @Override
    public void writeToJSON(JsonObject data) {
        JSONHelper.setBoolean(data, "isVisible", isVisible, true);
        JSONHelper.setBoolean(data, "inverted", inverted, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof IConditionProvider)) return false;

        IConditionProvider that = (IConditionProvider) o;
        return getUniqueID() != null ? getUniqueID().equals(that.getUniqueID()) : that.getUniqueID() == null;

    }

    @Override
    public int hashCode() {
        return getUniqueID() != null ? getUniqueID().hashCode() : 0;
    }
}
