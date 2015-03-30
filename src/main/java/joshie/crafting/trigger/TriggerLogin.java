package joshie.crafting.trigger;

import joshie.crafting.api.Bus;
import joshie.crafting.api.CraftingAPI;
import joshie.crafting.api.ITrigger;
import joshie.crafting.gui.TextFieldHelper.IntegerFieldHelper;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class TriggerLogin extends TriggerBaseCounter {
    private IntegerFieldHelper amountEdit;
    public int amount = 1;

    public TriggerLogin() {
        super("Login", 0xFF8000FF, "login");
        amountEdit = new IntegerFieldHelper("amount", this);
    }

    @Override
    public ITrigger newInstance() {
        return new TriggerLogin();
    }

    @Override
    public Bus getBusType() {
        return Bus.FML;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEvent(PlayerLoggedInEvent event) {
        CraftingAPI.registry.fireTrigger(event.player, getTypeName());
    }

    @Override
    public ITrigger deserialize(JsonObject data) {
        TriggerLogin trigger = new TriggerLogin();
        if (data.get("amount") != null) {
            trigger.amount = data.get("amount").getAsInt();
        }

        return trigger;
    }

    @Override
    public void serialize(JsonObject data) {
        if (amount != 1) {
            data.addProperty("amount", amount);
        }
    }

    @Override
    protected boolean canIncrease(Object... data) {
        return true;
    }

    @Override
    public Result clicked() {
        if (mouseX <= 84 && mouseX >= 1) {
            if (mouseY >= 17 && mouseY <= 25) {
                amountEdit.select();
                return Result.ALLOW;
            }
        }

        return Result.DEFAULT;
    }

    @Override
    public void draw() {
        int color = 0xFF000000;
        if (mouseX <= 84 && mouseX >= 1) {
            if (mouseY >= 17 && mouseY <= 25) color = 0xFFBBBBBB;
        }

        drawText("times: " + amountEdit, 4, 18, color);
    }
    
    @Override
    public int getAmountRequired() {
        return amount;
    }
}
