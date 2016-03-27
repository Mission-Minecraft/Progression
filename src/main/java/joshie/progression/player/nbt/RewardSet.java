package joshie.progression.player.nbt;

import joshie.progression.api.criteria.IProgressionReward;
import joshie.progression.handlers.APIHandler;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.UUID;

public class RewardSet extends AbstractUniqueNBT {
    public static final RewardSet INSTANCE = new RewardSet();

    @Override
    public NBTBase write(Object s) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Reward", ((IProgressionReward) s).getUniqueID().toString());
        return tag;
    }

    @Override
    public Object read(NBTTagList list, int i) {
        NBTTagCompound tag = list.getCompoundTagAt(i);
        return APIHandler.getCache().getRewardFromUUID(UUID.fromString(tag.getString("Reward")));
    }
}
