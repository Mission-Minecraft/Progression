package joshie.progression.api;

import java.util.UUID;

import com.google.gson.JsonObject;

import joshie.progression.gui.base.DrawHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IConditionType {       
    /** Returns the unlocalised name of this condition
     *  This name is what is us in the json **/
    public String getUnlocalisedName();
    
    /** Returns the localised name of this condition**/
    public String getLocalisedName();
    
    /** Returns the colour used in the GUI editor **/
    public int getColor();
   
    /** Should true true if this condition is satisfied
     * @param world     the world object
     * @param player    the player (may be null, if the player is offline)
     * @param uuid      the uuid of the player **/
    public boolean isSatisfied(World world, EntityPlayer player, UUID uuid);

    /** Read this condition in from json **/
    public void readFromJSON(JsonObject object);
    
    /** Write this condition to json **/
    public void writeToJSON(JsonObject object);

    /** Draw this condition in the gui editor, use {@link DrawHelper}  */
    @SideOnly(Side.CLIENT)
    public void draw(int mouseX, int mouseY);

    /** Should return allow, if clicked, or default if not **/
    @SideOnly(Side.CLIENT)
    public Result onClicked(int mouseX, int mouseY);
}
