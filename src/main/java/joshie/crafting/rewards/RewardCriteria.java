package joshie.crafting.rewards;

import java.util.List;
import java.util.UUID;

import joshie.crafting.CraftAPIRegistry;
import joshie.crafting.Criteria;
import joshie.crafting.api.DrawHelper;
import joshie.crafting.gui.SelectTextEdit;
import joshie.crafting.gui.SelectTextEdit.ITextEditable;
import joshie.crafting.helpers.ClientHelper;
import joshie.crafting.helpers.JSONHelper;
import joshie.crafting.json.Theme;
import joshie.crafting.player.PlayerTracker;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.Event.Result;

public class RewardCriteria extends RewardBase implements ITextEditable {
    private String criteriaID = "";
    private boolean remove = false;
    private Criteria criteria = null;

    public RewardCriteria() {
        super("criteria", 0xFF99B3FF);
    }

    @Override
    public void readFromJSON(JsonObject data) {
        criteriaID = JSONHelper.getString(data, "criteriaID", criteriaID);
        remove = JSONHelper.getBoolean(data, "remove", remove);
    }

    @Override
    public void writeToJSON(JsonObject data) {
        JSONHelper.setString(data, "criteriaID", criteriaID, "");
        JSONHelper.setBoolean(data, "remove", remove, false);
    }

    public Criteria getAssignedCriteria() {
        return CraftAPIRegistry.getCriteriaFromName(criteriaID);
    }

    @Override
    public void reward(UUID uuid) {
        criteria = getAssignedCriteria();
        if (criteria == null) return; //Do not give the reward
        if (remove) {
            PlayerTracker.getServerPlayer(uuid).getMappings().fireAllTriggers("forced-remove", criteria);
        } else PlayerTracker.getServerPlayer(uuid).getMappings().fireAllTriggers("forced-complete", criteria);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.golden_apple);
    }

    @Override
    public Result onClicked(int mouseX, int mouseY) {
        if (mouseX <= 84 && mouseX >= 1) {
            if (mouseY >= 17 && mouseY <= 33) {
                SelectTextEdit.INSTANCE.select(this);
                return Result.ALLOW;
            } else if (mouseY > 33 && mouseY <= 41) {
                remove = !remove;
                return Result.ALLOW;
            }
        }

        return Result.DEFAULT;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        int researchColor = Theme.INSTANCE.optionsFontColor;
        int booleanColor = Theme.INSTANCE.optionsFontColor;
        if (ClientHelper.canEdit()) {
            if (mouseX <= 84 && mouseX >= 1) {
                if (mouseY >= 17 && mouseY <= 33) researchColor = Theme.INSTANCE.optionsFontColorHover;
                if (mouseY > 33 && mouseY <= 41) booleanColor = Theme.INSTANCE.optionsFontColorHover;
            }
        }

        DrawHelper.triggerDraw.drawText("criteria: ", 4, 18, researchColor);
        EnumChatFormatting prefix = criteria != null? EnumChatFormatting.GREEN: EnumChatFormatting.RED;
        DrawHelper.triggerDraw.drawText(prefix + SelectTextEdit.INSTANCE.getText(this), 4, 26, researchColor);
        DrawHelper.triggerDraw.drawText("remove: " + remove, 4, 33, booleanColor);
    }

    private String displayName = null;

    @Override
    public String getTextField() {
        if (displayName == null) {
            criteria = CraftAPIRegistry.getCriteriaFromName(criteriaID);
            if (criteria == null) {
                displayName = "null";
            } else displayName = criteria.displayName;
        }

        return criteria != null ? displayName : displayName;
    }

    @Override
    public void setTextField(String text) {
        displayName = text;

        try {
            criteria = null;
            for (Criteria c : CraftAPIRegistry.criteria.values()) {
                String display = c.displayName;
                if (c.displayName.equals(displayName)) {
                    criteria = c;
                    criteriaID = c.uniqueName;
                    break;
                }
            }
        } catch (Exception e) {}
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
