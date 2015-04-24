package joshie.crafting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import joshie.crafting.api.CraftingAPI;
import joshie.crafting.api.ICondition;
import joshie.crafting.api.IConditionType;
import joshie.crafting.api.IRegistry;
import joshie.crafting.api.IResearch;
import joshie.crafting.api.IReward;
import joshie.crafting.api.IRewardType;
import joshie.crafting.api.ITab;
import joshie.crafting.api.ITrigger;
import joshie.crafting.api.ITriggerData;
import joshie.crafting.api.ITriggerNew;
import joshie.crafting.api.ITriggerType;
import joshie.crafting.helpers.PlayerHelper;
import joshie.crafting.trigger.data.DataBoolean;
import joshie.crafting.trigger.data.DataCount;
import joshie.crafting.trigger.data.DataCrafting;
import net.minecraft.entity.player.EntityPlayer;

import com.google.gson.JsonObject;

public class CraftAPIRegistry implements IRegistry {
    //This is the registry for trigger type and reward type creation
    public static final HashMap<String, ITriggerType> triggerTypes = new HashMap();
    public static final HashMap<String, IRewardType> rewardTypes = new HashMap();
    public static final HashMap<String, IConditionType> conditionTypes = new HashMap();

    //These four maps are registries for fetching the various types
    public static HashMap<String, ITab> tabs;
    public static HashMap<String, Criteria> criteria;
    public static Set<ITrigger> triggers;
    public static Set<ICondition> conditions;
    public static Set<IReward> rewards;

    @Override
    //Fired Server Side only
    public boolean fireTrigger(UUID uuid, String string, Object... data) {
        return CraftingAPI.players.getServerPlayer(uuid).getMappings().fireAllTriggers(string, data);
    }

    @Override
    //Fired Server Side only
    public boolean fireTrigger(EntityPlayer player, String string, Object... data) {
        if (!player.worldObj.isRemote) {
            return fireTrigger(PlayerHelper.getUUIDForPlayer(player), string, data);
        } else return false;
    }

    @Override
    public IConditionType registerConditionType(IConditionType type) {
        conditionTypes.put(type.getTypeName(), type);
        return type;
    }

    @Override
    public ITriggerType registerTriggerType(ITriggerType type) {
        triggerTypes.put(type.getTypeName(), type);
        return type;
    }

    @Override
    public IRewardType registerRewardType(IRewardType type) {
        rewardTypes.put(type.getTypeName(), type);
        return type;
    }

    @Override
    public Criteria newCriteria(ITab tab, String name) {
        Criteria condition = new Criteria().setUniqueName(name).setTab(tab);
        tab.addCriteria(condition);
        criteria.put(name, condition);
        return condition;
    }

    @Override
    public ITab newTab(String name) {
        ITab iTab = new CraftingTab().setUniqueName(name);
        tabs.put(name, iTab);
        return iTab;
    }

    @Override
    public ICondition newCondition(Criteria criteria, String type, JsonObject data) {
        boolean inverted = data.get("inverted") != null ? data.get("inverted").getAsBoolean() : false;
        ICondition condition = conditionTypes.get(type).deserialize(data).setInversion(inverted).setCriteria(criteria);
        conditions.add(condition);
        return condition;
    }

    @Override
    public ITrigger newTrigger(Criteria criteria, String type, JsonObject data) {
        ITrigger trigger = triggerTypes.get(type).deserialize(data).setCriteria(criteria);
        CraftingEventsManager.onTriggerAdded(trigger);
        triggers.add(trigger);
        return trigger;
    }

    @Override
    public IReward newReward(Criteria criteria, String type, JsonObject data) {
        IReward reward = rewardTypes.get(type).deserialize(data).setCriteria(criteria);
        CraftingEventsManager.onRewardAdded(reward);
        rewards.add(reward);
        return reward;
    }

    @Override
    public ITrigger cloneTrigger(Criteria criteria, ITriggerType trigger) {
        ITrigger newTrigger = trigger.newInstance().setCriteria(criteria);
        CraftingEventsManager.onTriggerAdded(newTrigger);
        triggers.add(newTrigger);
        criteria.addTriggers(newTrigger);
        return newTrigger;
    }

    @Override
    public IReward cloneReward(Criteria criteria, IRewardType reward) {
        IReward newReward = reward.newInstance().setCriteria(criteria);
        CraftingEventsManager.onRewardAdded(newReward);
        rewards.add(newReward);
        criteria.addRewards(newReward);
        return newReward;
    }

    @Override
    public ICondition cloneCondition(ITrigger trigger, IConditionType condition) {
        ICondition newCondition = condition.newInstance().setTrigger(trigger);
        conditions.add(newCondition);
        trigger.addCondition(newCondition);
        return newCondition;
    }

    @Override
    public Criteria getCriteriaFromName(String name) {
        return criteria.get(name);
    }

    @Override
    public ITab getTabFromName(String name) {
        return tabs.get(name);
    }

    private static List<IResearch> technologies;

    @Override
    public Collection<Criteria> getCriteria() {
        return criteria.values();
    }

    /** Convenience methods for removals **/
    public static void removeCondition(ICondition condition) {
        conditions.remove(condition);
    }

    public static void removeTrigger(ITrigger trigger) {
        CraftingEventsManager.onTriggerRemoved(trigger);
        triggers.remove(trigger);
    }

    public static void removeReward(IReward reward) {
        CraftingEventsManager.onRewardRemoved(reward);
        reward.onRemoved();
        rewards.remove(reward);
    }

    public static void removeTab(ITab tab) {
        for (Criteria c: tab.getCriteria()) {
            removeCriteria(c.getUniqueName(), true);
        }
        
        tabs.remove(tab.getUniqueName());
    }
    
    public static void removeCriteria(String unique) {
        removeCriteria(unique, false);
    }

    public static void removeCriteria(String unique, boolean skipTab) {
        Criteria c = criteria.get(unique);
        //Remove the criteria from the tab
        if (!skipTab) {
            Iterator<Criteria> itC = c.getTabID().getCriteria().iterator();
            while (itC.hasNext()) {
                Criteria ic = itC.next();
                if (ic.equals(c)) {
                    itC.remove();
                }
            }
        }

        //Remove this from all the conflict lists
        for (Criteria conflict : c.getConflicts()) {
            Iterator<Criteria> it = conflict.getConflicts().iterator();
            while (it.hasNext()) {
                Criteria ct = it.next();
                if (ct.equals(c)) {
                    it.remove();
                }
            }
        }

        //Remove this from all the requirement lists
        for (Criteria require : criteria.values()) {
            Iterator<Criteria> it = require.getRequirements().iterator();
            while (it.hasNext()) {
                Criteria ct = it.next();
                if (ct.equals(c)) {
                    it.remove();
                }
            }
        }

        //Remove all rewards associated with this criteria
        for (IReward reward : c.getRewards()) {
            removeReward(reward);
        }

        //Remove all triggers associated with this criteria
        for (ITrigger trigger : c.getTriggers()) {
            removeTrigger(trigger);
        }

        //Remove it in general
        criteria.remove(unique);
    }

    //Returns the next unique string for this crafting api
    public static String getNextUnique() {
        return "" + System.currentTimeMillis();
    }

    @Override
    public ITriggerData newData(String string) {
        if (string.equalsIgnoreCase("count")) {
            return new DataCount();
        } else if (string.equalsIgnoreCase("boolean")) {
            return new DataBoolean();
        } else if (string.equalsIgnoreCase("crafting")) {
            return new DataCrafting();
        }
        
        return null;
    }
}
