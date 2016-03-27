package joshie.progression.criteria.triggers;

import joshie.progression.api.ProgressionAPI;
import joshie.progression.api.special.ICancelable;
import joshie.progression.api.special.IInit;
import joshie.progression.items.ItemCriteria;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class TriggerChat extends TriggerBaseBoolean implements IInit, ICancelable {
    private String matchString;
    private boolean matchBoth;
    private boolean matchFront;
    private boolean matchBack;

    public String toMatch = "*help*";
    public boolean cancel = false;

    public TriggerChat() {
        super(ItemCriteria.getStackFromMeta(ItemCriteria.ItemMeta.onSentMessage), "chat", 0xFFCC6600);
    }

    @Override
    public void init() {
        if (toMatch.startsWith("*")) matchFront = true;
        else matchFront = false;
        if (toMatch.endsWith("*")) matchBack = true;
        else matchBack = false;
        matchBoth = matchFront && matchBack;
        matchString = toMatch.replaceAll("\\*", "");
    }

    @Override
    public boolean isCanceling() {
        return cancel;
    }

    @Override
    public void setCanceling(boolean isCanceled) {
        cancel = isCanceled;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommandSend(CommandEvent event) throws CommandException {
        if (!event.sender.getEntityWorld().isRemote) {
            StringBuilder string = new StringBuilder();
            for (String s : event.parameters)
                string.append(s + " ");
            String command = event.command.getCommandName() + " " + string.toString().trim();
            if (ProgressionAPI.registry.fireTrigger((EntityPlayer) event.sender.getCommandSenderEntity(), getUnlocalisedName(), command) == Result.DENY) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public boolean onFired(UUID uuid, Object... data) {
        value = isTrue(data);
        return value;
    }

    @Override
    protected boolean isTrue(Object... data) {
        String command = (String) data[0];
        if (matchBoth && matchString.contains(command)) return true;
        else if (matchFront && !matchBack && matchString.endsWith(command)) return true;
        else if (!matchFront && matchBack && matchString.startsWith(command)) return true;
        else if (matchString.equals(command)) return true;
        else return false;
    }
}
