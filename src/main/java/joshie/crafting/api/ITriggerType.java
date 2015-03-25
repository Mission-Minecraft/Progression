package joshie.crafting.api;

import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;


public interface ITriggerType {
	/** Returns the types name **/
	public String getTypeName();
	
	public Bus getBusType();
	
	/** Creates an ITrigger from JSON **/
	public ITrigger deserialize(JsonObject data);
	
	/** Converts an ITrigger to JSON **/
	public void serialize(JsonObject elements);
	
	/** Reads extra data from nbt **/
	public Object[] readFromNBT(NBTTagCompound tag);
	
	/** Writes extra data to nbt **/
	public void writeToNBT(NBTTagCompound tag, Object[] existing);
	
	public static enum Bus {
		FML, FORGE, TERRAIN, ORE, NONE;
	}
}
