package joshie.progression.api;

import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IRewardType {    
    /** Associates this reward type with the criteria
     *  Most reward types will not need access to this. **/
    public void markCriteria(ICriteria criteria);
    
    /** Returns the name of this trigger type, used to call from the json **/
    public String getUnlocalisedName();
    
    /** Returns the localised name for this trigger type **/
    public String getLocalisedName();
    
    /** Returns the colour used for this trigger type **/
    public int getColor();

    /** This class will automatically be registered/de-registered from the event
     *  buses that are returned from this */
    public EventBusType[] getEventBusTypes();
    
    /** Gives the reward to this UUID **/
    public void reward(UUID uuid);
    
    /** Called when the reward is added **/
    public void onAdded();
    
    /** Called when the reward is removed**/
    public void onRemoved();
    
    /** Return the itemstack that represents this reward in tree editor view **/
    public ItemStack getIcon();

    /** Add a tooltip for display in the tree view **/
    public void addTooltip(List list);

    /** Called to load the data about this trigger from json **/
    public void readFromJSON(JsonObject object);
    
    /** Called to save the data about this trigger to json **/
    public void writeToJSON(JsonObject object);

    /** Called to draw the information when editing this trigger  */
    @SideOnly(Side.CLIENT)
    public void draw(int mouseX, int mouseY);

    /** Should return allow, if clicked, or default if not **/
    @SideOnly(Side.CLIENT)
    public Result onClicked(int mouseX, int mouseY);
}
