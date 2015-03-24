package joshie.crafting;

import static joshie.crafting.lib.CraftingInfo.JAVAPATH;
import static joshie.crafting.lib.CraftingInfo.MODID;
import static joshie.crafting.lib.CraftingInfo.MODNAME;
import static joshie.crafting.lib.CraftingInfo.VERSION;

import java.io.File;
import java.util.Map;

import joshie.crafting.api.CraftingAPI;
import joshie.crafting.asm.CraftingTransformer;
import joshie.crafting.player.PlayerSavedData;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = MODID, name = MODNAME, version = VERSION)
public class CraftingMod implements IFMLLoadingPlugin {
	public static final Logger logger = LogManager.getLogger(MODNAME);
	
	@SidedProxy(clientSide = JAVAPATH + "CraftingClient", serverSide = JAVAPATH + "CraftingCommon")
    public static CraftingCommon proxy;
	
	@Instance(MODID)
    public static CraftingMod instance;
	public static File configDir;
	
	public static PlayerSavedData data;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}
	
	/* Set up the save data */
	@EventHandler
	public void onServerStarted(FMLServerStartedEvent event) {
	      if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) return;
	      World world = MinecraftServer.getServer().worldServers[0];
	      data = (PlayerSavedData) world.loadItemData(PlayerSavedData.class, MODNAME);
	      if (data == null) {
	    	  createWorldData();
	      }
	      
	    //Remap all relevant data
		CraftingAPI.registry.serverRemap();
	}
	
	 @EventHandler
	 public void onServerStarting(FMLServerStartingEvent event) {
	 	ICommandManager manager = event.getServer().getCommandManager();
	    if (manager instanceof ServerCommandManager) {
	    	((ServerCommandManager) manager).registerCommand(CraftingAPI.commands);
	    }
	  }
	
	public void createWorldData() {
		World world = MinecraftServer.getServer().worldServers[0];
		data = new PlayerSavedData(MODNAME);
  	    world.setItemData(MODNAME, data);
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] { CraftingTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {}

	@Override
	public String getAccessTransformerClass() {
		return "";
	}
}
