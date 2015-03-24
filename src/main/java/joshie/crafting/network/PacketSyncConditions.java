package joshie.crafting.network;

import io.netty.buffer.ByteBuf;
import joshie.crafting.api.CraftingAPI;
import joshie.crafting.api.ICriteria;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncConditions implements IMessage, IMessageHandler<PacketSyncConditions, IMessage> {
	private ICriteria[] conditions;
	private boolean overwrite;
    
    public PacketSyncConditions() {}
    public PacketSyncConditions(boolean overwrite, ICriteria... conditions) {
    	this.conditions = conditions;
    	this.overwrite = overwrite;
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeBoolean(overwrite);
    	buf.writeInt(conditions.length);
    	for (ICriteria tech: conditions) {
            ByteBufUtils.writeUTF8String(buf, tech.getUniqueName());
    	}
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	overwrite = buf.readBoolean();
    	int size = buf.readInt();
    	conditions = new ICriteria[size];
    	for (int i = 0; i < size; i++) {
    		conditions[i] = CraftingAPI.registry.getConditionFromName(ByteBufUtils.readUTF8String(buf));
    	}
    }
    
    @Override
    public IMessage onMessage(PacketSyncConditions message, MessageContext ctx) {    
    	CraftingAPI.players.getClientPlayer().getMappings().markCriteriaAsCompleted(message.overwrite, message.conditions);
        return null;
    }
}
