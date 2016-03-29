package joshie.progression.criteria.filters.location;

import joshie.progression.lib.WorldLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.ArrayList;

public class FilterPlayerLocationLooking extends FilterLocationBase {
    public FilterPlayerLocationLooking() {
        super("playerLook", 0xFFBBBBBB);
    }

    protected MovingObjectPosition getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer player) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        double d0 = player.posX;
        double d1 = player.posY + (double)player.getEyeHeight();
        double d2 = player.posZ;
        Vec3 vec3 = new Vec3(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
            d3 = ((net.minecraft.entity.player.EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance();
        }

        Vec3 vec31 = vec3.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return worldIn.rayTraceBlocks(vec3, vec31, false, true, false);
    }

    @Override
    public WorldLocation getRandom(EntityPlayer player) {
        ArrayList<WorldLocation> locations = new ArrayList();
        MovingObjectPosition position = getMovingObjectPositionFromPlayer(player.worldObj, player);
        return new WorldLocation(player.dimension, position.getBlockPos());
    }

    @Override
    public boolean matches(WorldLocation location) {
        return true;
    }
}
