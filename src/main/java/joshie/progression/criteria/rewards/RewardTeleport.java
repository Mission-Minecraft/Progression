package joshie.progression.criteria.rewards;

import joshie.progression.Progression;
import joshie.progression.api.criteria.*;
import joshie.progression.api.special.*;
import joshie.progression.gui.fields.ItemFilterField;
import joshie.progression.gui.filters.FilterTypeLocation;
import joshie.progression.lib.WorldLocation;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ProgressionRule(name="teleport", color=0xFFDDDDDD, icon="minecraft:ender_pearl")
public class RewardTeleport extends RewardBase implements ICustomDescription, IHasFilters, ISpecialFieldProvider {
    public List<IFilterProvider> locations = new ArrayList();
    protected transient IField field;

    public RewardTeleport() {
        field = new ItemFilterField("locations", this);
    }

    @Override
    public String getDescription() {
        return Progression.translate(getProvider().getUnlocalisedName() + ".description") + " \n" + field.getField();
    }

    @Override
    public List<IFilterProvider> getAllFilters() {
        return locations;
    }

    @Override
    public IFilterType getFilterForField(String fieldName) {
        return FilterTypeLocation.INSTANCE;
    }

    @Override
    public void addSpecialFields(List<IField> fields, DisplayMode mode) {
        fields.add(new ItemFilterField("locations", this));
    }

    @Override
    public void reward(EntityPlayerMP player) {
        boolean notteleported = true;
        for (int i = 0; i < 10 && notteleported; i++) {
            WorldLocation location = WorldLocation.getRandomLocationFromFilters(locations, player);
            if (location != null) {
                World world = DimensionManager.getWorld(location.dimension);
                int dimension = location.dimension;
                if (world == null) continue; //NO!!!!
                if (player.dimension != dimension) {
                    int oldDim = player.dimension;
                    ServerConfigurationManager manager = player.mcServer.getConfigurationManager();
                    WorldServer worldserver = manager.getServerInstance().worldServerForDimension(player.dimension);
                    player.dimension = dimension;
                    WorldServer worldserver1 = manager.getServerInstance().worldServerForDimension(player.dimension);
                    player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.getDifficulty(), player.worldObj.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
                    worldserver.removePlayerEntityDangerously(player);
                    if (player.riddenByEntity != null) {
                        player.riddenByEntity.mountEntity(null);
                    }

                    if (player.ridingEntity != null) {
                        player.mountEntity(null);
                    }

                    player.isDead = false;
                    WorldProvider pOld = worldserver.provider;
                    WorldProvider pNew = worldserver1.provider;
                    double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
                    double x = player.posX * moveFactor;
                    double z = player.posZ * moveFactor;

                    worldserver.theProfiler.startSection("placing");
                    x = MathHelper.clamp_double(x, -29999872, 29999872);
                    z = MathHelper.clamp_double(z, -29999872, 29999872);

                    if (player.isEntityAlive()) {
                        player.setLocationAndAngles(x, player.posY, z, player.rotationYaw, player.rotationPitch);
                        worldserver1.spawnEntityInWorld(player);
                        worldserver1.updateEntityWithOptionalForce(player, false);
                    }

                    worldserver.theProfiler.endSection();

                    player.setWorld(worldserver1);
                    manager.preparePlayer(player, worldserver);
                    player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                    player.theItemInWorldManager.setWorld(worldserver1);
                    manager.updateTimeAndWeatherForPlayer(player, worldserver1);
                    manager.syncPlayerInventory(player);

                    Iterator<PotionEffect> iterator = player.getActivePotionEffects().iterator();
                    while (iterator.hasNext()) {
                        PotionEffect potioneffect = iterator.next();
                        player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
                    }

                    FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
                }

                BlockPos pos = new BlockPos(location.pos);
                if (world.isBlockLoaded(pos)) {
                    if (isValidLocation(world, pos)) {
                        notteleported = false;
                        player.setPositionAndUpdate(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D);
                    }
                }
            }
        }
    }

    //Helper Methods
    private boolean isValidLocation(World world, BlockPos pos) {
        Material posfloor = world.getBlockState(pos).getBlock().getMaterial();
        Material posfeet = world.getBlockState(pos.up()).getBlock().getMaterial();
        Material poshead = world.getBlockState(pos.up(2)).getBlock().getMaterial();
        if (posfeet.blocksMovement()) return false;
        if (poshead.blocksMovement()) return false;
        if (posfloor.isLiquid() || posfeet.isLiquid() || poshead.isLiquid()) return false;
        return posfloor.blocksMovement();
    }
}
