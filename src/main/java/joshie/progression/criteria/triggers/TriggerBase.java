package joshie.progression.criteria.triggers;

import joshie.progression.Progression;
import joshie.progression.api.ProgressionAPI;
import joshie.progression.api.criteria.IProgressionCondition;
import joshie.progression.api.criteria.IProgressionCriteria;
import joshie.progression.api.criteria.IProgressionTrigger;
import joshie.progression.api.criteria.IProgressionTriggerData;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class TriggerBase implements IProgressionTrigger {
    private List<IProgressionCondition> conditions = new ArrayList();
    private IProgressionCriteria criteria;
    private String name;
    private int color;
    private String data;
    private ItemStack stack;
    private UUID uuid;
    
    public TriggerBase(ItemStack stack, String name, int color, String data) {
        this.name = name;
        this.color = color;
        this.data = data;
        this.stack = stack;
    }

    public TriggerBase(String name, int color, String data) {
        this.name = name;
        this.color = color;
        this.data = data;
        this.stack = new ItemStack(Blocks.stone);
    }
    
    @Override
    public ItemStack getIcon() {
        return stack;
    }

    @Override
    public List<IProgressionCondition> getConditions() {
        return conditions;
    }
    
    @Override
    public IProgressionCriteria getCriteria() {
        return criteria;
    }

    @Override
    public IProgressionTriggerData newData() {
        return ProgressionAPI.data.newData(data);
    }

    @Override
    public void setCriteria(IProgressionCriteria criteria, UUID uuid) {
        this.criteria = criteria;
        this.uuid = uuid;
    }

    @Override
    public String getUnlocalisedName() {
        return name;
    }

    @Override
    public String getLocalisedName() {
        return Progression.translate("trigger." + getUnlocalisedName());
    }

    @Override
    public UUID getUniqueID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        return uuid;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void updateDraw() {}

    @Override
    public String getDescription() {
        return "MISSING DESCRIPTION";
    }
    
    @Override
    public EventBus getEventBus() {
        return MinecraftForge.EVENT_BUS;
    }
}
