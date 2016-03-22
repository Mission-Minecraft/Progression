package joshie.progression.network;

import io.netty.buffer.ByteBuf;
import joshie.progression.api.criteria.IProgressionCriteria;
import joshie.progression.handlers.APIHandler;
import joshie.progression.network.core.PenguinPacket;
import joshie.progression.player.PlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketSyncTriggers extends PenguinPacket {
    public static class SyncPair {
        public IProgressionCriteria criteria;
        public int[] triggers;

        public SyncPair() {}

        public SyncPair(IProgressionCriteria criteria, int[] triggers) {
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
            criteria = APIHandler.getCriteriaFromName(ByteBufUtils.readUTF8String(buf));
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

    public PacketSyncTriggers(IProgressionCriteria criteria, int... triggers) {
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
	public void handlePacket(EntityPlayer player) {   
        PlayerTracker.getClientPlayer().getMappings().markTriggerAsCompleted(overwrite, toSync);
    }
}
