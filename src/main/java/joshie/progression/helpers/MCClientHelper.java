package joshie.progression.helpers;

import joshie.progression.api.special.DisplayMode;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class MCClientHelper {
    public static boolean FORCE_EDIT = false;

    public static EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	public static Minecraft getMinecraft() {
		return Minecraft.getMinecraft();
	}

    public static World getWorld() {
        return getPlayer().worldObj;
    }
    
    private static boolean isInEditMode() {
        return FORCE_EDIT ? true: getPlayer().capabilities.isCreativeMode;
    }

    public static DisplayMode getMode() {
        return isInEditMode() ? DisplayMode.EDIT : DisplayMode.DISPLAY;
    }
}
