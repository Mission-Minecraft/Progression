package joshie.progression.criteria.conditions;

import joshie.progression.Progression;
import joshie.progression.api.criteria.IProgressionCriteria;
import joshie.progression.api.special.IGetterCallback;
import joshie.progression.api.special.IInit;
import joshie.progression.handlers.APIHandler;
import joshie.progression.items.ItemCriteria;
import joshie.progression.player.PlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.UUID;

public class ConditionHasCriteria extends ConditionBase implements IGetterCallback, IInit {
    private IProgressionCriteria criteria = null;
    private UUID criteriaID = UUID.randomUUID();
    public String displayName = "";

    public ConditionHasCriteria() {
        super(ItemCriteria.getStackFromMeta(ItemCriteria.ItemMeta.ifCriteriaCompleted), "criteria", 0xFF00FFBF);
    }

    @Override
    public void init() {
        try {
            for (IProgressionCriteria c : APIHandler.getCriteria().values()) {
                String display = c.getDisplayName();
                if (c.getDisplayName().equals(displayName)) {
                    criteria = c;
                    criteriaID = c.getUniqueID();
                    break;
                }
            }
        } catch (Exception e) {}
    }

    public IProgressionCriteria getAssignedCriteria() {
        return APIHandler.getCriteriaFromName(criteriaID);
    }

    @Override
    public boolean isSatisfied(World world, EntityPlayer player, UUID uuid) {
        if (criteria == null) criteria = getAssignedCriteria();
        if (criteria != null) {
            return PlayerTracker.getServerPlayer(uuid).getMappings().getCompletedCriteria().keySet().contains(criteria);
        }

        return false;
    }

    @Override
    public String getField(String fieldName) {
        return criteria != null ? EnumChatFormatting.GREEN + displayName : EnumChatFormatting.RED + displayName;
    }

    @Override
    public String getDescription() {
        if (criteria != null) {
            return Progression.format(getUnlocalisedName() + ".description", criteria.getDisplayName());
        } else return "BROKEN CRITERIA";
    }
}
