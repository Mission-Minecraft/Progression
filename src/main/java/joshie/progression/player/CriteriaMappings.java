package joshie.progression.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import joshie.progression.Progression;
import joshie.progression.api.ICancelable;
import joshie.progression.api.IConditionType;
import joshie.progression.api.ICriteria;
import joshie.progression.api.IRewardType;
import joshie.progression.api.ITriggerData;
import joshie.progression.api.ITriggerType;
import joshie.progression.handlers.APIHandler;
import joshie.progression.handlers.RemappingHandler;
import joshie.progression.helpers.CollectionHelper;
import joshie.progression.helpers.NBTHelper;
import joshie.progression.helpers.PlayerHelper;
import joshie.progression.helpers.TriggerHelper;
import joshie.progression.network.PacketCompleted;
import joshie.progression.network.PacketHandler;
import joshie.progression.network.PacketSyncAbilities;
import joshie.progression.network.PacketSyncCriteria;
import joshie.progression.network.PacketSyncImpossible;
import joshie.progression.network.PacketSyncTriggers;
import joshie.progression.network.PacketSyncTriggers.SyncPair;
import joshie.progression.player.nbt.CriteriaNBT;
import joshie.progression.player.nbt.CriteriaSet;
import joshie.progression.player.nbt.TriggerNBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class CriteriaMappings {
    private PlayerDataServer master;
    private UUID uuid;

    protected HashMap<ICriteria, Integer> completedCritera = new HashMap(); //All the completed criteria, with a number for how many times repeated
    protected Set<ITriggerType> completedTriggers = new HashSet(); //All the completed trigger, With their unique name as their identifier, Persistent
    protected HashMap<ITriggerType, ITriggerData> triggerData = new HashMap(); //Unique String > Data mappings for this trigger
    protected Set<ICriteria> unclaimed = new HashSet();
    protected Set<ICriteria> impossible = new HashSet();

    //Generated by the remapping
    protected Multimap<String, ITriggerType> activeTriggers; //List of all the active triggers, based on their trigger type

    //Sets the uuid associated with this class
    public void setMaster(PlayerDataServer master) {
        this.master = master;
        this.uuid = master.getUUID();
    }

    public void syncToClient(EntityPlayerMP player) {
        //remap(); //Remap the data, before the client gets sent the data

        PacketHandler.sendToClient(new PacketSyncAbilities(master.getAbilities()), player);
        SyncPair[] values = new SyncPair[APIHandler.getCriteria().size()];
        int pos = 0;
        for (ICriteria criteria : APIHandler.getCriteria().values()) {
            int[] numbers = new int[criteria.getTriggers().size()];
            for (int i = 0; i < criteria.getTriggers().size(); i++) {
                numbers[i] = i;
            }

            values[pos] = new SyncPair(criteria, numbers);

            pos++;
        }

        PacketHandler.sendToClient(new PacketSyncImpossible(impossible.toArray(new ICriteria[impossible.size()])), player);
        PacketHandler.sendToClient(new PacketSyncTriggers(values), player); //Sync all researches to the client
        PacketHandler.sendToClient(new PacketSyncCriteria(true, completedCritera.values().toArray(new Integer[completedCritera.size()]), completedCritera.keySet().toArray(new ICriteria[completedCritera.size()])), player); //Sync all conditions to the client
    }

    //Reads the completed criteria
    public void readFromNBT(NBTTagCompound nbt) {
        NBTHelper.readTagCollection(nbt, "Impossible Criteria", CriteriaSet.INSTANCE.setCollection(impossible));
        NBTHelper.readTagCollection(nbt, "Unclaimed Criteria", CriteriaSet.INSTANCE.setCollection(unclaimed));
        NBTHelper.readTagCollection(nbt, "Completed Triggers", TriggerNBT.INSTANCE.setCollection(completedTriggers));
        NBTHelper.readMap(nbt, "Completed Criteria", CriteriaNBT.INSTANCE.setMap(completedCritera));
        NBTTagList data = nbt.getTagList("Active Trigger Data", 10);
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound tag = data.getCompoundTagAt(i);
            String name = tag.getString("Name");
            ICriteria criteria = APIHandler.getCriteriaFromName(name);
            if (criteria != null) {
                for (ITriggerType trigger : criteria.getTriggers()) {
                    ITriggerData iTriggerData = trigger.newData();
                    iTriggerData.readFromNBT(tag);
                    triggerData.put(trigger, iTriggerData);
                }
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTHelper.writeCollection(nbt, "Impossible Criteria", CriteriaSet.INSTANCE.setCollection(impossible));
        NBTHelper.writeCollection(nbt, "Unclaimed Criteria", CriteriaSet.INSTANCE.setCollection(unclaimed));
        NBTHelper.writeCollection(nbt, "Completed Triggers", TriggerNBT.INSTANCE.setCollection(completedTriggers));
        NBTHelper.writeMap(nbt, "Completed Criteria", CriteriaNBT.INSTANCE.setMap(completedCritera));
        //Save the extra data for the existing triggers
        NBTTagList data = new NBTTagList();
        for (ITriggerType trigger : triggerData.keySet()) {
            if (trigger != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("Name", trigger.getCriteria().getUniqueName());
                ITriggerData iTriggerData = triggerData.get(trigger);
                iTriggerData.writeToNBT(tag);
                data.appendTag(tag);
            }
        }

        nbt.setTag("Active Trigger Data", data);
        return nbt;
    }

    public HashMap<ICriteria, Integer> getCompletedCriteria() {
        return completedCritera;
    }

    public Set<ITriggerType> getCompletedTriggers() {
        return completedTriggers;
    }

    public void markCriteriaAsCompleted(boolean overwrite, Integer[] values, ICriteria... conditions) {
        if (overwrite) completedCritera = new HashMap();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == 0) {
                completedCritera.remove(conditions[i]);
            } else completedCritera.put(conditions[i], values[i]);
        }
    }

    public void markTriggerAsCompleted(boolean overwrite, SyncPair[] pairs) {
        if (overwrite) completedTriggers = new HashSet();
        for (SyncPair pair : pairs) {
            if (pair == null || pair.criteria == null) continue; //Avoid broken pairs
            for (int i = 0; i < pair.triggers.length; i++) {
                int num = pair.triggers[i];
                if (pair.criteria.getTriggers().size() > num) completedTriggers.add(pair.criteria.getTriggers().get(num));
            }
        }
    }

    private boolean containsAny(List<ICriteria> list) {
        for (ICriteria criteria : list) {
            if (completedCritera.keySet().contains(criteria)) return true;
        }

        return false;
    }

    private ITriggerData getTriggerData(ITriggerType trigger) {
        ITriggerData data = triggerData.get(trigger);
        if (data == null) {
            data = trigger.newData();
            triggerData.put(trigger, data);
            return data;
        } else return data;
    }
    
    public boolean isImpossible(ICriteria criteria) {
        return impossible.contains(criteria);
    }
    
    public void setImpossibles(ICriteria... criteria) {
        for (ICriteria c : criteria) {
            impossible.add(c);
        }
    }
    
    public void switchPossibility(ICriteria criteria) {
        boolean isPossible = !isImpossible(criteria);
        if (isPossible) impossible.add(criteria);
        else CollectionHelper.remove(impossible, criteria);
        
        Progression.data.markDirty();
        PacketHandler.sendToClient(new PacketSyncImpossible(impossible.toArray(new ICriteria[impossible.size()])), uuid);
    }
    
    public void claimUnclaimedReward(ICriteria criteria) {
        if (unclaimed.contains(criteria)) {
            unclaimed.remove(criteria);
            claimRewards(criteria);

            Progression.data.markDirty();
        }
    }

    /** Called to fire a trigger type, Triggers are only ever called on criteria that is activated **/
    public Result fireAllTriggers(String type, Object... data) {
        if (activeTriggers == null) return Result.DEFAULT; //If the remapping hasn't occured yet, say goodbye!
        //If the trigger is a forced removal, then force remve it
        if (type.equals("forced-remove")) {
            ICriteria criteria = (ICriteria) data[0];
            if (criteria == null || !completedCritera.keySet().contains(criteria)) return Result.DEFAULT;
            else removeCriteria(criteria);
            remap(); //Remap everything
            Progression.data.markDirty();
            return Result.ALLOW;
        }

        EntityPlayer player = PlayerHelper.getPlayerFromUUID(uuid);
        World world = player == null ? DimensionManager.getWorld(0) : player.worldObj;
        boolean completedAnyCriteria = false;
        Collection<ITriggerType> triggers = activeTriggers.get(type);
        HashSet<ITriggerType> cantContinue = new HashSet();
        List<ITriggerType> toTrigger = new ArrayList();
        for (ITriggerType trigger : triggers) {
            Collection<IConditionType> conditions = trigger.getConditions();
            for (IConditionType condition : conditions) { //If we're bypassing everything, ignore conditions
                if (condition.isSatisfied(world, player, uuid) == condition.isInverted()) {
                    cantContinue.add(trigger);
                    break;
                }
            }

            if (cantContinue.contains(trigger)) continue; //Grab the old data
            toTrigger.add(trigger); //Add triggers for firing
        }

        //Fire the trigger
        Collections.shuffle(toTrigger);
        for (ITriggerType trigger : toTrigger) {
            if (trigger instanceof ICancelable) {
                boolean isCancelingEnabled = (((ICancelable) trigger).isCanceling());
                if ((trigger.onFired(uuid, getTriggerData(trigger), data))) {
                     if (isCancelingEnabled) return Result.DENY;
                }
            } else if (!trigger.onFired(uuid, getTriggerData(trigger), data)) {
                return Result.DENY;
            }
        }

        //Next step, now that the triggers have been fire, we need to go through them again
        //Check if they have been satisfied, and if so, mark them as completed triggers
        HashSet<ITriggerType> toRemove = new HashSet();
        for (ITriggerType trigger : triggers) {
            if (cantContinue.contains(trigger)) continue; //If we're bypassing mark all triggers as fired
            if (trigger.isCompleted(getTriggerData(trigger))) {
                completedTriggers.add(trigger);
                toRemove.add(trigger);
                PacketHandler.sendToClient(new PacketSyncTriggers(trigger.getCriteria(), TriggerHelper.getInternalID(trigger)), uuid);
            }
        }

        //Remove completed triggers from the active map
        for (ITriggerType trigger : toRemove) {
            triggers.remove(toRemove);
        }

        //Create a list of new triggers to add to the active trigger map
        HashSet<ITriggerType> forRemovalFromActive = new HashSet(); //Reset them
        HashSet<ITriggerType> forRemovalFromCompleted = new HashSet();
        HashSet<ICriteria> toRemap = new HashSet();
        HashSet<ICriteria> toComplete = new HashSet();

        //Next step, now that we have fired the trigger, we need to go through all the active criteria
        //We should check if all triggers have been fulfilled
        for (ITriggerType trigger : triggers) {
            if (cantContinue.contains(trigger) || trigger.getCriteria() == null) continue;
            ICriteria criteria = trigger.getCriteria();
            if (impossible.contains(criteria)) continue;
            
            //Check that all triggers are in the completed set
            List<ITriggerType> allTriggers = criteria.getTriggers();
            boolean allRequired = criteria.getIfRequiresAllTasks();
            int countRequired = criteria.getTasksRequired();
            int firedCount = 0;
            boolean allFired = true;
            for (ITriggerType criteriaTrigger : allTriggers) { //the completed triggers map, doesn't contains all the requirements, then we need to remove it
                if (!completedTriggers.contains(criteriaTrigger)) {
                    allFired = false;
                } else firedCount++;
            }

            //Complete the criteria and bypass any requirements
            if ((allFired && allRequired) || (firedCount >= countRequired)) {
                completedAnyCriteria = true;
                toComplete.add(criteria);
            }
        }

        //Add the bypassing of requirements completion
        if (type.equals("forced-complete")) {
            ICriteria criteria = (ICriteria) data[0];
            boolean repeat = criteria.canRepeatInfinite();
            if (!repeat) {
                int max = criteria.getRepeatAmount();
                int last = getCriteriaCount(criteria);
                repeat = last < max;
            }

            if (repeat) { //If we're allowed to fire again, do so
                completedAnyCriteria = true;
                toComplete.add(criteria);
            }
        }

        for (ICriteria criteria : toComplete) {
            completeCriteria(criteria, forRemovalFromActive, toRemap);
        }

        remapStuff(forRemovalFromActive, toRemap);
        //Now that we have removed all the triggers, and marked this as completed and remapped data, we should give out the rewards
        for (ICriteria criteria : toComplete) {
            if (!criteria.requiresClaiming()) {
                claimRewards(criteria);
            } else unclaimed.add(criteria);
        }

        //Mark data as dirty, whether it changed or not
        Progression.data.markDirty();
        return completedAnyCriteria ? Result.ALLOW : Result.DEFAULT;
    }

    public void claimRewards(ICriteria criteria) {
        ArrayList<IRewardType> copy = new ArrayList(criteria.getRewards());
        Collections.shuffle(copy);
        int rewardsGiven = 0;

        for (IRewardType reward : copy) {
            if (rewardsGiven < criteria.getAmountOfRewards() || criteria.givesAllRewards()) {
                reward.reward(uuid);
            } else if (rewardsGiven >= criteria.getAmountOfRewards() && !criteria.givesAllRewards()) {
                break; //Exit the loop, no use carrying on if we've reached the maximum rewards allowed
            }

            rewardsGiven++;
        }
    }

    public void removeCriteria(ICriteria criteria) {
        completedCritera.remove(criteria);
        PacketHandler.sendToClient(new PacketSyncCriteria(false, new Integer[] { 0 }, new ICriteria[] { criteria }), uuid);
    }

    private void completeCriteria(ICriteria criteria, HashSet<ITriggerType> forRemovalFromActive, HashSet<ICriteria> toRemap) {
        List<ITriggerType> allTriggers = criteria.getTriggers();
        int completedTimes = getCriteriaCount(criteria);
        completedTimes++;
        completedCritera.put(criteria, completedTimes);
        //Now that we have updated how times we have completed this quest
        //We should mark all the triggers for removal from activetriggers, as well as actually remove their stored data
        for (ITriggerType criteriaTrigger : allTriggers) {
            forRemovalFromActive.add(criteriaTrigger);
            //Remove all the conflicts triggers
            for (ICriteria conflict : criteria.getConflicts()) {
                forRemovalFromActive.addAll(conflict.getTriggers());
            }

            triggerData.remove(criteriaTrigger);
        }

        //The next step in the process is to update the active trigger maps for everything
        //That we unlock with this criteria have been completed
        toRemap.add(criteria);

        if (completedTimes == 1) { //Only do shit if this is the first time it was completed                    
            toRemap.addAll(RemappingHandler.criteriaToUnlocks.get(criteria));
        }

        List<EntityPlayerMP> list = PlayerHelper.getPlayersFromUUID(uuid);
        for (EntityPlayerMP player : list) {
            PacketHandler.sendToClient(new PacketSyncCriteria(false, new Integer[] { completedTimes }, new ICriteria[] { criteria }), player);
            if (criteria.displayAchievement()) PacketHandler.sendToClient(new PacketCompleted(criteria), player);
        }
    }

    private void remapStuff(HashSet<ITriggerType> forRemovalFromActive, HashSet<ICriteria> toRemap) {
        //Removes all the triggers from the active map
        for (ITriggerType trigger : forRemovalFromActive) {
            activeTriggers.get(trigger.getUnlocalisedName()).remove(trigger);
        }

        //Remap the criteria
        for (ICriteria criteria : toRemap) {
            remapCriteriaOnCompletion(criteria);
        }
    }

    public int getCriteriaCount(ICriteria criteria) {
        int amount = 0;
        Integer last = completedCritera.get(criteria);
        if (last != null) {
            amount = last;
        }

        return amount;
    }

    private void remapCriteriaOnCompletion(ICriteria criteria) {
        ICriteria available = null;
        //We are now looping though all criteria, we now need to check to see if this
        //First step is to validate to see if this criteria, is available right now
        //If the criteria is repeatable, or is not completed continue
        boolean repeat = criteria.canRepeatInfinite();
        if (!repeat) {
            int max = criteria.getRepeatAmount();
            int last = getCriteriaCount(criteria);
            repeat = last < max;
        }

        if (repeat) {
            if (completedCritera.keySet().containsAll(criteria.getPreReqs())) {
                //If we have all the requirements, continue
                //Now that we know that we have all the requirements, we should check for conflicts
                //If it doesn't contain any of the conflicts, continue forwards
                if (!containsAny(criteria.getConflicts())) {
                    //The Criteria passed the check for being available, mark it as so
                    available = criteria;
                }
            }

            //If we are allowed to redo triggers, remove from completed
            completedTriggers.removeAll(criteria.getTriggers());
        }

        if (available != null) {
            List<ITriggerType> triggers = criteria.getTriggers(); //Grab a list of all the triggers
            for (ITriggerType trigger : triggers) {
                //If we don't have the trigger in the completed map, mark it as available in the active triggers
                if (!completedTriggers.contains(trigger)) {
                    activeTriggers.get(trigger.getUnlocalisedName()).add((ITriggerType) trigger);
                }
            }
        }
    }

    public void remap() {
        Set<ICriteria> availableCriteria = new HashSet(); //Recreate the available mappings
        activeTriggers = HashMultimap.create(); //Recreate the trigger mappings

        Collection<ICriteria> allCriteria = APIHandler.getCriteria().values();
        for (ICriteria criteria : allCriteria) {
            //If the criteria has been marked as impossible don't attach it to anything
            if (impossible.contains(criteria)) continue;
            
            //We are now looping though all criteria, we now need to check to see if this
            //First step is to validate to see if this criteria, is available right now
            //If the criteria is repeatable, or is not completed continue
            boolean repeat = criteria.canRepeatInfinite();
            if (!repeat) {
                int max = criteria.getRepeatAmount();
                int last = getCriteriaCount(criteria);
                repeat = last < max;
            }

            if (repeat) {
                if (completedCritera.keySet().containsAll(criteria.getPreReqs())) {
                    //If we have all the requirements, continue
                    //Now that we know that we have all the requirements, we should check for conflicts
                    //If it doesn't contain any of the conflicts, continue forwards
                    if (!containsAny(criteria.getConflicts())) {
                        //The Criteria passed the check for being available, mark it as so
                        availableCriteria.add(criteria);
                    }
                }
            }
        }

        //Now that we have remapped all of the criteria, we should remap the triggers
        for (ICriteria criteria : availableCriteria) {
            List<ITriggerType> triggers = criteria.getTriggers(); //Grab a list of all the triggers
            for (ITriggerType trigger : triggers) {
                //If we don't have the trigger in the completed map, mark it as available in the active triggers
                if (!completedTriggers.contains(trigger)) {
                    activeTriggers.get(trigger.getUnlocalisedName()).add((ITriggerType) trigger);
                }
            }
        }
    }
}
