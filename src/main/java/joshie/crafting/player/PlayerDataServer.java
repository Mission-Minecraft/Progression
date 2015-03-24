package joshie.crafting.player;

import java.util.HashMap;
import java.util.UUID;

import joshie.crafting.CraftingMappings;
import joshie.crafting.CraftingMod;
import joshie.crafting.api.CraftingAPI;
import joshie.crafting.api.IPlayerDataServer;
import joshie.crafting.helpers.NBTHelper;
import joshie.crafting.network.PacketHandler;
import joshie.crafting.network.PacketSyncSpeed;
import joshie.crafting.player.nbt.CraftingNBT;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerDataServer extends PlayerDataCommon implements IPlayerDataServer {
	private final UUID uuid;
	
	public PlayerDataServer(UUID uuid) {
		this.uuid = uuid;
		this.mappings.setMaster(this);
	}
	
	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public void resetData() {
		abilities = new DataAbilities();
		mappings = new CraftingMappings();
		crafts = new HashMap();
		CraftingMod.instance.createWorldData();
		CraftingAPI.registry.serverRemap();
	}

	@Override
	public void addSpeed(float speed) {
		float newSpeed = abilities.getSpeed() + speed;
		abilities.setSpeed(newSpeed);
		PacketHandler.sendToClient(new PacketSyncSpeed(newSpeed), uuid);
		markDirty();
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		abilities.readFromNBT(tag.getCompoundTag("Abilities"));
		mappings.readFromNBT(tag.getCompoundTag("Data"));
		NBTHelper.readMap(tag, "Crafting", new CraftingNBT(crafts));
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setTag("Abilities", abilities.writeToNBT(new NBTTagCompound()));
		tag.setTag("Data", mappings.writeToNBT(new NBTTagCompound()));
		NBTHelper.writeMap(tag, "Crafting", new CraftingNBT(crafts));
	}
}
