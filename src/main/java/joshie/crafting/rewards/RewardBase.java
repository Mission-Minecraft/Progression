package joshie.crafting.rewards;

import java.util.HashSet;
import java.util.Set;

import joshie.crafting.api.Bus;
import joshie.crafting.api.DrawHelper;
import joshie.crafting.api.ICriteria;
import joshie.crafting.api.IRewardType;
import joshie.crafting.gui.TextList;
import joshie.crafting.helpers.ClientHelper;
import joshie.crafting.json.Theme;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.eventhandler.Event.Result;

public abstract class RewardBase implements IRewardType {
    protected Set<TextList> list = new HashSet();
    protected ICriteria criteria;
    private String name;
    private int color;
    private boolean mustClaim = false;
    private ItemStack stack;
    
    public RewardBase(ItemStack stack, String name, int color) {
        this(name, color);
        this.stack = stack;
    }

    public RewardBase(String name, int color) {
        this.name = name;
        this.color = color;
        this.stack = new ItemStack(Blocks.stone);
    }
    
    @Override
    public ItemStack getIcon() {
        return stack;
    }
    
    @Override
    public void markCriteria(ICriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public String getUnlocalisedName() {
        return name;
    }

    @Override
    public String getLocalisedName() {
        return StatCollector.translateToLocal("progression.reward." + getUnlocalisedName());
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public Bus[] getEventBusTypes() {
        return new Bus[] { getEventBus() };
    }
    
    protected Bus getEventBus() {
        return Bus.NONE;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRemoved() {}
    
    @Override
    public Result onClicked(int mouseX, int mouseY) {
        if (ClientHelper.canEdit()) {
            int index = 0;
            for (TextList t : list) {
                int color = Theme.INSTANCE.optionsFontColor;
                int yPos = 17 + (index * 8);
                if (mouseX >= 1 && mouseX <= 84) {
                    if (mouseY >= yPos && mouseY < yPos + 8) {
                        t.click();
                        return Result.ALLOW;
                    }
                }
                
                index++;
            }
        }

        return Result.DEFAULT;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        int index = 0;
        for (TextList t : list) {
            int color = Theme.INSTANCE.optionsFontColor;
            int yPos = 17 + (index * 8);
            if (ClientHelper.canEdit()) {
                if (mouseX >= 1 && mouseX <= 84) {
                    if (mouseY >= yPos && mouseY < yPos + 8) {
                        color = Theme.INSTANCE.optionsFontColorHover;
                    }
                }
            }

            t.draw(color, yPos);
            index++;
        }
    }
}
