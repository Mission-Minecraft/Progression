package joshie.progression.network;

import io.netty.buffer.ByteBuf;
import joshie.progression.helpers.ClientHelper;
import joshie.progression.helpers.SpawnItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRewardItem implements IMessage, IMessageHandler<PacketRewardItem, IMessage> {
    private ItemStack stack;

    public PacketRewardItem() {}

    public PacketRewardItem(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public IMessage onMessage(PacketRewardItem message, MessageContext ctx) {
        SpawnItemHelper.addToPlayerInventory(ClientHelper.getPlayer(), message.stack);
        return null;
    }
}
