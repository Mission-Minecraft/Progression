package joshie.progression.criteria.rewards;

import java.util.List;
import java.util.UUID;

import joshie.progression.api.IHasEventBus;
import joshie.progression.gui.fields.TextField;
import joshie.progression.player.PlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RewardFallDamage extends RewardBase implements IHasEventBus {
    public int absorption = 1;

    public RewardFallDamage() {
        super(new ItemStack(Items.feather), "fallDamage", 0xFF661A00);
        list.add(new TextField("absorption", this));
    }

    @Override
    public EventBus getEventBus() {
        return MinecraftForge.EVENT_BUS;
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingFallEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            int damage = (int) (event.distance - 3);
            int maxAbsorbed = PlayerTracker.getPlayerData(player).getAbilities().getFallDamagePrevention();
            if (damage < maxAbsorbed) {
                event.setCanceled(true);
            } else {
                event.distance = event.distance - maxAbsorbed;
            }
        }
    }

    @Override
    public void reward(UUID uuid) {
        PlayerTracker.getServerPlayer(uuid).addFallDamagePrevention(absorption);
    }

    @Override
    public void addTooltip(List list) {
        list.add(EnumChatFormatting.WHITE + "Ability Gain");
        list.add("Fall Resistance: " + absorption);
    }
}
