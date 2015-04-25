package joshie.crafting.rewards;

import java.util.List;
import java.util.UUID;

import joshie.crafting.gui.TextFieldHelper;
import joshie.crafting.gui.TextFieldHelper.IntegerFieldHelper;
import joshie.crafting.gui.TextList.TextSelector;
import joshie.crafting.helpers.JSONHelper;
import joshie.crafting.player.PlayerTracker;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.google.gson.JsonObject;

public class RewardPoints extends RewardBase {
    public String name = "research";
    public int amount = 1;

    public RewardPoints() {
        super(new ItemStack(Items.potionitem), "points", 0xFF002DB2);
        list.add(new TextSelector("name", new TextFieldHelper("name", this)));
        list.add(new TextSelector("amount", new IntegerFieldHelper("amount", this)));
    }

    @Override
    public void readFromJSON(JsonObject data) {
        name = JSONHelper.getString(data, "name", name);
        amount = JSONHelper.getInteger(data, "amount", amount);
    }

    @Override
    public void writeToJSON(JsonObject elements) {
        JSONHelper.setString(elements, "name", name, "research");
        JSONHelper.setInteger(elements, "amount", amount, 1);
    }

    @Override
    public void reward(UUID uuid) {
        PlayerTracker.getServerPlayer(uuid).addPoints(name, amount);
    }

    @Override
    public void addTooltip(List list) {
        list.add("" + EnumChatFormatting.WHITE + amount + " " + name + " Points");
    }
}
