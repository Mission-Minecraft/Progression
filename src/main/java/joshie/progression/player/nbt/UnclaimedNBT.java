package joshie.progression.player.nbt;

import joshie.progression.criteria.Criteria;
import joshie.progression.handlers.APIHandler;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class UnclaimedNBT extends AbstractUniqueNBT {
    public static final UnclaimedNBT INSTANCE = new UnclaimedNBT();

    @Override
    public NBTBase write(Object s) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Criteria", ((Criteria) s).uniqueName);
        return tag;
    }

    @Override
    public Object read(NBTTagList list, int i) {
        NBTTagCompound tag = list.getCompoundTagAt(i);
        return APIHandler.getCriteriaFromName(tag.getString("Criteria"));
    }
}
