package joshie.progression.player.nbt;

import joshie.progression.api.criteria.IProgressionCriteria;
import joshie.progression.handlers.APIHandler;
import joshie.progression.helpers.NBTHelper.IMapHelper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.UUID;

public class CriteriaNBT implements IMapHelper {
	public static final CriteriaNBT INSTANCE = new CriteriaNBT();
	
	public Map map;
	
	public IMapHelper setMap(Map map) {
		this.map = map;
		return this;
	}

	@Override
	public Map getMap() {
		return map;
	}

	@Override
	public Object readKey(NBTTagCompound tag) {
		String name = tag.getString("Name");
		return APIHandler.getCriteriaFromName(UUID.fromString(name));
	}

	@Override
	public Object readValue(NBTTagCompound tag) {
		return (Integer)tag.getInteger("Repeated");
	}

	@Override
	public void writeKey(NBTTagCompound tag, Object o) {
		String name = ((IProgressionCriteria)o).getUniqueID().toString();
		tag.setString("Name", name);
	}

	@Override
	public void writeValue(NBTTagCompound tag, Object o) {
		tag.setInteger("Repeated", (Integer)o);
	}
}
