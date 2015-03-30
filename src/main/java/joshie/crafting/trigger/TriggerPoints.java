package joshie.crafting.trigger;

import java.util.UUID;

import joshie.crafting.api.Bus;
import joshie.crafting.api.CraftingAPI;
import joshie.crafting.api.ITrigger;
import joshie.crafting.api.ITriggerData;
import joshie.crafting.gui.TextFieldHelper;
import joshie.crafting.gui.TextFieldHelper.IntegerFieldHelper;
import joshie.crafting.trigger.data.DataBoolean;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.Event.Result;

public class TriggerPoints extends TriggerBaseBoolean {
    private TextFieldHelper nameEdit;
    private IntegerFieldHelper amountEdit;
    public int amount = 1;
    public boolean consume = true;
    public String name = "Research";

    public TriggerPoints() {
        super("Points", 0xFFB2B200, "points");
        nameEdit = new TextFieldHelper("name", this);
        amountEdit = new IntegerFieldHelper("amount", this);
    }

    @Override
    public ITrigger newInstance() {
        return new TriggerPoints();
    }

    @Override
    public Bus getBusType() {
        return Bus.NONE;
    }

    @Override
    public ITrigger deserialize(JsonObject data) {
        TriggerPoints trigger = new TriggerPoints();
        if (data.get("consume") != null) {
            trigger.consume = data.get("consume").getAsBoolean();
        }

        trigger.name = data.get("name").getAsString();
        if (data.get("amount") != null) {
            trigger.amount = data.get("amount").getAsInt();
        }

        return trigger;
    }

    @Override
    public void serialize(JsonObject data) {
        if (consume != true) {
            data.addProperty("consume", consume);
        }

        data.addProperty("name", name);
        if (amount != 1) {
            data.addProperty("amount", amount);
        }
    }

    @Override
    public void onFired(UUID uuid, ITriggerData iTriggerData, Object... data) {
        int total = CraftingAPI.players.getPlayerData(uuid).getAbilities().getPoints(name);
        if (total >= amount) {
            ((DataBoolean) iTriggerData).completed = true;
            if (consume) {
                CraftingAPI.players.getServerPlayer(uuid).addPoints(name, -amount);
            }
        }
    }

    @Override
    public Result clicked() {
        if (mouseX <= 84 && mouseX >= 1) {
            if (mouseY >= 17 && mouseY <= 25) nameEdit.select();
            if (mouseY > 25 && mouseY <= 33) amountEdit.select();
            if (mouseY > 34 && mouseY <= 41) consume = !consume;
            if (mouseY >= 17 && mouseY <= 33) return Result.ALLOW;
        }

        return Result.DEFAULT;
    }

    @Override
    public void draw() {
        int color = 0xFF000000;
        int amountColor = 0xFF000000;
        int consumeColor = 0xFF000000;
        if (mouseX <= 84 && mouseX >= 1) {
            if (mouseY >= 17 && mouseY <= 25) color = 0xFFBBBBBB;
            if (mouseY > 25 && mouseY <= 33) amountColor = 0xFFBBBBBB;
            if (mouseY > 34 && mouseY <= 41) consumeColor = 0xFFBBBBBB;
        }

        drawText("name: " + nameEdit, 4, 18, color);
        drawText("amount: " + amountEdit, 4, 26, amountColor);
        drawText("consume: " + consume, 4, 34, consumeColor);
    }
}
