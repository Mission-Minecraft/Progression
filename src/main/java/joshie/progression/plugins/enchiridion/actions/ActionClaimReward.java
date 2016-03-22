package joshie.progression.plugins.enchiridion.actions;

import com.google.gson.JsonObject;

import joshie.enchiridion.api.book.IButtonAction;
import joshie.progression.api.criteria.IProgressionCriteria;
import joshie.progression.handlers.APIHandler;
import joshie.progression.helpers.JSONHelper;
import joshie.progression.network.PacketClaimReward;
import joshie.progression.network.PacketHandler;

public class ActionClaimReward extends AbstractAction implements IButtonAction {
    public transient String displayName;
    public transient String criteriaID;
    public transient IProgressionCriteria criteria;
    public transient boolean randomReward;
    public transient int rewardPosition;

    public ActionClaimReward() {
        super("reward");
    }

    public ActionClaimReward(String displayName, String criteriaID, IProgressionCriteria criteria, int rewardPosition) {
        super("reward");
        this.displayName = displayName;
        this.criteriaID = criteriaID;
        this.criteria = criteria;
        this.rewardPosition = rewardPosition;
    }

    @Override
    public void initAction() {
        criteria = getCriteria();
    }

    @Override
    public IButtonAction copy() {
        return new ActionClaimReward(displayName, criteriaID, criteria, rewardPosition);
    }

    @Override
    public String[] getFieldNames() {
        initAction();
        return new String[] { "tooltip", "hoverText", "unhoveredText", "displayName", "rewardPosition", "randomReward" };
    }

    @Override
    public IButtonAction create() {
        return new ActionClaimReward("New Criteria", "", null, 1);
    }

    private IProgressionCriteria getCriteria() {
        if (criteria != null) {
            if (criteria.getDisplayName().equals(displayName)) return criteria;
        }

        //Attempt to grab the criteria based on the displayname
        for (IProgressionCriteria c : APIHandler.getCriteria().values()) {
            String display = c.getDisplayName();
            if (c.getDisplayName().equals(displayName)) {
                criteria = c;
                criteriaID = c.getUniqueName();
                return criteria;
            }
        }

        return null;
    }

    @Override
    public void performAction() {
        IProgressionCriteria criteria = getCriteria();
        if (criteria != null) {
            PacketHandler.sendToServer(new PacketClaimReward(criteria, rewardPosition - 1, randomReward));
        }
    }

    @Override
    public void readFromJson(JsonObject data) {
        super.readFromJson(data);
        criteriaID = JSONHelper.getString(data, "criteriaID", "");
        randomReward = JSONHelper.getBoolean(data, "randomReward", false);
        rewardPosition = JSONHelper.getInteger(data, "rewardPosition", 1);
    }

    @Override
    public void writeToJson(JsonObject data) {
        super.writeToJson(data);
        JSONHelper.setBoolean(data, "randomReward", randomReward, false);
        JSONHelper.setInteger(data, "rewardPosition", rewardPosition, 1);

        if (criteriaID != null) {
            JSONHelper.setString(data, "criteriaID", criteriaID, "");
        }
    }
}
