package joshie.crafting.network;

import io.netty.buffer.ByteBuf;
import joshie.crafting.CraftAPIRegistry;
import joshie.crafting.Criteria;
import joshie.crafting.player.PlayerTracker;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncTriggers implements IMessage, IMessageHandler<PacketSyncTriggers, IMessage> {
    public static class SyncPair {
        public Criteria criteria;
        public int[] triggers;

        public SyncPair() {}

        public SyncPair(Criteria criteria, int[] triggers) {
            this.criteria = criteria;
            this.triggers = triggers;
        }

        public void toBytes(ByteBuf buf) {
            buf.writeInt(triggers.length);
            ByteBufUtils.writeUTF8String(buf, criteria.getUniqueName());
            for (int tech : triggers) {
                buf.writeInt(tech);
            }
        }

        public void fromBytes(ByteBuf buf) {
            int size = buf.readInt();
            criteria = CraftAPIRegistry.getCriteriaFromName(ByteBufUtils.readUTF8String(buf));
            triggers = new int[size];
            for (int i = 0; i < size; i++) {
                triggers[i] = buf.readInt();
            }
        }
    }

    private SyncPair[] toSync;
    private boolean overwrite;

    public PacketSyncTriggers() {}
    public PacketSyncTriggers(SyncPair[] toSync) {
        this.toSync = toSync;
    }

    public PacketSyncTriggers(Criteria criteria, int... triggers) {
        this.toSync = new SyncPair[] { new SyncPair(criteria, triggers) };
        this.overwrite = false;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(overwrite);
        buf.writeInt(toSync.length);
        for (SyncPair pair : toSync) {
            pair.toBytes(buf);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        overwrite = buf.readBoolean();
        int size = buf.readInt();
        toSync = new SyncPair[size];
        for (int i = 0; i < size; i++) {
            SyncPair pair = new SyncPair();
            pair.fromBytes(buf);
            toSync[i] = pair;
        }
    }

    @Override
    public IMessage onMessage(PacketSyncTriggers message, MessageContext ctx) {
        PlayerTracker.getClientPlayer().getMappings().markTriggerAsCompleted(message.overwrite, message.toSync);
        return null;
    }
}
