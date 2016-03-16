package joshie.progression.criteria.rewards;

import java.util.List;
import java.util.UUID;

import joshie.progression.api.IGetterCallback;
import joshie.progression.api.ISetterCallback;
import joshie.progression.criteria.Criteria;
import joshie.progression.handlers.APIHandler;
import joshie.progression.player.PlayerTracker;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class RewardCriteria extends RewardBase implements IGetterCallback, ISetterCallback {
    private Criteria criteria = null;
    private String criteriaID = "";
    public String displayName = "";
    public boolean remove = false;

    public RewardCriteria() {
        super(new ItemStack(Items.golden_apple), "criteria", 0xFF99B3FF);
    }

    public Criteria getAssignedCriteria() {
        return APIHandler.getCriteriaFromName(criteriaID);
    }

    @Override
    public void reward(UUID uuid) {
        criteria = getAssignedCriteria();
        if (criteria == null) return; //Do not give the reward
        if (remove) {
            PlayerTracker.getServerPlayer(uuid).getMappings().fireAllTriggers("forced-remove", criteria);
        } else PlayerTracker.getServerPlayer(uuid).getMappings().fireAllTriggers("forced-complete", criteria, criteria.rewards);
    }

    @Override
    public String getField(String fieldName) {
        if (fieldName.equals(displayName)) {
            if (criteria == null) {
                criteria = APIHandler.getCriteriaFromName(criteriaID);
            }

            return criteria != null ? EnumChatFormatting.GREEN + displayName : EnumChatFormatting.RED + displayName;
        } else return null;
    }

    @Override
    public boolean setField(String fieldName, Object object) {
        String fieldValue = (String) object;
        if (fieldName.equals("displayName")) {
            displayName = fieldValue;

            try {
                for (Criteria c : APIHandler.getCriteria().values()) {
                    String display = c.displayName;
                    if (c.displayName.equals(displayName)) {
                        criteria = c;
                        criteriaID = c.uniqueName;
                        return true;
                    }
                }
            } catch (Exception e) {}
        }

        //FAILURE :D
        return false;
    }

    @Override
    public void addTooltip(List list) {
        if (criteria == null) {
            criteria = getAssignedCriteria();
        }

        if (criteria != null) {
            if (remove) {
                list.add("Remove " + criteria.displayName);
            } else list.add("Add " + criteria.displayName);
        }
    }
}
