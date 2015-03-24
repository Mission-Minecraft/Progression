package joshie.crafting.player;

import java.util.UUID;

import joshie.crafting.api.ICraftingMappings;
import joshie.crafting.api.ICriteria;
import joshie.crafting.api.IPlayerDataClient;
import joshie.crafting.api.ITrigger;
import joshie.crafting.helpers.ClientHelper;
import joshie.crafting.helpers.PlayerHelper;


public class PlayerDataClient extends PlayerDataCommon implements IPlayerDataClient {
	private static PlayerDataClient INSTANCE = new PlayerDataClient();
	
	public static PlayerDataClient getInstance() {
		return INSTANCE;
	}
	
	public static void newInstance() {
		INSTANCE = new PlayerDataClient();
	}

	@Override
	public UUID getUUID() {
		return PlayerHelper.getUUIDForPlayer(ClientHelper.getPlayer());
	}

	@Override
	public void setSpeed(float speed) {
		this.abilities.setSpeed(speed);
	}
}
