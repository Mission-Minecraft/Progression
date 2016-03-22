package joshie.progression.criteria.conditions;

import java.util.UUID;

import com.google.gson.JsonObject;

import joshie.progression.api.special.ISpecialJSON;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ConditionCoordinates extends ConditionBase implements ISpecialJSON {
    public boolean checkDimension = false; //Whether we check the dimension
    public boolean checkX = true; //Whether we check the x coordinate
    public boolean checkY = true; //Whether we check the y coordinate
    public boolean checkZ = true; //Whether we check the x coordinate
    public int radius = 0; //How much further to check than the block
    public int dimension = 0, x = 0, y = 0, z = 0; //The coordinates
    public boolean greaterThan = false; //If we are checking greater than
    public boolean lessThan = false; //If we are checking less than

    public ConditionCoordinates() {
        super("coordinates", 0xFF000000);
    }

    @Override
    public boolean isSatisfied(World world, EntityPlayer player, UUID uuid) {
        if (player == null) return false;
        int playerDimension = player.worldObj.provider.getDimensionId();
        int playerX = (int) player.posX;
        int playerY = (int) player.posY;
        int playerZ = (int) player.posZ;

        if (checkDimension) if (playerDimension != dimension) return false;
        boolean xMatches = checkX ? false : true;
        boolean yMatches = checkY ? false : true;
        boolean zMatches = checkZ ? false : true;
        if (greaterThan || lessThan) {
            if (greaterThan) {
                if (playerX >= x) xMatches = true;
                if (playerY >= y) yMatches = true;
                if (playerZ >= z) zMatches = true;
            } else {
                if (playerX <= x) xMatches = true;
                if (playerY <= y) yMatches = true;
                if (playerZ <= z) zMatches = true;
            }
        } else {
            //Check the xCoordinate
            if (checkX) {
                for (int xCoord = playerX - radius; xCoord <= playerX + radius; xCoord++) {
                    if (xCoord == x) {
                        xMatches = true;
                        break;
                    }
                }
            }
            //Check the yCoordinate
            if (checkY) {
                for (int yCoord = playerY - radius; yCoord <= playerY + radius; yCoord++) {
                    if (yCoord == y) {
                        yMatches = true;
                        break;
                    }
                }
            }
            //Check the zCoordinate
            if (checkZ) {
                for (int zCoord = playerZ - radius; zCoord <= playerZ + radius; zCoord++) {
                    if (zCoord == z) {
                        zMatches = true;
                        break;
                    }
                }
            }
        }

        return xMatches && yMatches && zMatches;
    }

    @Override
    public boolean onlySpecial() {
        return false;
    }

    @Override
    public void readFromJSON(JsonObject data) {
        
    }

    @Override
    public void writeToJSON(JsonObject data) {
        
    }
}
