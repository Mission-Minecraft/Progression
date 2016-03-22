package joshie.progression.criteria.rewards;

import java.util.List;
import java.util.UUID;

import joshie.progression.api.ProgressionAPI;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class RewardPoints extends RewardBase {
    public ItemStack icon = new ItemStack(Items.gold_ingot);
    public String variable = "gold";
    public String display = "[amount] Gold";
    public int amount = 1;

    public RewardPoints() {
        super(new ItemStack(Items.potionitem), "points", 0xFF002DB2);
    }

    @Override
    public void reward(UUID uuid) {
        ProgressionAPI.player.addDouble(uuid, variable, amount);
    }

    @Override
    public void addTooltip(List list) {
        String[] tooltip = display.split("/n");
        for (String string : tooltip) {
            list.add(EnumChatFormatting.WHITE + string.replace("[amount]", "" + amount));
        }
    }
}
