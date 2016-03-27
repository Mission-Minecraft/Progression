package joshie.progression.network;

import io.netty.buffer.ByteBuf;
import joshie.progression.api.criteria.IProgressionCriteria;
import joshie.progression.handlers.APIHandler;
import joshie.progression.network.core.PenguinPacket;
import joshie.progression.player.PlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.UUID;

public class PacketSyncCriteria extends PenguinPacket {
    private IProgressionCriteria[] criteria;
    private Integer[] integers;
    private boolean overwrite;

    public PacketSyncCriteria() {}

    public PacketSyncCriteria(boolean overwrite, Integer[] values, IProgressionCriteria[] criteria) {
        this.criteria = criteria;
        this.integers = values;
        this.overwrite = overwrite;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(overwrite);
        buf.writeInt(criteria.length);
        for (IProgressionCriteria tech : criteria) {
            ByteBufUtils.writeUTF8String(buf, tech.getUniqueID().toString());
        }

        for (Integer i : integers) {
            buf.writeInt(i);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        overwrite = buf.readBoolean();
        int size = buf.readInt();
        criteria = new IProgressionCriteria[size];
        for (int i = 0; i < size; i++) {
            criteria[i] = APIHandler.getCriteriaFromName(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        }

        integers = new Integer[size];
        for (int i = 0; i < size; i++) {
            integers[i] = buf.readInt();
        }
    }

    @Override
    public void handlePacket(EntityPlayer player) {
        PlayerTracker.getClientPlayer().getMappings().markCriteriaAsCompleted(overwrite, integers, criteria);
    }
}
