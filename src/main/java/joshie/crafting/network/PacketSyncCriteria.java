package joshie.crafting.network;

import io.netty.buffer.ByteBuf;
import joshie.crafting.CraftAPIRegistry;
import joshie.crafting.Criteria;
import joshie.crafting.Reward;
import joshie.crafting.player.PlayerTracker;
import joshie.crafting.rewards.RewardCrafting;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncCriteria implements IMessage, IMessageHandler<PacketSyncCriteria, IMessage> {
	private Criteria[] criteria;
	private Integer[] integers;
	private boolean overwrite;
    
    public PacketSyncCriteria() {}
    public PacketSyncCriteria(boolean overwrite, Integer[] values, Criteria[] criteria) {
    	this.criteria = criteria;
    	this.integers = values;
    	this.overwrite = overwrite;
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeBoolean(overwrite);
    	buf.writeInt(criteria.length);
    	for (Criteria tech: criteria) {
            ByteBufUtils.writeUTF8String(buf, tech.uniqueName);
    	}
    	
    	for (Integer i: integers) {
    		buf.writeInt(i);
    	}
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	overwrite = buf.readBoolean();
    	int size = buf.readInt();
    	criteria = new Criteria[size];
    	for (int i = 0; i < size; i++) {
    		criteria[i] = CraftAPIRegistry.getCriteriaFromName(ByteBufUtils.readUTF8String(buf));
    	}
    	
    	integers = new Integer[size];
    	for (int i = 0; i < size; i++) {
    		integers[i] = buf.readInt();
    	}
    }
    
    @Override
    public IMessage onMessage(PacketSyncCriteria message, MessageContext ctx) {    
        PlayerTracker.getClientPlayer().getMappings().markCriteriaAsCompleted(message.overwrite, message.integers, message.criteria);
        if (message.overwrite) {
        	for (Criteria condition: CraftAPIRegistry.criteria.values()) {
        		for (Criteria unlocked: message.criteria) {
        		    if (unlocked == null) continue;
        			for (Reward reward: unlocked.rewards) {
        				if (reward.getType() instanceof RewardCrafting) {
        					reward.getType().reward(PlayerTracker.getClientPlayer().getUUID());
        				}
        			}
        		}
        	}
        }
    	
    	return null;
    }
}
