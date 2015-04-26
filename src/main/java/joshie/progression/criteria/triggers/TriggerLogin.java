package joshie.progression.criteria.triggers;

import joshie.progression.api.EventBusType;
import joshie.progression.api.ProgressionAPI;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class TriggerLogin extends TriggerBaseCounter {
    public TriggerLogin() {
        super("login", 0xFF8000FF);
    }

    @Override
    public EventBusType getEventBus() {
        return EventBusType.FML;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEvent(PlayerLoggedInEvent event) {
        ProgressionAPI.registry.fireTrigger(event.player, getUnlocalisedName());
    }

    @Override
    protected boolean canIncrease(Object... data) {
        return true;
    }
}
